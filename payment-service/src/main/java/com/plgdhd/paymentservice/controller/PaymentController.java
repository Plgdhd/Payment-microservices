package com.plgdhd.paymentservice.controller;

import com.plgdhd.paymentservice.model.dto.PaymentDto;
import com.plgdhd.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

//    @PostMapping
//    public PaymentDto create(@RequestBody @Valid PaymentDto paymentDto) {
//        return paymentService.createPayment(paymentDto);
//    }

    @GetMapping("/order/{orderId}")
    public List<PaymentDto> findByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrderId(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<PaymentDto> findByUser(@PathVariable Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @GetMapping("/status")
    public List<PaymentDto> findByStatuses(@RequestParam List<String> statuses) {
        return paymentService.getPaymentsByStatuses(statuses);
    }

    @GetMapping("/period")
    public List<PaymentDto> findByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return paymentService.getPaymentsByDatePeriod(from, to);
    }

    @GetMapping("/sum")
    public BigDecimal getTotalSum(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return paymentService.getTotalSumForPeriod(from, to);
    }
}
