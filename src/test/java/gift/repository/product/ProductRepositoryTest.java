package gift.repository.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.product.Product;
import gift.entity.product.value.ProductId;
import gift.exception.custom.InvalidProductException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("저장 후 ID 및 필드 검증")
    void saveAndFindById() {
        Product product = Product.of(1L, "MyProduct", 1000, "http://example.com/img.png", false);
        Product saved = productRepository.saveAndFlush(product);

        assertThat(saved.getId().id()).isEqualTo(1L);
        assertThat(saved.getName().name()).isEqualTo("MyProduct");
        assertThat(saved.getPrice().price()).isEqualTo(1000);
        assertThat(saved.getImageUrl().url()).isEqualTo("http://example.com/img.png");
        assertThat(saved.isHidden()).isFalse();

        Optional<Product> found = productRepository.findById(new ProductId(1L));
        assertThat(found).isPresent();
        assertThat(found.get().getName().name()).isEqualTo("MyProduct");
    }

    @Test
    @DisplayName("이름으로 조회 성공")
    void findByName_success() {
        Product product = Product.of(2L, "SearchName", 500, "http://example.com/img2.png", false);
        productRepository.saveAndFlush(product);

        Optional<Product> found = productRepository.findByName_Name("SearchName");
        assertThat(found).isPresent();
        assertThat(found.get().getId().id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("이름 조회 실패 - 결과 없음")
    void findByName_notFound() {
        Optional<Product> found = productRepository.findByName_Name("NoSuchName");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("중복 ID 저장 시 업데이트로 동작 - 예외 없음")
    void duplicateId_updatesSuccessfully() {
        Product first = Product.of(3L, "Original", 300, "http://example.com/img3.png", false);
        productRepository.saveAndFlush(first);

        Product updated = Product.of(3L, "UpdatedName", 400, "http://example.com/img4.png", true);
        Product result = productRepository.saveAndFlush(updated);

        assertThat(result.getId().id()).isEqualTo(3L);
        assertThat(result.getName().name()).isEqualTo("UpdatedName");
        assertThat(result.getPrice().price()).isEqualTo(400);
        assertThat(result.getImageUrl().url()).isEqualTo("http://example.com/img4.png");
        assertThat(result.isHidden()).isTrue();
    }


    @Test
    @DisplayName("유효하지 않은 ID로 상품 생성 시 예외 발생")
    void invalidId_throwsException() {
        assertThrows(InvalidProductException.class, () -> {
            Product.of(0L, "P", 100, "http://example.com/img.png", false);
        });
    }

    @Test
    @DisplayName("유효하지 않은 가격으로 상품 생성 시 예외 발생")
    void invalidPrice_throwsException() {
        assertThrows(InvalidProductException.class, () -> {
            Product.of(4L, "P", -10, "http://example.com/img.png", false);
        });
    }

    @Test
    @DisplayName("유효하지 않은 이미지 URL로 상품 생성 시 예외 발생")
    void invalidUrl_throwsException() {
        assertThrows(InvalidProductException.class, () -> {
            Product.of(5L, "P", 100, "invalid-url", false);
        });
    }
}
