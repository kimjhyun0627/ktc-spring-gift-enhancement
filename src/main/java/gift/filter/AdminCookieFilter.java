package gift.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class AdminCookieFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest httpServletRequest)
            throws ServletException {
        String uri = httpServletRequest.getRequestURI();
        String ctx = httpServletRequest.getContextPath();
        return !(uri.equals(ctx + "/admin/login") || uri.equals(ctx + "/admin/logout"));
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest httpServletRequest,
            @NotNull HttpServletResponse httpServletResponse,
            FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(httpServletRequest, httpServletResponse);

        Object tokenObj = httpServletRequest.getAttribute("AUTH_TOKEN");
        if (tokenObj instanceof String token) {
            Cookie cookie = new Cookie("AUTH_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            httpServletResponse.addCookie(cookie);
        }

        Object logoutObj = httpServletRequest.getAttribute("LOGOUT");
        if (logoutObj instanceof Boolean && (Boolean) logoutObj) {
            Cookie cookie = new Cookie("AUTH_TOKEN", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
        }

    }
}
