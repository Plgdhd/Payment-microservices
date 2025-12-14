package com.plgdhd.orderservice.service;

import com.plgdhd.orderservice.client.UserServiceClient;
import com.plgdhd.orderservice.common.OrderStatus;
import com.plgdhd.orderservice.exception.ItemNotFoundException;
import com.plgdhd.orderservice.exception.OrderNotFoundException;
import com.plgdhd.orderservice.mapper.OrderMapper;
import com.plgdhd.orderservice.model.Item;
import com.plgdhd.orderservice.model.Order;
import com.plgdhd.orderservice.model.OrderItem;
import com.plgdhd.orderservice.model.dto.*;
import com.plgdhd.orderservice.repository.ItemRepository;
import com.plgdhd.orderservice.repository.OrderItemRepository;
import com.plgdhd.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks private OrderService orderService;

    private Item testItem;
    private Order testOrder;
    private OrderItem testOrderItem;
    private OrderCreateDTO createDTO;
    private OrderResponseDTO responseDTO;
    private UserInfoDTO userInfoDTO;

    private final Long ORDER_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long ITEM_ID = 100L;
    private final String TEST_EMAIL = "alex@plgdhd.com";

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(ITEM_ID)
                .name("Laptop")
                .price(999L)
                .build();

        testOrder = new Order();
        testOrder.setId(ORDER_ID);
        testOrder.setUserId(USER_ID);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreationDate(LocalDate.now());

        testOrderItem = new OrderItem();
        testOrderItem.setId(10L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setItem(testItem);
        testOrderItem.setQuantity(BigDecimal.valueOf(2));

        createDTO = new OrderCreateDTO();
        createDTO.setUserId(USER_ID);
        createDTO.setUserEmail(TEST_EMAIL);
        createDTO.setOrderItems(List.of(new OrderItemCreateDTO(ITEM_ID, BigDecimal.valueOf(2))));

        userInfoDTO = new UserInfoDTO(USER_ID, "Alex", "Plgdhd", LocalDate.of(1995, 5, 15), TEST_EMAIL);

        responseDTO = new OrderResponseDTO();
        responseDTO.setId(ORDER_ID);
        responseDTO.setStatus("PENDING");
        responseDTO.setCreationDate(LocalDate.now());
        responseDTO.setUserInfo(userInfoDTO);
        responseDTO.setItems(List.of(
                new OrderItemResponseDTO(10L, "Laptop", 999L, BigDecimal.valueOf(2)) // Цена в DTO должна соответствовать Long
        ));
    }

    @Test
    void createOrder_success() {
        when(userServiceClient.getUserByEmail(TEST_EMAIL)).thenReturn(userInfoDTO);

        when(orderMapper.toEntity(createDTO)).thenReturn(new Order());
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(orderMapper.toOrderItemEntity(any())).thenReturn(new OrderItem());

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(ORDER_ID);
            o.setUserId(USER_ID);
            o.setStatus(OrderStatus.PENDING);
            o.setCreationDate(LocalDate.now());
            return o;
        });

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(i -> {
            OrderItem oi = i.getArgument(0);
            oi.setId(10L);
            oi.setOrder(testOrder);
            oi.setItem(testItem);
            oi.setQuantity(BigDecimal.valueOf(2));
            return oi;
        });
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(responseDTO);
        when(orderMapper.toOrderItemResponseDTO(any(OrderItem.class))).thenReturn(responseDTO.getItems().get(0));

        OrderResponseDTO result = orderService.createOrder(createDTO);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getUserInfo().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getItems()).hasSize(1);

        verify(orderRepository).save(argThat(order -> order.getUserId().equals(USER_ID)));
        verify(userServiceClient).getUserByEmail(TEST_EMAIL);
        verify(orderItemRepository).save(any(OrderItem.class));
    }


    @Test
    void getOrderById_success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

        when(orderItemRepository.findByOrderIdWithItem(ORDER_ID)).thenReturn(List.of(testOrderItem));
        when(orderMapper.toOrderItemResponseDTO(testOrderItem)).thenReturn(responseDTO.getItems().get(0));
        when(orderMapper.toResponseDTO(testOrder)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.getOrderById(ORDER_ID);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void getOrderById_throwsOrderNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(ORDER_ID))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");
    }


    @Test
    void getOrdersByEmail_success() {
        Page<Order> page = new PageImpl<>(List.of(testOrder), PageRequest.of(0, 10), 1);

        when(userServiceClient.getUserByEmail(TEST_EMAIL)).thenReturn(userInfoDTO);
        when(orderRepository.findByUserId(USER_ID, PageRequest.of(0, 10))).thenReturn(page);
        when(orderItemRepository.findByOrderIdWithItem(ORDER_ID)).thenReturn(List.of(testOrderItem));
        when(orderMapper.toOrderItemResponseDTO(testOrderItem)).thenReturn(responseDTO.getItems().get(0));
        when(orderMapper.toResponseDTO(testOrder)).thenReturn(responseDTO);

        Page<OrderResponseDTO> result = orderService.getOrdersByEmail(TEST_EMAIL, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUserInfo().getEmail()).isEqualTo(TEST_EMAIL);

        verify(userServiceClient).getUserByEmail(TEST_EMAIL);
        verify(orderRepository).findByUserId(USER_ID, PageRequest.of(0, 10));
    }

    @Test
    void cancelOrder_success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
        when(userServiceClient.getUserByEmail(TEST_EMAIL)).thenReturn(userInfoDTO);

        orderService.cancelOrder(ORDER_ID, TEST_EMAIL);

        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).findById(ORDER_ID);
        verify(userServiceClient).getUserByEmail(TEST_EMAIL);
    }

    @Test
    void cancelOrder_notOwner_throwsException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
        UserInfoDTO wrongUser = new UserInfoDTO(99L, "Wrong", "User", null, "wrong@plgdhd.com");
        when(userServiceClient.getUserByEmail("wrong@plgdhd.com")).thenReturn(wrongUser);

        assertThatThrownBy(() -> orderService.cancelOrder(ORDER_ID, "wrong@plgdhd.com"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Not your order");
    }

    @Test
    void deleteOrder_success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
        when(userServiceClient.getUserByEmail(TEST_EMAIL)).thenReturn(userInfoDTO);

        orderService.deleteOrder(ORDER_ID, TEST_EMAIL);

        verify(userServiceClient).getUserByEmail(TEST_EMAIL);
        verify(orderItemRepository).deleteByOrderId(ORDER_ID);
        verify(orderRepository).delete(testOrder);
    }

    @Test
    void createOrder_itemNotFound_throwsException() {
        when(userServiceClient.getUserByEmail(TEST_EMAIL)).thenReturn(userInfoDTO);
        when(orderMapper.toEntity(createDTO)).thenReturn(new Order());
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(createDTO))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("Item not found");

        verify(userServiceClient).getUserByEmail(TEST_EMAIL);
    }
}