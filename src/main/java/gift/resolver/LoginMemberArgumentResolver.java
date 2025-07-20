package gift.resolver;

import gift.annotation.LoginMember;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.MemberNotFoundException;
import gift.service.member.MemberService;
import gift.util.BearerAuthUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;
    private final BearerAuthUtil bearerAuthUtil;

    public LoginMemberArgumentResolver(BearerAuthUtil bearerAuthUtil,
            MemberService memberService) {
        this.memberService = memberService;
        this.bearerAuthUtil = bearerAuthUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = Objects.requireNonNull(request).getHeader("Authorization");

        Claims claims = bearerAuthUtil.extractAndValidate(header);

        webRequest.setAttribute("authClaims", claims, RequestAttributes.SCOPE_REQUEST);

        Long memberId = Long.valueOf(claims.getSubject());

        return memberService.getMemberById(memberId, Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
