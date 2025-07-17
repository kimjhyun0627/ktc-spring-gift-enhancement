package gift.service.product;

import static gift.entity.product.value.ProductName.FORBIDDEN_PATTERNS;

import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.product.value.ProductId;
import gift.exception.custom.ProductNotFoundException;
import gift.repository.product.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Product> getAllProducts(Role role) {
        List<Product> products = repo.findAll();
        if (role.isUser()) {
            return products.stream()
                    .filter(p -> !p.isHidden())
                    .toList();
        }
        return products;
    }

    @Override
    public Optional<Product> getProductById(Long id, Role role) {
        Optional<Product> optionalProduct = repo.findById(new ProductId(id));
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
        return repo.save(newProduct);
    }

    @Override
    public Product updateProduct(Long id, String name, int price, String imageUrl, Role role) {
        Product existingProduct = repo.findById(new ProductId(id))
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
        Product targetProduct = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && targetProduct.isHidden()) {
            throw new ProductNotFoundException(id);
        }
        repo.deleteById(new ProductId(id));
    }

    @Override
    public void hideProduct(Long id, Role role) {
        if (role.isUser()) {
            throw new ProductNotFoundException(id);
        }
        Product targetProduct = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        repo.save(targetProduct.withHidden(true));
    }

    @Override
    public void unhideProduct(Long id, Role role) {
        if (role.isUser()) {
            throw new ProductNotFoundException(id);
        }
        Product targetProduct = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        repo.save(targetProduct.withHidden(false));
    }

    private boolean isForbidden(String name) {
        return FORBIDDEN_PATTERNS.stream()
                .anyMatch(f -> f.matcher(name).find());
    }
}
