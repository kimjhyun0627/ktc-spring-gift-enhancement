package gift.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;

public class TestUtils {

    public static Claims mockClaims(String role) {
        Claims claims = mock(Claims.class);
        when(claims.get("role", String.class)).thenReturn(role);
        when(claims.get("role")).thenReturn(role);
        return claims;
    }
}
