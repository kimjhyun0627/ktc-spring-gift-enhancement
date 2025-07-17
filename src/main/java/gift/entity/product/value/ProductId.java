package gift.entity.product.value;

import gift.exception.custom.InvalidProductException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductId(
        @Column(name = "id")
        Long id
) {

    public ProductId {
        if (id != null && id < 1) {
            throw new InvalidProductException("상품 ID는 음수이거나 0일 수 없습니다.");
        }
    }
}
