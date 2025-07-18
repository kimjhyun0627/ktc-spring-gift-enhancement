package gift.service.product;

import gift.entity.member.value.Role;
import gift.entity.product.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<Product> getAllProducts(Pageable pageable, Role role);

    Optional<Product> getProductById(Long id, Role role);

    Product createProduct(String name, int price, String imageUrl, Role role);

    Product updateProduct(Long id, String name, int price, String imageUrl, Role role);

    void deleteProduct(Long id, Role role);

    void hideProduct(Long id, Role role);

    void unhideProduct(Long id, Role role);
}
