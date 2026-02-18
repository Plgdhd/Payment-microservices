package com.plgdhd.paymentservice.service;

import com.plgdhd.paymentservice.client.RandomApiClient;
import com.plgdhd.paymentservice.model.dto.PaymentDto;
import com.plgdhd.paymentservice.model.Payment;
import com.plgdhd.paymentservice.mapper.PaymentMapper;
import com.plgdhd.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private RandomApiClient randomApiClient;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDto paymentDto;
    private Payment paymentEntity;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        paymentDto = PaymentDto.builder()
                .id("1")
                .orderId(8L)
                .userId(3L)
                .paymentAmount(new BigDecimal("100.00"))
                .build();

        paymentEntity = Payment.builder()
                .id(null)
                .orderId(8L)
                .userId(2L)
                .paymentAmount(new BigDecimal("120.00"))
                .build();

        savedPayment = Payment.builder()
                .id("4")
                .orderId(8L)
                .userId(2L)
                .status("SUCCESS")
                .timestamp(Instant.now())
                .paymentAmount(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void testCreatePayment_WithEvenRandomNumber_ShouldReturnSuccess() {
        when(randomApiClient.fetchRandomNumber()).thenReturn(42);
        when(paymentMapper.toEntity(paymentDto)).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentDto);

        PaymentDto result = paymentService.createPayment(paymentDto);

        assertNotNull(result);
        verify(paymentMapper).toEntity(paymentDto);
        verify(paymentRepository).save(any(Payment.class));
        verify(randomApiClient).fetchRandomNumber();
        verify(paymentMapper).toDto(savedPayment);
    }

    @Test
    void testCreatePayment_WithOddRandomNumber_ShouldReturnFailed() {
        when(randomApiClient.fetchRandomNumber()).thenReturn(41);
        when(paymentMapper.toEntity(paymentDto)).thenReturn(paymentEntity);

        Payment failedPayment = Payment.builder()
                .id("payment-789")
                .orderId(12L)
                .userId(5L)
                .status("FAILED")
                .timestamp(Instant.now())
                .paymentAmount(new BigDecimal("100.00"))
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(failedPayment);
        when(paymentMapper.toDto(failedPayment)).thenReturn(paymentDto);

        PaymentDto result = paymentService.createPayment(paymentDto);

        assertNotNull(result);
        verify(randomApiClient).fetchRandomNumber();
    }

    @Test
    void testGetPaymentsByOrderId() {
        Long orderId = 12L;
        List<Payment> payments = Arrays.asList(savedPayment);

        when(paymentRepository.findByOrderId(orderId)).thenReturn(payments);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentDto);

        List<PaymentDto> result = paymentService.getPaymentsByOrderId(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findByOrderId(orderId);
        verify(paymentMapper).toDto(savedPayment);
    }

    @Test
    void testGetPaymentsByUserId() {
        Long userId = 2L;
        List<Payment> payments = Arrays.asList(savedPayment);

        when(paymentRepository.findByUserId(userId)).thenReturn(payments);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentDto);

        List<PaymentDto> result = paymentService.getPaymentsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findByUserId(userId);
        verify(paymentMapper).toDto(savedPayment);
    }

    @Test
    void testGetPaymentsByStatuses() {
        List<String> statuses = Arrays.asList("SUCCESS", "FAILED");
        List<Payment> payments = Arrays.asList(savedPayment);

        when(paymentRepository.findByStatusIn(statuses)).thenReturn(payments);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentDto);

        List<PaymentDto> result = paymentService.getPaymentsByStatuses(statuses);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findByStatusIn(statuses);
        verify(paymentMapper).toDto(savedPayment);
    }

    @Test
    void testGetPaymentsByDatePeriod() {
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();
        List<Payment> payments = Arrays.asList(savedPayment);

        when(paymentRepository.findByTimestampBetween(from, to)).thenReturn(payments);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentDto);

        List<PaymentDto> result = paymentService.getPaymentsByDatePeriod(from, to);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findByTimestampBetween(from, to);
        verify(paymentMapper).toDto(savedPayment);
    }

    @Test
    void testGetTotalSumForPeriod_WithResults() {
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();
        PaymentRepository.TotalSum totalSum = () -> new BigDecimal("250.00");

        when(paymentRepository.getTotalSumForPeriod(from, to))
                .thenReturn(Collections.singletonList(totalSum));

        BigDecimal result = paymentService.getTotalSumForPeriod(from, to);

        assertNotNull(result);
        assertEquals(new BigDecimal("250.00"), result);
        verify(paymentRepository).getTotalSumForPeriod(from, to);
    }

    @Test
    void testGetTotalSumForPeriod_WithEmptyResults() {
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();

        when(paymentRepository.getTotalSumForPeriod(from, to))
                .thenReturn(Collections.emptyList());

        BigDecimal result = paymentService.getTotalSumForPeriod(from, to);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(paymentRepository).getTotalSumForPeriod(from, to);
    }

    @Test
    void testGetTotalSumForPeriod_WithNullTotal() {
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();

        PaymentRepository.TotalSum totalSum = () -> null;

        when(paymentRepository.getTotalSumForPeriod(from, to))
                .thenReturn(Collections.singletonList(totalSum));

        BigDecimal result = paymentService.getTotalSumForPeriod(from, to);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(paymentRepository).getTotalSumForPeriod(from, to);
    }
}


