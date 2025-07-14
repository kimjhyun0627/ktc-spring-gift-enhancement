package gift.entity.product.value;

import gift.exception.custom.InvalidProductException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public record ProductName(String name) {

    public static final int MAX_LENGTH = 15;

    public static final String ALLOWED_CHAR =
            "^[\\p{IsHangul}\\p{IsHan}\\p{IsAlphabetic}\\d\\s()\\[\\]+\\-&/_]+$";

    public static final List<Pattern> FORBIDDEN_PATTERNS = List.of(
            Pattern.compile("카카오")
    );

    public ProductName {
        Objects.requireNonNull(name, "상품 이름은 필수 입력값입니다.");
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidProductException("상품 이름은 필수 입력값입니다.");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new InvalidProductException("상품 이름은 공백 포함 최대 " + MAX_LENGTH + "자까지 입력할 수 있습니다.");
        }
        if (!Pattern.compile(ALLOWED_CHAR).matcher(trimmed).matches()) {
            throw new InvalidProductException("상품 이름에 허용되지 않는 문자가 포함되었습니다");
        }

        name = trimmed;
    }
}
