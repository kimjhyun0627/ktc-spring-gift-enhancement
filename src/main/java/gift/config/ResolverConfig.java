package gift.config;

import gift.resolver.AuthorizedProductArgumentResolver;
import gift.resolver.CurrentRoleArgumentResolver;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.member.MemberService;
import gift.service.product.ProductService;
import gift.util.BearerAuthUtil;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResolverConfig implements WebMvcConfigurer {

    private final MemberService memberService;
    private final BearerAuthUtil bearerAuthUtil;
    private final ProductService productService;

    public ResolverConfig(
            MemberService memberService,
            BearerAuthUtil bearerAuthUtil,
            ProductService productService
    ) {
        this.memberService = memberService;
        this.bearerAuthUtil = bearerAuthUtil;
        this.productService = productService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(bearerAuthUtil, memberService));
        resolvers.add(new CurrentRoleArgumentResolver());
        resolvers.add(new AuthorizedProductArgumentResolver(productService));
    }
}
