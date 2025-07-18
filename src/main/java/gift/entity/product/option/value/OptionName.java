package gift.entity.product.option.value;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@Embeddable
public record OptionName(
        @NotBlank
        @Length(max = MAX_OPT_NAME_SIZE, message = "상품 옵션의 길이는 최대 " + MAX_OPT_NAME_SIZE + "입니다")
        @Pattern(regexp = OPT_NAME_PATTERN, message = "상품 옵션에 허용되지 않는 문자가 포함되었습니다.")
        String name
) {

    private static final int MAX_OPT_NAME_SIZE = 50;
    private static final String OPT_NAME_PATTERN =
            "^[\\p{L}0-9()\\[\\]+\\-&/_ ]+$";

    public OptionName {
    }
}
