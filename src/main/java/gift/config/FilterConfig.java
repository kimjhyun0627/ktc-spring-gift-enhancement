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
        FilterRegistrationBean<JwtHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtHeaderFilter(jwtUtil));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtCookieFilter> jwtCookieFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtCookieFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtCookieFilter(jwtUtil));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registrationBean.addUrlPatterns("/admin/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AdminCookieFilter> adminCookieFilter() {
        FilterRegistrationBean<AdminCookieFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminCookieFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        registrationBean.addUrlPatterns("/admin/login", "/admin/logout");
        return registrationBean;
    }
}
