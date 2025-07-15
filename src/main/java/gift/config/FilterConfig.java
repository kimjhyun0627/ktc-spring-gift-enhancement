package gift.config;

import gift.filter.AdminCookieFilter;
import gift.filter.JwtCookieFilter;
import gift.filter.JwtHeaderFilter;
import gift.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtHeaderFilter> jwtHeaderFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtHeaderFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new JwtHeaderFilter(jwtUtil));
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        reg.addUrlPatterns("/api/*");
        return reg;
    }

    @Bean
    public FilterRegistrationBean<JwtCookieFilter> jwtCookieFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtCookieFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new JwtCookieFilter(jwtUtil));
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        reg.addUrlPatterns("/admin/*");
        return reg;
    }

    @Bean
    public FilterRegistrationBean<AdminCookieFilter> adminCookieFilter() {
        FilterRegistrationBean<AdminCookieFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new AdminCookieFilter());
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        reg.addUrlPatterns("/admin/login", "/admin/logout");
        return reg;
    }
}
