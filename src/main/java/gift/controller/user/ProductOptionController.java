package gift.controller.user;

import static gift.util.RoleUtil.extractRole;

import gift.dto.product.option.DecreaseOptionRequest;
import gift.dto.product.option.OptionRequest;
import gift.dto.product.option.OptionResponse;
import gift.exception.custom.ProductNotFoundException;
import gift.service.product.ProductService;
import gift.service.product.option.ProductOptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class ProductOptionController {

    private final ProductService productService;
    private final ProductOptionService optionService;

    public ProductOptionController(ProductService productService,
            ProductOptionService optionService) {
        this.productService = productService;
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<OptionResponse>> getOptions(
            @PathVariable Long productId,
            HttpServletRequest request) {
        authorizeAccess(productId, request);
        List<OptionResponse> options = optionService.getOptions(productId);
        return ResponseEntity.ok(options);
    }

    @PostMapping
    public ResponseEntity<OptionResponse> addOption(
            @PathVariable Long productId,
            @Valid @RequestBody OptionRequest optionRequest,
            HttpServletRequest request) {
        authorizeAccess(productId, request);
        OptionResponse response = optionService.addOption(
                productId,
                optionRequest.name(),
                optionRequest.quantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{optionId}/decrease")
    public ResponseEntity<Void> decreaseOption(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @Valid @RequestBody DecreaseOptionRequest decreaseOptionRequest,
            HttpServletRequest request) {
        authorizeAccess(productId, request);

        optionService.decreaseOption(optionId, decreaseOptionRequest.amount());
        return ResponseEntity.noContent().build();
    }

    private void authorizeAccess(Long productId, HttpServletRequest request) {
        var role = extractRole(request);
        productService.getProductById(productId, role)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
