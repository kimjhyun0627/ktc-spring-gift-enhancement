package gift.resolver;

import gift.annotation.AuthorizedProduct;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.exception.custom.InvalidAuthExeption;
import gift.exception.custom.InvalidBearerAuthException;
import gift.exception.custom.ProductNotFoundException;
import gift.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

public class AuthorizedProductArgumentResolver implements HandlerMethodArgumentResolver {

    private final ProductService productService;

    public AuthorizedProductArgumentResolver(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthorizedProduct.class)
                && (parameter.getParameterType().equals(Product.class)
                || parameter.getParameterType().equals(Optional.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws InvalidAuthExeption {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        @SuppressWarnings("unchecked")
        Map<String, String> uriVariables = (Map<String, String>)
                Objects.requireNonNull(request).getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String idStr = uriVariables.get("productId");
        Long productId = Long.valueOf(idStr);

        Role role = (Role) webRequest.getAttribute("currentRole", RequestAttributes.SCOPE_REQUEST);
        if (role == null) {
            throw new InvalidBearerAuthException("Role 정보가 없습니다.");
        }

        Optional<Product> productOpt = productService.getProductById(productId, role);
        Product product = productOpt.orElseThrow(() -> new ProductNotFoundException(productId));

        if (parameter.getParameterType().equals(Optional.class)) {
            return productOpt;
        }
        return product;
    }
}
