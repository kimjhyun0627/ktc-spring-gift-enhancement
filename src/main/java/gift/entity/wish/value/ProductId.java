package gift.entity.wish.value;

import java.util.Objects;

public record ProductId(Long productId) {

    public ProductId {
        Objects.requireNonNull(productId, "ProductId는 null이 될 수 없습니다.");
    }
}
