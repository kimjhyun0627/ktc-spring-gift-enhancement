package gift.util;

import gift.entity.member.value.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key key;
    private final long validityMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expireMillisecond}") long validityMillis
    ) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.validityMillis = validityMillis;
    }

    public String generateToken(Long memberId, Role role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(memberId.toString())
                .claim("role", role.name())
                .issuedAt(new Date(now))
                .expiration(new Date(now + validityMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token);
    }

    public boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return parseToken(token).getPayload();
    }

    public Long getMemberId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public Role getRole(String token) {
        String roleName = getClaims(token).get("role", String.class);
        return Role.valueOf(roleName);
    }

    public LocalDateTime getExpiration(String token) {
        Date exp = getClaims(token).getExpiration();
        return LocalDateTime.ofInstant(exp.toInstant(), ZoneId.systemDefault());
    }
}
