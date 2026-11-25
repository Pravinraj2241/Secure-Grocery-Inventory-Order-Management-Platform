package com.grocery.management.service;

import com.grocery.management.dto.ProductDtos.ProductRequest;
import com.grocery.management.dto.ProductDtos.ProductResponse;
import com.grocery.management.entity.Category;
import com.grocery.management.entity.Product;
import com.grocery.management.exception.ResourceNotFoundException;
import com.grocery.management.repository.CategoryRepository;
import com.grocery.management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public List<ProductResponse> search(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getLowStock() {
        return productRepository.findLowStockProducts().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .category(category)
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .reorderLevel(request.getReorderLevel())
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntity(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUnit(request.getUnit());
        product.setReorderLevel(request.getReorderLevel());

        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = findEntity(id);
        productRepository.delete(product);
    }

    Product findEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .reorderLevel(product.getReorderLevel())
                .lowStock(product.isLowStock())
                .build();
    }
}
