package com.plgdhd.orderservice.controller;

import com.plgdhd.orderservice.model.dto.OrderCreateDTO;
import com.plgdhd.orderservice.model.dto.OrderResponseDTO;
import com.plgdhd.orderservice.repository.OrderRepository;
import com.plgdhd.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO order) {
        OrderResponseDTO response = orderService.createOrder(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrdersById(@PathVariable Long id){
        OrderResponseDTO response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByEmail(@RequestParam String email,
                                                 Pageable pageable){
        Page<OrderResponseDTO> response = orderService.getOrdersByEmail(email, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id,
                                            @RequestParam String email){
        orderService.cancelOrder(id, email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id,
                                                @RequestParam String email){
        orderService.deleteOrder(id, email);
        return ResponseEntity.noContent().build();
    }
}
