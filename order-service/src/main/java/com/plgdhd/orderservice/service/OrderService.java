// OrderService.java
package com.plgdhd.orderservice.service;

import com.plgdhd.orderservice.client.UserServiceClient;
import com.plgdhd.orderservice.common.OrderStatus;
import com.plgdhd.orderservice.exception.ItemNotFoundException;
import com.plgdhd.orderservice.exception.OrderNotFoundException;
import com.plgdhd.orderservice.mapper.UserMapper;
import com.plgdhd.orderservice.model.dto.*;
import com.plgdhd.orderservice.mapper.OrderMapper;
import com.plgdhd.orderservice.model.*;
import com.plgdhd.orderservice.repository.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final UserServiceClient userServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ItemRepository itemRepository,
                        OrderMapper orderMapper,
                        UserMapper userMapper,
                        UserServiceClient userServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.userServiceClient = userServiceClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "createOrderFallback")
    @Retry(name = "userService")
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO) {
        UserInfoDTO userInfo = userServiceClient.getUserByEmail(orderCreateDTO.getUserEmail());
        User user = userMapper.toEntity(userInfo);

        Order order = orderMapper.toEntity(orderCreateDTO);
        order.setUser(user);
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

        OrderResponseDTO response = orderMapper.toResponseDTO(order, userInfo);
        response.setItems(orderItemsDTOs);
        response.setUser(userInfo);
        return response;
    }

    public OrderResponseDTO createOrderFallback(OrderCreateDTO orderCreateDTO, Throwable t) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDate(LocalDate.now());
        order = orderRepository.save(order);

        UserInfoDTO fallback = new UserInfoDTO(null, "N/A", "N/A", null, orderCreateDTO.getUserEmail());
        OrderResponseDTO response = orderMapper.toResponseDTO(order, fallback);
        return response;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getOrderByIdFallback")
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        UserInfoDTO userInfo = userServiceClient.getUserByEmail(order.getUser().getEmail());
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithItem(id);

        List<OrderItemResponseDTO> itemDtos = new ArrayList<>();

        for (OrderItem oi : orderItems) {
            OrderItemResponseDTO orderItemResponseDTO = orderMapper.toOrderItemResponseDTO(oi);
            itemDtos.add(orderItemResponseDTO);
        }

        OrderResponseDTO response = orderMapper.toResponseDTO(order, userInfo);
        response.setItems(itemDtos);
        return response;
    }

    public OrderResponseDTO getOrderByIdFallback(Long id, Throwable t) {
        Order order = orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithItem(id);
        List<OrderItemResponseDTO> itemDtos = new ArrayList<>();

        for (OrderItem oi : orderItems) {
            OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(oi);
            itemDtos.add(dto);
        }

        UserInfoDTO fallback = new UserInfoDTO(null, "N/A", "N/A", null, order.getUser().getEmail());
        OrderResponseDTO response = orderMapper.toResponseDTO(order, fallback);
        response.setItems(itemDtos);
        return response;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getOrdersByEmailFallback")
    public Page<OrderResponseDTO> getOrdersByEmail(String email, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserEmail(email, pageable);
        UserInfoDTO userInfo = userServiceClient.getUserByEmail(email);
        return orders.map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderIdWithItem(order.getId());

            List<OrderItemResponseDTO> itemDtos = items.stream()
                    .map(oi -> {
                        OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(oi);
                        return dto;
                    })
                    .toList();

            OrderResponseDTO response = orderMapper.toResponseDTO(order, userInfo);
            response.setItems(itemDtos);
            return response;
        });
    }

    public Page<OrderResponseDTO> getOrdersByEmailFallback(String email, Pageable pageable, Throwable t) {
        Page<Order> orders = orderRepository.findByUserEmail(email, pageable);
        UserInfoDTO fallback = new UserInfoDTO(null, "N/A", "N/A", null, email);

        return orders.map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderIdWithItem(order.getId());

            List<OrderItemResponseDTO> itemDtos = items.stream()
                    .map(oi -> {
                        OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(oi);
                        return dto;
                    })
                    .toList();

            OrderResponseDTO response = orderMapper.toResponseDTO(order, fallback);
            response.setItems(itemDtos);
            return response;
        });
    }

    @Transactional
    public void cancelOrder(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new OrderNotFoundException("Not your order");
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Transactional
    public void deleteOrder(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new OrderNotFoundException("Not your order");
        }

        orderItemRepository.deleteByOrderId(id);
        orderRepository.delete(order);
    }
}