package com.grocery.management.seeder;

import com.grocery.management.entity.*;
import com.grocery.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Runs once at application startup.
 * Creates a default ADMIN and STAFF login plus a handful of sample
 * categories/products so the app is immediately demo-able after cloning.
 *
 * Default credentials (change/remove for production use):
 *   admin / admin123   (ROLE_ADMIN)
 *   staff / staff123   (ROLE_STAFF)
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedCatalog();
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@grocery.local")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build());
        }

        if (!userRepository.existsByUsername("staff")) {
            userRepository.save(User.builder()
                    .username("staff")
                    .password(passwordEncoder.encode("staff123"))
                    .email("staff@grocery.local")
                    .role(Role.STAFF)
                    .enabled(true)
                    .build());
        }
    }

    private void seedCatalog() {
        if (categoryRepository.count() > 0) {
            return; // already seeded
        }

        Category dairy = categoryRepository.save(Category.builder()
                .name("Dairy").description("Milk, cheese, curd and related products").build());
        Category produce = categoryRepository.save(Category.builder()
                .name("Fruits & Vegetables").description("Fresh produce").build());
        Category staples = categoryRepository.save(Category.builder()
                .name("Staples").description("Rice, flour, pulses and grains").build());
        Category beverages = categoryRepository.save(Category.builder()
                .name("Beverages").description("Tea, coffee, juices and drinks").build());

        productRepository.save(Product.builder().name("Toned Milk (500ml)").category(dairy)
                .price(new BigDecimal("28.00")).quantity(120).unit("packet").reorderLevel(20).build());
        productRepository.save(Product.builder().name("Paneer").category(dairy)
                .price(new BigDecimal("90.00")).quantity(40).unit("kg").reorderLevel(10).build());

        productRepository.save(Product.builder().name("Onion").category(produce)
                .price(new BigDecimal("35.00")).quantity(200).unit("kg").reorderLevel(30).build());
        productRepository.save(Product.builder().name("Tomato").category(produce)
                .price(new BigDecimal("40.00")).quantity(150).unit("kg").reorderLevel(30).build());
        productRepository.save(Product.builder().name("Banana").category(produce)
                .price(new BigDecimal("50.00")).quantity(15).unit("dozen").reorderLevel(20).build()); // low stock example

        productRepository.save(Product.builder().name("Basmati Rice").category(staples)
                .price(new BigDecimal("110.00")).quantity(80).unit("kg").reorderLevel(15).build());
        productRepository.save(Product.builder().name("Wheat Flour (Atta)").category(staples)
                .price(new BigDecimal("45.00")).quantity(100).unit("kg").reorderLevel(20).build());
        productRepository.save(Product.builder().name("Toor Dal").category(staples)
                .price(new BigDecimal("130.00")).quantity(60).unit("kg").reorderLevel(15).build());

        productRepository.save(Product.builder().name("Tea Powder (250g)").category(beverages)
                .price(new BigDecimal("95.00")).quantity(70).unit("packet").reorderLevel(15).build());
        productRepository.save(Product.builder().name("Orange Juice (1L)").category(beverages)
                .price(new BigDecimal("120.00")).quantity(8).unit("bottle").reorderLevel(10).build()); // low stock example
    }
}
