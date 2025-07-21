package gift.entity.product.option.value;

import gift.exception.custom.InvalidOptionException;
import jakarta.persistence.Embeddable;

@Embeddable
public record OptionName(
        String name
) {

    private static final int MAX_OPT_NAME_SIZE = 50;
    private static final String OPT_NAME_PATTERN = "^[\\p{L}0-9()\\[\\]+\\-&/_ ]+$";

    public OptionName {
        if (name == null || name.isBlank()) {
            throw new InvalidOptionException("상품 옵션 이름은 비어있을 수 없습니다.");
        }
        if (name.length() > MAX_OPT_NAME_SIZE) {
            throw new InvalidOptionException("상품 옵션의 길이는 최대 " + MAX_OPT_NAME_SIZE + "입니다.");
        }
        if (!name.matches(OPT_NAME_PATTERN)) {
            throw new InvalidOptionException("상품 옵션에 허용되지 않는 문자가 포함되었습니다.");
        }
    }
}
