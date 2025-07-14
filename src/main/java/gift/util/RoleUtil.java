package gift.util;

import gift.entity.member.value.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public class RoleUtil {

    public static Role extractRole(HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("authClaims");
        return Role.valueOf(claims.get("role", String.class));
    }
}
