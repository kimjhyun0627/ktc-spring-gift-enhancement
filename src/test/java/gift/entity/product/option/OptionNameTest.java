package gift.entity.product.option;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.product.option.value.OptionName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OptionNameTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validNameShouldHaveNoViolations() {
        OptionName name = new OptionName("Option 1-[]()+&/_");
        Set<ConstraintViolation<OptionName>> violations = validator.validate(name);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankNameShouldViolateNotBlank() {
        OptionName name = new OptionName("");
        Set<ConstraintViolation<OptionName>> violations = validator.validate(name);
        assertThat(violations).anyMatch(
                v -> v.getMessage().contains("상품 옵션에 허용되지 않는 문자가 포함되었습니다."));
    }

    @Test
    void tooLongNameShouldViolateSize() {
        String longName = "A".repeat(51);
        OptionName name = new OptionName(longName);
        Set<ConstraintViolation<OptionName>> violations = validator.validate(name);
        assertThat(violations).anyMatch(
                v -> v.getMessage().contains("상품 옵션의 길이는 최대 50입니다"));
    }

    @Test
    void invalidCharactersShouldViolatePattern() {
        OptionName name = new OptionName("Invalid@Name$");
        Set<ConstraintViolation<OptionName>> violations = validator.validate(name);
        assertThat(violations).anyMatch(
                v -> v.getMessage().contains("상품 옵션에 허용되지 않는 문자가 포함되었습니다."));
    }
}
