package com.grocery.management.repository;

import com.grocery.management.entity.Order;
import com.grocery.management.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId AND o.orderDate BETWEEN :start AND :end")
    List<Order> findByProductIdAndOrderDateBetween(Long productId, LocalDateTime start, LocalDateTime end);
}
