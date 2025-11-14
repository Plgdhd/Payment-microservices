package com.plgdhd.orderservice.service;

import com.plgdhd.orderservice.client.UserServiceClient;
import com.plgdhd.orderservice.common.OrderStatus;
import com.plgdhd.orderservice.exception.ItemNotFoundException;
import com.plgdhd.orderservice.exception.OrderNotFoundException;
import com.plgdhd.orderservice.mapper.OrderMapper;
import com.plgdhd.orderservice.mapper.UserMapper;
import com.plgdhd.orderservice.model.Item;
import com.plgdhd.orderservice.model.Order;
import com.plgdhd.orderservice.model.OrderItem;
import com.plgdhd.orderservice.model.User;
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
    @Mock private UserMapper userMapper;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks private OrderService orderService;

    private User testUser;
    private Item testItem;
    private Order testOrder;
    private OrderItem testOrderItem;
    private OrderCreateDTO createDTO;
    private OrderResponseDTO responseDTO;
    private UserInfoDTO userInfoDTO;

    private final Long ORDER_ID = 1L;
    private final Long ITEM_ID = 100L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("alex@plgdhd.com");
        testUser.setName("Alex");
        testUser.setSurname("Plgdhd");
        testUser.setBirthDate(LocalDate.of(1995, 5, 15));

        testItem = Item.builder()
                .id(ITEM_ID)
                .name("Laptop")
                .price(999L)
                .build();

        testOrder = new Order();
        testOrder.setId(ORDER_ID);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreationDate(LocalDate.now());

        testOrderItem = new OrderItem();
        testOrderItem.setId(10L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setItem(testItem);
        testOrderItem.setQuantity(2);

        createDTO = new OrderCreateDTO();
        createDTO.setUserEmail("alex@plgdhd.com");
        createDTO.setOrderItems(List.of(new OrderItemCreateDTO(ITEM_ID, 2)));

        userInfoDTO = new UserInfoDTO(1L, "Alex", "Plgdhd", LocalDate.of(1995, 5, 15), "alex@plgdhd.com");

        responseDTO = new OrderResponseDTO();
        responseDTO.setId(ORDER_ID);
        responseDTO.setStatus("PENDING");
        responseDTO.setCreationDate(LocalDate.now());
        responseDTO.setUser(userInfoDTO);
        responseDTO.setItems(List.of(
                new OrderItemResponseDTO(10L, "Laptop", 2L,2)
        ));
    }

    @Test
    void createOrder_success() {
        when(userServiceClient.getUserByEmail("alex@plgdhd.com")).thenReturn(userInfoDTO);
        when(userMapper.toEntity(userInfoDTO)).thenReturn(testUser);
        when(orderMapper.toEntity(createDTO)).thenReturn(new Order());
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(testItem));
        when(orderMapper.toOrderItemEntity(any())).thenReturn(new OrderItem());
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(ORDER_ID);
            o.setUser(testUser);
            o.setStatus(OrderStatus.PENDING);
            o.setCreationDate(LocalDate.now());
            return o;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(i -> {
            OrderItem oi = i.getArgument(0);
            oi.setId(10L);
            oi.setOrder(testOrder);
            oi.setItem(testItem);
            oi.setQuantity(2);
            return oi;
        });
        when(orderMapper.toResponseDTO(any(Order.class), eq(userInfoDTO))).thenReturn(responseDTO);
        when(orderMapper.toOrderItemResponseDTO(any(OrderItem.class))).thenReturn(responseDTO.getItems().get(0));

        OrderResponseDTO result = orderService.createOrder(createDTO);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getUser().getEmail()).isEqualTo("alex@plgdhd.com");
        assertThat(result.getItems()).hasSize(1);

        verify(userServiceClient).getUserByEmail("alex@plgdhd.com");
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

//    @Test
//    void createOrder_fallback() {
//        Order emptyOrder = new Order();
//        when(orderMapper.toEntity(createDTO)).thenReturn(emptyOrder);
//
//        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
//            Order o = invocation.getArgument(0);
//            o.setId(ORDER_ID);
//            o.setStatus(OrderStatus.PENDING);
//            o.setCreationDate(LocalDate.now());
//            return o;
//        });
//
//        UserInfoDTO fallbackUser = new UserInfoDTO(null, "N/A", "N/A", null, "alex@plgdhd.com");
//        when(orderMapper.toResponseDTO(any(Order.class), eq(fallbackUser))).thenReturn(
//                OrderResponseDTO.builder()
//                        .id(ORDER_ID)
//                        .status("PENDING")
//                        .creationDate(LocalDate.now())
//                        .user(fallbackUser)
//                        .items(List.of())
//                        .build()
//        );
//
//        OrderResponseDTO result = orderService.createOrderFallback(createDTO, new RuntimeException("Service down"));
//
//        assertThat(result.getId()).isEqualTo(ORDER_ID);
//        assertThat(result.getUser().getName()).isEqualTo("N/A");
//        assertThat(result.getUser().getEmail()).isEqualTo("alex@plgdhd.com");
//        assertThat(result.getItems()).isEmpty();
//
//        verify(orderRepository).save(any(Order.class));
//        verify(orderMapper).toResponseDTO(any(), eq(fallbackUser));
//    }
    @Test
    void getOrderById_success() {
        when(orderRepository.findByIdWithUser(ORDER_ID)).thenReturn(Optional.of(testOrder));
        when(userServiceClient.getUserByEmail("alex@plgdhd.com")).thenReturn(userInfoDTO);
        when(orderItemRepository.findByOrderIdWithItem(ORDER_ID)).thenReturn(List.of(testOrderItem));
        when(orderMapper.toOrderItemResponseDTO(testOrderItem)).thenReturn(responseDTO.getItems().get(0));
        when(orderMapper.toResponseDTO(testOrder, userInfoDTO)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.getOrderById(ORDER_ID);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getUser().getName()).isEqualTo("Alex");
    }

    @Test
    void getOrderById_fallback() {
        when(orderRepository.findByIdWithUser(ORDER_ID)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderIdWithItem(ORDER_ID)).thenReturn(List.of(testOrderItem));


        UserInfoDTO fallbackUser = new UserInfoDTO(null, "N/A", "N/A", null, "alex@plgdhd.com");
        when(orderMapper.toResponseDTO(eq(testOrder), eq(fallbackUser))).thenReturn(
                OrderResponseDTO.builder()
                        .id(ORDER_ID)
                        .status("PENDING")
                        .user(fallbackUser)
                        .build()
        );

        OrderResponseDTO result = orderService.getOrderByIdFallback(ORDER_ID, new RuntimeException("Down"));

        assertThat(result.getUser().getName()).isEqualTo("N/A");
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void getOrdersByEmail_success() {
        Page<Order> page = new PageImpl<>(List.of(testOrder), PageRequest.of(0, 10), 1);
        when(orderRepository.findByUserEmail("alex@plgdhd.com", PageRequest.of(0, 10))).thenReturn(page);
        when(userServiceClient.getUserByEmail("alex@plgdhd.com")).thenReturn(userInfoDTO);
        when(orderItemRepository.findByOrderIdWithItem(ORDER_ID)).thenReturn(List.of(testOrderItem));
        when(orderMapper.toOrderItemResponseDTO(testOrderItem)).thenReturn(responseDTO.getItems().get(0));
        when(orderMapper.toResponseDTO(testOrder, userInfoDTO)).thenReturn(responseDTO);

        Page<OrderResponseDTO> result = orderService.getOrdersByEmail("alex@plgdhd.com", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUser().getEmail()).isEqualTo("alex@plgdhd.com");
    }

    @Test
    void cancelOrder_success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

        orderService.cancelOrder(ORDER_ID, "alex@plgdhd.com");

        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void cancelOrder_notOwner_throwsException() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(ORDER_ID, "wrong@plgdhd.com"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Not your order");
    }

    @Test
    void deleteOrder_success() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(ORDER_ID, "alex@plgdhd.com");

        verify(orderItemRepository).deleteByOrderId(ORDER_ID);
        verify(orderRepository).delete(testOrder);
    }

    @Test
    void createOrder_itemNotFound_throwsException() {
        when(userServiceClient.getUserByEmail("alex@plgdhd.com")).thenReturn(userInfoDTO);
        when(userMapper.toEntity(userInfoDTO)).thenReturn(testUser);
        when(orderMapper.toEntity(createDTO)).thenReturn(new Order());
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(createDTO))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("Item not found");
    }
}