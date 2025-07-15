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
        List<Product> all = repo.findAll();
        if (role.isUser()) {
            return all.stream()
                    .filter(p -> !p.isHidden())
                    .toList();
        }
        return all;
    }

    @Override
    public Optional<Product> getProductById(Long id, Role role) {
        Optional<Product> opt = repo.findById(new ProductId(id));
        Product p = opt.orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && p.isHidden()) {
            throw new ProductNotFoundException(id);
        }
        return Optional.of(p);
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
        Product existing = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && existing.isHidden()) {
            throw new ProductNotFoundException(id);
        }

        Product updated = existing.withName(name)
                .withPrice(price)
                .withImageUrl(imageUrl);

        if (role.isUser() && isForbidden(name)) {
            updated = updated.withHidden(true);
        }
        return repo.save(updated);
    }

    @Override
    public void deleteProduct(Long id, Role role) {
        Product existing = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (role.isUser() && existing.isHidden()) {
            throw new ProductNotFoundException(id);
        }
        repo.deleteById(new ProductId(id));
    }

    @Override
    public void hideProduct(Long id, Role role) {
        if (role.isUser()) {
            throw new ProductNotFoundException(id);
        }
        Product p = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        repo.save(p.withHidden(true));
    }

    @Override
    public void unhideProduct(Long id, Role role) {
        if (role.isUser()) {
            throw new ProductNotFoundException(id);
        }
        Product p = repo.findById(new ProductId(id))
                .orElseThrow(() -> new ProductNotFoundException(id));
        repo.save(p.withHidden(false));
    }

    private boolean isForbidden(String name) {
        return FORBIDDEN_PATTERNS.stream()
                .anyMatch(f -> f.matcher(name).find());
    }
}
