package gift.entity.product.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.Product;
import gift.exception.custom.InvalidOptionDecreaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상품 옵션 기능 테스트")
class ProductOptionTest {

    @Test
    @DisplayName("of 메서드로 생성 시 올바른 값이어야 한다")
    void ofShouldCreateWithCorrectValues() {
        ProductOption opt = ProductOption.of("Test", 20);
        assertThat(opt.getId()).isNull();
        assertThat(opt.getName().name()).isEqualTo("Test");
        assertThat(opt.getQuantity().quantity()).isEqualTo(20);
    }

    @Test
    @DisplayName("제품에 할당 시 product 필드가 설정되어야 한다")
    void assignToShouldSetProduct() {
        Product product = Product.of("P", 100, "http://asdf.png");
        ProductOption opt = ProductOption.of("Opt", 5);
        opt.assignTo(product);
        assertThat(opt.getProduct()).isEqualTo(product);
    }

    @Test
    @DisplayName("수량 감소 시 quantity가 올바르게 줄어야 한다")
    void decreaseAmountShouldReduceQuantity() {
        ProductOption opt = ProductOption.of("Opt", 10);
        opt.decreaseAmount(4);
        assertThat(opt.getQuantity().quantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("과도한 감소 요청 시 InvalidOptionDecreaseException 예외가 발생해야 한다")
    void decreaseAmountTooMuchShouldThrow() {
        ProductOption opt = ProductOption.of("Opt", 2);
        assertThatThrownBy(() -> opt.decreaseAmount(5))
                .isInstanceOf(InvalidOptionDecreaseException.class);
    }
}
