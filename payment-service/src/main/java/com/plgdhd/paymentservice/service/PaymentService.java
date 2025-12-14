package com.plgdhd.paymentservice.service;

import com.plgdhd.paymentservice.client.RandomApiClient;
import com.plgdhd.paymentservice.model.dto.PaymentDto;
import com.plgdhd.paymentservice.model.Payment;
import com.plgdhd.paymentservice.mapper.PaymentMapper;
import com.plgdhd.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomApiClient randomApiClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper,
                          RandomApiClient randomApiClient) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.randomApiClient = randomApiClient;
    }

    @Transactional
    public PaymentDto createPayment(PaymentDto dto) {
        Payment entity = paymentMapper.toEntity(dto);
        entity.setTimestamp(Instant.now());
        entity.setStatus(fetchRandomStatus());
        return paymentMapper.toDto(paymentRepository.save(entity));
    }

    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream().map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream().map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getPaymentsByStatuses(List<String> statuses) {
        return paymentRepository.findByStatusIn(statuses)
                .stream().map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getPaymentsByDatePeriod(Instant from, Instant to) {
        return paymentRepository.findByTimestampBetween(from, to)
                .stream().map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalSumForPeriod(Instant from, Instant to) {
        List<PaymentRepository.TotalSum> result = paymentRepository.getTotalSumForPeriod(from, to);
        return (result != null && !result.isEmpty() && result.get(0).getTotal() != null)
                ? result.get(0).getTotal() : BigDecimal.ZERO;
    }

    private String fetchRandomStatus() {
        Integer random = randomApiClient.fetchRandomNumber();
        //TODO make throw enum
        return (random % 2 == 0) ? "SUCCESS" : "FAILED";
    }
}
