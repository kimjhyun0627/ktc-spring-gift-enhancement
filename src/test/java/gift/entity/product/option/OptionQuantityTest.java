package gift.entity.product.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.option.value.OptionQuantity;
import gift.exception.custom.InvalidOptionDecreaseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OptionQuantityTest {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validQuantityShouldHaveNoViolations() {
        OptionQuantity qty = new OptionQuantity(100);
        Set<ConstraintViolation<OptionQuantity>> violations = validator.validate(qty);
        assertThat(violations).isEmpty();
    }

    @Test
    void belowMinShouldViolateMin() {
        OptionQuantity qty = new OptionQuantity(0);
        Set<ConstraintViolation<OptionQuantity>> violations = validator.validate(qty);
        assertThat(violations).anyMatch(
                v -> v.getMessage().contains("수량은 최소 1개 이상이어야 합니다"));
    }

    @Test
    void aboveMaxShouldViolateMax() {
        OptionQuantity qty = new OptionQuantity(100_000_000);
        Set<ConstraintViolation<OptionQuantity>> violations = validator.validate(qty);
        assertThat(violations).anyMatch(
                v -> v.getMessage().contains("수량은 100000000개 미만이어야 합니다"));
    }

    @Test
    void decreaseByValidAmount() {
        OptionQuantity qty = new OptionQuantity(10);
        OptionQuantity newQty = qty.decreaseBy(5);
        assertThat(newQty.quantity()).isEqualTo(5);
    }

    @Test
    void decreaseByTooMuchShouldThrow() {
        OptionQuantity qty = new OptionQuantity(3);
        assertThatThrownBy(() -> qty.decreaseBy(5))
                .isInstanceOf(InvalidOptionDecreaseException.class)
                .hasMessageContaining("잘못된 수량 수정 요청입니다");
    }
}
