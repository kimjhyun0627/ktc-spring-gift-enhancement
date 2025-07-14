package gift.filter;

import gift.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class JwtCookieFilter extends JwtFilter {

    public JwtCookieFilter(JwtUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    protected boolean shouldFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (uri.startsWith(ctx + "/api/")
                || uri.startsWith(ctx + "/css/")
                || uri.startsWith(ctx + "/js/")
                || uri.equals(ctx + "/admin/login")
                || uri.equals(ctx + "/admin/logout")) {
            return false;
        }
        return uri.startsWith(ctx + "/admin/");
    }

    @Override
    protected String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> "AUTH_TOKEN".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    protected void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                String.format("{\"status\":%d,\"error\":\"%s\"}",
                        HttpServletResponse.SC_UNAUTHORIZED, message)
        );
    }
}
