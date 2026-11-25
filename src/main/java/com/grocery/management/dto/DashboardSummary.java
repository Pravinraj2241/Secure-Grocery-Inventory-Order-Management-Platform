package com.grocery.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummary {
    private long totalProducts;
    private long lowStockProducts;
    private long totalCustomers;
    private long totalOrders;
    private long pendingOrders;
    private BigDecimal totalRevenue;
}
