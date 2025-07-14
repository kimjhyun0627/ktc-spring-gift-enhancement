package gift.config;

import gift.filter.AdminCookieFilter;
import gift.filter.JwtCookieFilter;
import gift.filter.JwtHeaderFilter;
import gift.resolver.CurrentRoleArgumentResolver;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.member.MemberService;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import java.util.List;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final BearerAuthUtil bearerAuthUtil;

    public WebConfig(JwtUtil jwtUtil, MemberService memberService, BearerAuthUtil bearerAuthUtil) {
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
        this.bearerAuthUtil = bearerAuthUtil;
    }


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

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentRoleArgumentResolver());
        resolvers.add(new LoginMemberArgumentResolver(jwtUtil, bearerAuthUtil, memberService));
    }
}
