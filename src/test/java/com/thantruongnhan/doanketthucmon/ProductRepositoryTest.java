package com.thantruongnhan.doanketthucmon;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveFoodProduct_success() {
        Product product = Product.builder()
                .name("Cơm gà xối mỡ")
                .description("Cơm gà giòn, ăn kèm dưa chua")
                .price(new BigDecimal("45000.00"))
                .isActive(true)
                .build();

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Cơm gà xối mỡ");
        assertThat(saved.getPrice())
                .isEqualByComparingTo("45000.00");
    }

    @Test
    void findFoodProductById_success() {
        Product product = Product.builder()
                .name("Phở bò tái")
                .description("Phở bò tái truyền thống")
                .price(new BigDecimal("55000.00"))
                .isActive(true)
                .build();

        Product saved = entityManager.persistAndFlush(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Phở bò tái");
    }
}
