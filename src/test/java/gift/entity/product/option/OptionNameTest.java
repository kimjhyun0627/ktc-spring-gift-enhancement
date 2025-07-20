package gift.entity.product.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.option.value.OptionName;
import gift.exception.custom.InvalidOptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("옵션 이름 생성자 검증 테스트")
class OptionNameTest {

    @Test
    @DisplayName("유효한 이름은 예외 없이 생성된다")
    void validNameShouldCreateWithoutException() {
        OptionName name = new OptionName("Option 1-[]()+&/_");
        assertThat(name.name()).isEqualTo("Option 1-[]()+&/_");
    }

    @Test
    @DisplayName("빈 이름은 InvalidOptionException 발생")
    void blankNameShouldThrow() {
        assertThatThrownBy(() -> new OptionName(""))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessageContaining("상품 옵션 이름은 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("너무 긴 이름은 InvalidOptionException 발생")
    void tooLongNameShouldThrow() {
        String longName = "A".repeat(51);
        assertThatThrownBy(() -> new OptionName(longName))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessageContaining("상품 옵션의 길이는 최대 50입니다");
    }

    @Test
    @DisplayName("허용되지 않은 문자가 포함된 이름은 InvalidOptionException 발생")
    void invalidCharactersShouldThrow() {
        assertThatThrownBy(() -> new OptionName("Invalid@Name$"))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessageContaining("상품 옵션에 허용되지 않는 문자가 포함되었습니다.");
    }
}
