package gift.controller.user;

import gift.annotation.CurrentRole;
import gift.dto.product.option.DecreaseOptionRequest;
import gift.dto.product.option.OptionRequest;
import gift.dto.product.option.OptionResponse;
import gift.entity.member.value.Role;
import gift.service.product.option.ProductOptionService;
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

    private final ProductOptionService optionService;

    public ProductOptionController(ProductOptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<OptionResponse>> getOptions(
            @CurrentRole Role role,
            @PathVariable Long productId
    ) {
        List<OptionResponse> options = optionService.getOptions(productId, role);
        return ResponseEntity.ok(options);
    }

    @PostMapping
    public ResponseEntity<OptionResponse> addOption(
            @CurrentRole Role role,
            @PathVariable Long productId,
            @Valid @RequestBody OptionRequest optionRequest
    ) {
        OptionResponse response = optionService.addOption(
                productId,
                optionRequest.name(),
                optionRequest.quantity(),
                role
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{optionId}/decrease")
    public ResponseEntity<Void> decreaseOption(
            @CurrentRole Role role,
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @Valid @RequestBody DecreaseOptionRequest decreaseOptionRequest
    ) {
        optionService.decreaseOptionAmount(productId, optionId, decreaseOptionRequest.amount(),
                role);
        return ResponseEntity.noContent().build();
    }
}
