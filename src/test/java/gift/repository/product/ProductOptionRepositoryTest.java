package gift.repository.product;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.entity.product.option.value.OptionName;
import gift.entity.product.value.ProductId;
import gift.repository.product.option.ProductOptionRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("ProductOptionRepository 단위 테스트")
class ProductOptionRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository optionRepository;

    @Test
    @DisplayName("findAllByProduct_Id: 저장된 옵션 모두 조회")
    void findAllByProductId_returnsAllOptions() {
        // given
        Product product = Product.of(1L, "TestProduct", 1000, "http://img.png", false);
        productRepository.saveAndFlush(product);

        ProductOption o1 = ProductOption.of("OptA", 5);
        o1.assignTo(product);
        ProductOption o2 = ProductOption.of("OptB", 3);
        o2.assignTo(product);
        optionRepository.saveAndFlush(o1);
        optionRepository.saveAndFlush(o2);

        // when
        List<ProductOption> options = optionRepository
                .findAllByProduct_Id(new ProductId(1L));

        // then
        assertThat(options)
                .hasSize(2)
                .extracting(ProductOption::getName)
                .extracting(OptionName::name)
                .containsExactlyInAnyOrder("OptA", "OptB");
    }

    @Test
    @DisplayName("findAllByProduct_Id: 옵션이 없으면 빈 리스트 반환")
    void findAllByProductId_noOptions_returnsEmpty() {
        // given: 상품만 저장, 옵션은 없음
        Product product = Product.of(2L, "EmptyProduct", 500, "http://img2.png", false);
        productRepository.saveAndFlush(product);

        // when
        List<ProductOption> options = optionRepository
                .findAllByProduct_Id(new ProductId(2L));

        // then
        assertThat(options).isEmpty();
    }

    @Test
    @DisplayName("existsByProduct_IdAndName_Name: 동일 상품에 동일 이름 옵션 있으면 true")
    void existsByProductIdAndName_trueWhenExists() {
        // given
        Product product = Product.of(3L, "ExistsProduct", 800, "http://img3.png", false);
        productRepository.saveAndFlush(product);

        ProductOption option = ProductOption.of("UniqueOpt", 10);
        option.assignTo(product);
        optionRepository.saveAndFlush(option);

        // when
        boolean exists = optionRepository
                .existsByProduct_IdAndName_Name(new ProductId(3L), "UniqueOpt");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByProduct_IdAndName_Name: 동일 상품에 다른 이름 옵션 있으면 false")
    void existsByProductIdAndName_falseWhenDifferentName() {
        // given
        Product product = Product.of(4L, "DiffNameProduct", 600, "http://img4.png", false);
        productRepository.saveAndFlush(product);

        ProductOption option = ProductOption.of("OptX", 2);
        option.assignTo(product);
        optionRepository.saveAndFlush(option);

        // when
        boolean exists = optionRepository
                .existsByProduct_IdAndName_Name(new ProductId(4L), "OptY");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByProduct_IdAndName_Name: 다른 상품의 동일 이름 옵션은 false")
    void existsByDifferentProduct_false() {
        // given
        Product p1 = Product.of(5L, "Prod1", 700, "http://img5.png", false);
        Product p2 = Product.of(6L, "Prod2", 900, "http://img6.png", false);
        productRepository.saveAndFlush(p1);
        productRepository.saveAndFlush(p2);

        ProductOption option1 = ProductOption.of("SharedOpt", 4);
        option1.assignTo(p1);
        optionRepository.saveAndFlush(option1);

        // when
        boolean existsForP2 = optionRepository
                .existsByProduct_IdAndName_Name(new ProductId(6L), "SharedOpt");

        // then
        assertThat(existsForP2).isFalse();
    }
}
