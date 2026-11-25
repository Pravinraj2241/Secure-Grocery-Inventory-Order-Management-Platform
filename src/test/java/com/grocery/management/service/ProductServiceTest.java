package com.grocery.management.service;

import com.grocery.management.dto.ProductDtos.ProductRequest;
import com.grocery.management.dto.ProductDtos.ProductResponse;
import com.grocery.management.entity.Category;
import com.grocery.management.entity.Product;
import com.grocery.management.exception.ResourceNotFoundException;
import com.grocery.management.repository.CategoryRepository;
import com.grocery.management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Dairy").description("Dairy items").build();
        product = Product.builder()
                .id(1L)
                .name("Milk")
                .category(category)
                .price(new BigDecimal("28.00"))
                .quantity(50)
                .unit("packet")
                .reorderLevel(10)
                .build();
    }

    @Test
    void create_shouldSaveAndReturnProduct_whenCategoryExists() {
        ProductRequest request = new ProductRequest(
                "Milk", 1L, new BigDecimal("28.00"), 50, "packet", 10);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.create(request);

        assertNotNull(response);
        assertEquals("Milk", response.getName());
        assertEquals("Dairy", response.getCategoryName());
        assertFalse(response.isLowStock());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void create_shouldThrow_whenCategoryDoesNotExist() {
        ProductRequest request = new ProductRequest(
                "Milk", 99L, new BigDecimal("28.00"), 50, "packet", 10);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    void getById_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(100L));
    }

    @Test
    void getLowStock_shouldReturnOnlyProductsAtOrBelowReorderLevel() {
        Product lowStockProduct = Product.builder()
                .id(2L).name("Banana").category(category)
                .price(new BigDecimal("50.00")).quantity(5).unit("dozen").reorderLevel(20)
                .build();

        when(productRepository.findLowStockProducts()).thenReturn(List.of(lowStockProduct));

        List<ProductResponse> result = productService.getLowStock();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isLowStock());
        assertEquals("Banana", result.get(0).getName());
    }
}
