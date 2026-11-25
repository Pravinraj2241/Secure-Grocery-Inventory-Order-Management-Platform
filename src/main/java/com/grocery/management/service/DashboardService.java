package com.grocery.management.service;

import com.grocery.management.dto.DashboardSummary;
import com.grocery.management.entity.OrderStatus;
import com.grocery.management.repository.CustomerRepository;
import com.grocery.management.repository.OrderRepository;
import com.grocery.management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.grocery.management.service.DemandForecastingService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
private final DemandForecastingService forecastService;

    public DashboardSummary getSummary() {
        BigDecimal totalRevenue = orderRepository.findByStatus(OrderStatus.COMPLETED).stream()
                .map(o -> o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardSummary.builder()
                .totalProducts(productRepository.count())
                .lowStockProducts(productRepository.findLowStockProducts().size())
                .totalCustomers(customerRepository.count())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.findByStatus(OrderStatus.PENDING).size())
                .totalRevenue(totalRevenue)
                .build();
public DemandForecastingService.DemandForecast getProductForecast(Long productId) {
        return forecastService.predictDemand(productId).orElseGet(() ->
                new DemandForecastingService.DemandForecast(productId, "Unknown", BigDecimal.ZERO, 0, "Unknown"));
    }
}
