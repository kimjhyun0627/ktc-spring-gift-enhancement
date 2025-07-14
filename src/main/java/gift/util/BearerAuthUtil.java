package gift.util;

import gift.exception.custom.InvalidBearerAuthException;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BearerAuthUtil {

    private static final String PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;

    public BearerAuthUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    public Claims extractAndValidate(String header) {
        if (!StringUtils.hasText(header) || !header.startsWith(PREFIX)) {
            throw new InvalidBearerAuthException("Authorization header is missing or invalid");
        }
        String token = header.substring(PREFIX.length()).trim();
        if (!jwtUtil.validate(token)) {
            throw new InvalidBearerAuthException("Invalid or expired JWT token");
        }
        return jwtUtil.getClaims(token);
    }
}
