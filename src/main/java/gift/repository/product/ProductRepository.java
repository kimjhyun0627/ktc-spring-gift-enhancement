package gift.repository.product;

import gift.entity.product.Product;
import gift.entity.product.value.ProductId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, ProductId> {

    Optional<Product> findByName_Name(String name);

    Page<Product> findByHiddenFalse(Pageable pageable);
}
