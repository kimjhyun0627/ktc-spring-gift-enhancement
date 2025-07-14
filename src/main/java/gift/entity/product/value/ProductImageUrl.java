package gift.entity.product.value;

import gift.exception.custom.InvalidProductException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Pattern;

public record ProductImageUrl(String url) {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(?:http|https)://[\\w.-]+(?:\\.[\\w.-]+)+(?:/\\S*)?$",
            Pattern.CASE_INSENSITIVE
    );

    public ProductImageUrl {
        Objects.requireNonNull(url, "이미지 URL은 필수 입력값입니다.");
        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidProductException("이미지 URL은 필수 입력값입니다.");
        }
        if (!URL_PATTERN.matcher(trimmed).matches()) {
            throw new InvalidProductException("올바른 HTTP/HTTPS 이미지 URL이어야 합니다.");
        }
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme().toLowerCase();
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                throw new InvalidProductException("이미지 URL은 HTTP/HTTPS 프로토콜이어야 합니다.");
            }
        } catch (URISyntaxException e) {
            throw new InvalidProductException("올바른 이미지 URL이어야 합니다: " + url);
        }
        url = trimmed;
    }
}
