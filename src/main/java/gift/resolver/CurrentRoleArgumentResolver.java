package gift.resolver;

import gift.annotation.CurrentRole;
import gift.entity.member.value.Role;
import gift.exception.custom.InvalidBearerAuthException;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentRoleArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentRole.class)
                && Role.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Claims claims = (Claims) webRequest.getAttribute("authClaims",
                RequestAttributes.SCOPE_REQUEST);

        if (claims == null || claims.get("role") == null) {
            throw new InvalidBearerAuthException("인증된 역할 정보가 없습니다.");
        }

        Role role = Role.valueOf(claims.get("role", String.class));

        webRequest.setAttribute("currentRole", role, RequestAttributes.SCOPE_REQUEST);

        return role;
    }
}
