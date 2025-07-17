package gift.controller.user;

import static gift.constant.PaginationConst.PRODUCT_PAGE_KEY;
import static gift.constant.PaginationConst.PRODUCT_PAGE_SIZE;
import static gift.util.RoleUtil.extractRole;

import gift.dto.product.ProductRequest;
import gift.dto.product.ProductResponse;
import gift.entity.product.Product;
import gift.exception.custom.ProductNotFoundException;
import gift.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
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
            @PageableDefault(size = PRODUCT_PAGE_SIZE, sort = PRODUCT_PAGE_KEY, direction = Sort.Direction.ASC)
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<Product> products = productService.getAllProducts(
                pageable,
                extractRole(httpServletRequest)
        );

        Page<ProductResponse> responsePage = products.map(Product::toResponse);

        return ResponseEntity.ok(responsePage);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id) {
        Product product = productService.getProductById(id, extractRole(httpServletRequest))
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(product.toResponse());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody ProductRequest productRequest) {
        Product product = productService.createProduct(
                productRequest.name(), productRequest.price(), productRequest.imageUrl(),
                extractRole(httpServletRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(product.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {
        Product product = productService.updateProduct(
                id, productRequest.name(), productRequest.price(), productRequest.imageUrl(),
                extractRole(httpServletRequest));
        return ResponseEntity.ok(product.toResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id) {
        productService.deleteProduct(id, extractRole(httpServletRequest));
        return ResponseEntity.noContent().build();
    }
}
