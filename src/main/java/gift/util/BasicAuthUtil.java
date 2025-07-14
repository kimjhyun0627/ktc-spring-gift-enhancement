package gift.util;

import gift.exception.custom.InvalidBasicAuthException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public final class BasicAuthUtil {

    private static final String SCHEME = "Basic ";

    private BasicAuthUtil() {
    }

    public static Credentials parse(String header) {
        if (header == null || !header.startsWith(SCHEME)) {
            throw new InvalidBasicAuthException("잘못된 Basic Authorization 헤더");
        }
        String base64 = header.substring(SCHEME.length()).trim();
        String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);

        int idx = decoded.indexOf(':');
        if (idx <= 0) {
            throw new InvalidBasicAuthException("Basic 인증 정보 포맷 오류");
        }
        String email = decoded.substring(0, idx);
        String password = decoded.substring(idx + 1);

        return new Credentials(email, password);
    }

    public record Credentials(String email, String password) {

    }
}
