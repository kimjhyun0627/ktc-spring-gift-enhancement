package gift.service.product;

import gift.entity.member.value.Role;
import gift.entity.product.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts(Role role);

    Optional<Product> getProductById(Long id, Role role);

    Product createProduct(String name, int price, String imageUrl, Role role);

    Product updateProduct(Long id, String name, int price, String imageUrl, Role role);

    void deleteProduct(Long id, Role role);

    void hideProduct(Long id, Role role);

    void unhideProduct(Long id, Role role);
}
