package gift.repository.product.option;

import gift.entity.product.option.ProductOption;
import gift.entity.product.value.ProductId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findAllByProduct_Id(ProductId id);

    boolean existsByProduct_IdAndName_Name(ProductId productId, String name);
}

