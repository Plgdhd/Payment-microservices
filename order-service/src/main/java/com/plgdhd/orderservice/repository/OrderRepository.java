package com.plgdhd.orderservice.repository;

import com.plgdhd.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user.email = :email")
    Page<Order> findByUserEmail(@Param("email") String email, Pageable pageable);
}