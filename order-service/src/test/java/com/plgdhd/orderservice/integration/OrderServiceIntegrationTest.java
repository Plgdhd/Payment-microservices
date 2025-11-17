package com.plgdhd.orderservice.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.plgdhd.orderservice.common.OrderStatus;
import com.plgdhd.orderservice.model.Item;
import com.plgdhd.orderservice.model.dto.OrderCreateDTO;
import com.plgdhd.orderservice.model.dto.OrderItemCreateDTO;
import com.plgdhd.orderservice.model.dto.OrderResponseDTO;
import com.plgdhd.orderservice.repository.ItemRepository;
import com.plgdhd.orderservice.repository.OrderRepository;
import com.plgdhd.orderservice.repository.OrderItemRepository;
import com.plgdhd.orderservice.service.OrderService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class OrderServiceIntegrationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void setWireMockUrl(DynamicPropertyRegistry registry) {
        registry.add("user.service.url", () ->
                "http://localhost:" + wireMockServer.port()
        );
    }

    private OrderService orderService;

    private OrderRepository orderRepository;

    private ItemRepository itemRepository;
    private OrderItemRepository orderItemRepository;

    private final Long USER_ID = 1L;
    private final String TEST_EMAIL = "alex@plgdhd.com";

    private Long ITEM_ID;

    private final String ENCODED_EMAIL_PATH = "/api/users/by-email?email=alex%40plgdhd.com";

    @Autowired
    public OrderServiceIntegrationTest(OrderService orderService, ItemRepository itemRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.itemRepository = itemRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }
    @BeforeEach
    void setupDatabase() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        Item testItem = Item.builder()
                .name("Integration Test Item")
                .price(500L)
                .build();

        Item savedItem = itemRepository.saveAndFlush(testItem);
        ITEM_ID = savedItem.getId();

        WireMock.resetAllRequests();
    }

    @Test
    void createOrder_fullIntegration_success() {
        String userInfoJson = String.format("""
            {
                "id": %d,
                "firstName": "Alex",
                "lastName": "Plgdhd",
                "dateOfBirth": "1995-05-15",
                "email": "%s"
            }
        """, USER_ID, TEST_EMAIL);

        stubFor(get(urlEqualTo(ENCODED_EMAIL_PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(userInfoJson)));

        OrderCreateDTO createDTO = new OrderCreateDTO(
                USER_ID,
                TEST_EMAIL,
                List.of(new OrderItemCreateDTO(ITEM_ID, 2))
        );

        OrderResponseDTO result = orderService.createOrder(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUserInfo().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING.toString());
        verify(getRequestedFor(urlEqualTo(ENCODED_EMAIL_PATH)));

        assertThat(orderRepository.findById(result.getId())).isPresent();
    }


}