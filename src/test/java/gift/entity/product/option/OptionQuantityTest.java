package gift.entity.product.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.option.value.OptionQuantity;
import gift.exception.custom.InvalidOptionDecreaseException;
import gift.exception.custom.InvalidOptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("옵션 수량 생성자 및 감소 기능 테스트")
class OptionQuantityTest {

    @Test
    @DisplayName("유효한 수량은 예외 없이 생성된다")
    void validQuantityShouldCreateWithoutException() {
        OptionQuantity qty = new OptionQuantity(100);
        assertThat(qty.quantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("최소 수량 미달 시 InvalidOptionException 발생")
    void belowMinShouldThrow() {
        assertThatThrownBy(() -> new OptionQuantity(0))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessageContaining("수량은 최소 1개 이상이어야 합니다");
    }

    @Test
    @DisplayName("최대 수량 초과 시 InvalidOptionException 발생")
    void aboveMaxShouldThrow() {
        assertThatThrownBy(() -> new OptionQuantity(100_000_000))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessageContaining("수량은 100000000개 미만이어야 합니다");
    }

    @Test
    @DisplayName("유효한 감소 요청 시 수량이 올바르게 감소해야 한다")
    void decreaseByValidAmount() {
        OptionQuantity qty = new OptionQuantity(10);
        OptionQuantity newQty = qty.decreaseBy(5);
        assertThat(newQty.quantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("감소 요청이 과도할 경우 InvalidOptionDecreaseException 예외 발생")
    void decreaseByTooMuchShouldThrow() {
        OptionQuantity qty = new OptionQuantity(3);
        assertThatThrownBy(() -> qty.decreaseBy(5))
                .isInstanceOf(InvalidOptionDecreaseException.class)
                .hasMessageContaining("잘못된 수량 수정 요청입니다");
    }
}
