package com.grocery.management.service;

import com.grocery.management.entity.Order;
import com.grocery.management.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandForecastingService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Optional<DemandForecast> predictDemand(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    LocalDateTime endDateTime = LocalDateTime.now();
                    LocalDateTime startDateTime = LocalDateTime.now().minusDays(90);
                    List<Order> history = orderRepository.findByProductIdAndOrderDateBetween(
                            productId, startDateTime, endDateTime);
                    if (history.isEmpty()) {
                        return new DemandForecast(productId, product.getName(),
                                BigDecimal.ZERO, 0, "No order history");
                    }
                    long totalQuantity = 0;
                    long weightedSum = 0;
                    long totalDays = 0;
                    for (Order entry : history) {
                        long daysAgo = ChronoUnit.DAYS.between(entry.getOrderDate().toLocalDate(), LocalDate.now());
                        long quantity = entry.getQuantity();
                        totalQuantity += quantity;
                        weightedSum += quantity * (30 - daysAgo);
                        totalDays += daysAgo;
                    }
                    BigDecimal avgDailyDemand;
                    if (totalDays > 0) {
                        avgDailyDemand = BigDecimal.valueOf(weightedSum)
                                .divide(BigDecimal.valueOf(totalDays), 2, BigDecimal.ROUND_HALF_UP);
                    } else {
                        avgDailyDemand = BigDecimal.valueOf(totalQuantity)
                                .divide(BigDecimal.valueOf(90), 2, BigDecimal.ROUND_HALF_UP);
                    }
                    BigDecimal predicted30Day = avgDailyDemand.multiply(BigDecimal.valueOf(30));
                    int dataPoints = history.size();
                    String confidence;
                    if (dataPoints >= 60) {
                        confidence = "High";
                    } else if (dataPoints >= 30) {
                        confidence = "Medium";
                    } else if (dataPoints > 0) {
                        confidence = "Low";
                    } else {
                        confidence = "Very Low";
                    }
                    return new DemandForecast(productId, product.getName(),
                            predicted30Day, dataPoints, confidence);
                });
    }

    public record OrderHistory(LocalDate orderDate, long quantity) {}

    public record DemandForecast(
            Long productId,
            String productName,
            BigDecimal predicted30Day,
            int dataPoints,
            String confidence
    ) {
        private int currentQuantity;
        private int reorderLevel;

        public int getCurrentQuantity() { return currentQuantity; }
        public void setCurrentQuantity(int currentQuantity) { this.currentQuantity = currentQuantity; }
        public int getReorderLevel() { return reorderLevel; }
        public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    }
}