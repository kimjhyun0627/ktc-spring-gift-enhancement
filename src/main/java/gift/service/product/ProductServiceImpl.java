package gift.service.product;

import static gift.entity.product.value.ProductName.FORBIDDEN_PATTERNS;

import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.product.value.ProductId;
import gift.exception.custom.ProductNotFoundException;
import gift.repository.product.ProductRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable, Role role) {
        if (role.isUser()) {
            return productRepository.findByHiddenFalse(pageable);
        }
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> getProductById(Long id, Role role) {
        Optional<Product> optionalProduct = productRepository.findById(new ProductId(id));
        Product product = optionalProduct.orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && product.isHidden()) {
            throw new ProductNotFoundException(id);
        }
        return Optional.of(product);
    }

    @Override
    public Product createProduct(String name, int price, String imageUrl, Role role) {
        Product newProduct = Product.of(name, price, imageUrl);
        if (role.isUser() && isForbidden(name)) {
            newProduct = newProduct.withHidden(true);
        }
        return productRepository.save(newProduct);
    }

    @Override
    public Product updateProduct(Long id, String name, int price, String imageUrl, Role role) {
        Product existingProduct = productRepository.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && existingProduct.isHidden()) {
            throw new ProductNotFoundException(id);
        }

        existingProduct.changeName(name);
        existingProduct.changePrice(price);
        existingProduct.changeImageUrl(imageUrl);

        if (role.isUser() && isForbidden(name)) {
            existingProduct.changeHidden(true);
        }
        return existingProduct;
    }

    @Override
    public void deleteProduct(Long id, Role role) {
        Product targetProduct = productRepository.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && targetProduct.isHidden()) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(new ProductId(id));
    }

    @Override
    public void hideProduct(Long id, Role role) {
        if (role.isUser()) {
            throw new ProductNotFoundException(id);
        }
        Product targetProduct = productRepository.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.save(targetProduct.withHidden(true));
    }

    @Override
    public void unhideProduct(Long id, Role role) {
        if (role.isUser()) {
            throw new ProductNotFoundException(id);
        }
        Product targetProduct = productRepository.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.save(targetProduct.withHidden(false));
    }

    private boolean isForbidden(String name) {
        return FORBIDDEN_PATTERNS.stream()
                .anyMatch(f -> f.matcher(name).find());
    }
}
