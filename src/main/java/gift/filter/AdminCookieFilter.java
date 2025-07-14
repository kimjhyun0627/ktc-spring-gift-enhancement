package gift.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class AdminCookieFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        return !(uri.equals(ctx + "/admin/login") || uri.equals(ctx + "/admin/logout"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        HttpServletResponseWrapper wrapped = new HttpServletResponseWrapper(response) {
            @Override
            public void sendRedirect(String location) throws IOException {
                Object tokenObj = request.getAttribute("AUTH_TOKEN");
                if (tokenObj instanceof String token) {
                    Cookie cookie = new Cookie("AUTH_TOKEN", token);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 60);
                    super.addCookie(cookie);
                }
                Object logoutObj = request.getAttribute("LOGOUT");
                if (logoutObj instanceof Boolean && (Boolean) logoutObj) {
                    Cookie cookie = new Cookie("AUTH_TOKEN", null);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    super.addCookie(cookie);
                }
                super.sendRedirect(location);
            }
        };

        chain.doFilter(request, wrapped);
    }

}
