package com.plgdhd.orderservice.repository;

import com.plgdhd.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.item WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithItem(@Param("orderId") Long orderId);

    void deleteByOrderId(Long id);
}
