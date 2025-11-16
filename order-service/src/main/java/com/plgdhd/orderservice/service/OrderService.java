package com.plgdhd.orderservice.service;

import com.plgdhd.orderservice.client.UserServiceClient;
import com.plgdhd.orderservice.common.OrderStatus;
import com.plgdhd.orderservice.exception.ItemNotFoundException;
import com.plgdhd.orderservice.exception.OrderNotFoundException;
import com.plgdhd.orderservice.model.dto.*;
import com.plgdhd.orderservice.mapper.OrderMapper;
import com.plgdhd.orderservice.model.*;
import com.plgdhd.orderservice.repository.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ItemRepository itemRepository,
                        OrderMapper orderMapper,
                        UserServiceClient userServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.userServiceClient = userServiceClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "createOrderFallback")
    @Retry(name = "userService")
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO) {
        UserInfoDTO userInfo = userServiceClient.getUserByEmail(orderCreateDTO.getUserEmail());

        Order order = orderMapper.toEntity(orderCreateDTO);
        order.setUserId(userInfo.getId());
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDate(LocalDate.now());
        order = orderRepository.save(order);

        List<OrderItemResponseDTO> orderItemsDTOs = new ArrayList<>();

        for (OrderItemCreateDTO orderItemDTO : orderCreateDTO.getOrderItems()) {
            Item item = itemRepository.findById(orderItemDTO.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found"));

            OrderItem orderItem = orderMapper.toOrderItemEntity(orderItemDTO);
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItemRepository.save(orderItem);


            orderItemsDTOs.add(orderMapper.toOrderItemResponseDTO(orderItem));
        }

        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        response.setItems(orderItemsDTOs);
        response.setUserInfo(userInfo);
        return response;
    }

    public OrderResponseDTO createOrderFallback(OrderCreateDTO orderCreateDTO, Throwable t) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDate(LocalDate.now());
        order = orderRepository.save(order);

        UserInfoDTO fallback = new UserInfoDTO(null, "N/A", "N/A", null, orderCreateDTO.getUserEmail());
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        response.setUserInfo(fallback);
        return response;
    }

//    @CircuitBreaker(name = "userService", fallbackMethod = "getOrderByIdFallback")
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));


        //TODO userInfo returning
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithItem(id);

        List<OrderItemResponseDTO> itemDtos = new ArrayList<>();

        for (OrderItem oi : orderItems) {
            OrderItemResponseDTO orderItemResponseDTO = orderMapper.toOrderItemResponseDTO(oi);
            itemDtos.add(orderItemResponseDTO);
        }

        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        response.setItems(itemDtos);
        return response;
    }

    public OrderResponseDTO getOrderByIdFallback(Long id, Throwable t) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithItem(id);
        List<OrderItemResponseDTO> itemDtos = new ArrayList<>();

        for (OrderItem oi : orderItems) {
            OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(oi);
            itemDtos.add(dto);
        }

        UserInfoDTO fallback = new UserInfoDTO(null, "N/A", "N/A", null, null);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        response.setUserInfo(fallback);
        response.setItems(itemDtos);
        return response;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getOrdersByEmailFallback")
    public Page<OrderResponseDTO> getOrdersByEmail(String email, Pageable pageable) {

        UserInfoDTO userInfo = userServiceClient.getUserByEmail(email);

        Page<Order> orders = orderRepository.findByUserId(userInfo.getId(), pageable);
        return orders.map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderIdWithItem(order.getId());

            List<OrderItemResponseDTO> itemDtos = items.stream()
                    .map(oi -> {
                        OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(oi);
                        return dto;
                    })
                    .toList();

            OrderResponseDTO response = orderMapper.toResponseDTO(order);
            response.setUserInfo(userInfo);
            response.setItems(itemDtos);
            return response;
        });
    }

    public Page<OrderResponseDTO> getOrdersByEmailFallback(String email, Pageable pageable, Throwable t) {
        UserInfoDTO fallback = new UserInfoDTO(null, "N/A", "N/A", null, email);
        //TODO fallback fix
        Page<Order> orders = orderRepository.findByUserId(fallback.getId(), pageable);

        return orders.map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderIdWithItem(order.getId());

            List<OrderItemResponseDTO> itemDtos = items.stream()
                    .map(oi -> {
                        OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(oi);
                        return dto;
                    })
                    .toList();

            OrderResponseDTO response = orderMapper.toResponseDTO(order);
            response.setUserInfo(fallback);
            response.setItems(itemDtos);
            return response;
        });
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "cancelOrderFallback")
    @Transactional
    public void cancelOrder(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        UserInfoDTO userInfo = userServiceClient.getUserByEmail(userEmail);


        if (!order.getUserId().equals(userInfo.getId())) {
            throw new OrderNotFoundException("Not your order");
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    public void cancelOrderFallback(Long id, Throwable t) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "deleteOrderFallback")
    @Transactional
    public void deleteOrder(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Order not found"));
        UserInfoDTO userInfo = userServiceClient.getUserByEmail(userEmail);

        if (!order.getUserId().equals(userInfo.getId())) {
            throw new OrderNotFoundException("Not your order");
        }

        orderItemRepository.deleteByOrderId(id);
        orderRepository.delete(order);
    }

    public void deleteOrderFallback(Long id, Throwable t) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        orderItemRepository.deleteByOrderId(id);
        orderRepository.delete(order);
    }
}