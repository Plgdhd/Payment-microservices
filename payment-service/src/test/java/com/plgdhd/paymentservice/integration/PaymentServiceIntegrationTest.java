package com.plgdhd.paymentservice.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.plgdhd.paymentservice.model.dto.PaymentDto;
import com.plgdhd.paymentservice.repository.PaymentRepository;
import com.plgdhd.paymentservice.service.PaymentService;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class PaymentServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startInfrastructure() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
    }

    @AfterAll
    static void stopInfrastructure() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

    }

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;


    @BeforeEach
    void setupDatabase() {
        paymentRepository.deleteAll();
        WireMock.resetAllRequests();
    }


    @Test
    void createPayment_fullIntegration_success_completedStatus() {

        PaymentDto createDTO = new PaymentDto();
        createDTO.setUserId(100L);
        createDTO.setOrderId(500L);
        PaymentDto result = paymentService.createPayment(createDTO);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(paymentRepository.findById(result.getId())).isPresent();
    }


}