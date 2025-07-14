package gift.resolver;

import gift.annotation.LoginMember;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.MemberNotFoundException;
import gift.service.member.MemberService;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final BearerAuthUtil bearerAuthUtil;

    public LoginMemberArgumentResolver(JwtUtil jwtUtil, BearerAuthUtil bearerAuthUtil,
            MemberService memberService) {
        this.jwtUtil = jwtUtil;
        this.bearerAuthUtil = bearerAuthUtil;
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Claims claims = bearerAuthUtil.extractAndValidate(request.getHeader("Authorization"));
        Long memberId = Long.valueOf(claims.getSubject());
        Role role = Role.valueOf(claims.get("role", String.class));
        return memberService.getMemberById(memberId, Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
