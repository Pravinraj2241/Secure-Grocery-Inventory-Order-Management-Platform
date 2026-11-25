package com.grocery.management.service;

import com.grocery.management.dto.OrderDtos.*;
import com.grocery.management.entity.*;
import com.grocery.management.exception.BadRequestException;
import com.grocery.management.exception.ResourceNotFoundException;
import com.grocery.management.repository.OrderRepository;
import com.grocery.management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;

    /**
     * Creates a new order:
     *  1. Validates that every requested product exists and has enough stock.
     *  2. Deducts stock quantity for each product (core "inventory update" logic).
     *  3. Computes line subtotals and the order total.
     *  4. Persists the order together with its items in a single transaction.
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerService.findEntity(request.getCustomerId());

        Order order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for '" + product.getName() + "'. Available: "
                                + product.getQuantity() + ", requested: " + itemRequest.getQuantity());
            }

            // Deduct stock -- keeps inventory in sync with sales automatically.
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.addItem(orderItem);
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream().map(this::toResponse).toList();
    }

    public OrderResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public List<OrderResponse> getByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = findEntity(id);

        // If an order is cancelled after being placed, restock the products.
        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    Order findEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}
