package gift.entity.product.value;

import gift.exception.custom.InvalidProductException;
import java.util.Objects;

public record ProductId(Long id) {

    public ProductId {
        Objects.requireNonNull(id, "상품 ID는 null일 수 없습니다.");
        if (id < 1) {
            throw new InvalidProductException("상품 ID는 음수이거나 0일 수 없습니다.");
        }
    }
}
