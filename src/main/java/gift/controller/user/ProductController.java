package gift.controller.user;

import gift.annotation.CurrentRole;
import gift.dto.product.ProductRequest;
import gift.dto.product.ProductResponse;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.exception.custom.ProductNotFoundException;
import gift.service.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable,
            @CurrentRole Role role
    ) {
        Page<Product> products = productService.getAllProducts(pageable, role);

        Page<ProductResponse> responsePage = products.map(Product::toResponse);

        return ResponseEntity.ok(responsePage);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @CurrentRole Role role,
            @PathVariable Long id
    ) {
        Product product = productService.getProductById(id, role)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(product.toResponse());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @CurrentRole Role role,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        Product product = productService.createProduct(
                productRequest.name(), productRequest.price(), productRequest.imageUrl(),
                role);
        return ResponseEntity.status(HttpStatus.CREATED).body(product.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @CurrentRole Role role,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        Product product = productService.updateProduct(id, productRequest.name(),
                productRequest.price(), productRequest.imageUrl(), role);
        return ResponseEntity.ok(product.toResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @CurrentRole Role role,
            @PathVariable Long id
    ) {
        productService.deleteProduct(id, role);
        return ResponseEntity.noContent().build();
    }
}
