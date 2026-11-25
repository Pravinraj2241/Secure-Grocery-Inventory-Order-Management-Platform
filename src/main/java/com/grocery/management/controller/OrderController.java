package com.grocery.management.controller;

import com.grocery.management.dto.OrderDtos.OrderRequest;
import com.grocery.management.dto.OrderDtos.OrderResponse;
import com.grocery.management.dto.OrderDtos.OrderStatusUpdateRequest;
import com.grocery.management.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getByCustomer(customerId));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }

    // Simple text-based invoice, handy for printing/downloading a bill.
    @GetMapping(value = "/{id}/invoice", produces = "text/plain")
    public ResponseEntity<String> getInvoice(@PathVariable Long id) {
        OrderResponse order = orderService.getById(id);

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("        GROCERY MANAGEMENT SYSTEM\n");
        sb.append("               INVOICE\n");
        sb.append("=========================================\n");
        sb.append("Order ID   : ").append(order.getId()).append("\n");
        sb.append("Date       : ").append(order.getOrderDate()).append("\n");
        sb.append("Customer   : ").append(order.getCustomerName()).append("\n");
        sb.append("Status     : ").append(order.getStatus()).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s %10s%n", "Product", "Qty", "Price", "Subtotal"));
        sb.append("-----------------------------------------\n");
        order.getItems().forEach(item ->
                sb.append(String.format("%-20s %5d %10s %10s%n",
                        item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal()))
        );
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-27s %19s%n", "TOTAL", order.getTotalAmount()));
        sb.append("=========================================\n");
        sb.append("        Thank you for shopping with us!\n");

        return ResponseEntity.ok(sb.toString());
    }
}
