package gift.config;

import gift.resolver.CurrentRoleArgumentResolver;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.member.MemberService;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentRoleArgumentResolver());
        resolvers.add(new LoginMemberArgumentResolver(jwtUtil, bearerAuthUtil, memberService));
    }
}
