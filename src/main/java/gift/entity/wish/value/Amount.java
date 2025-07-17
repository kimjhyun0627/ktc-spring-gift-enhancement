package gift.entity.wish.value;

import gift.exception.custom.InvalidWishException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Amount(
        @Column(name = "amount", nullable = false)
        int amount
) {

    public Amount {
        if (amount < 1) {
            throw new InvalidWishException("상품 수량은 1개 이상이어야 합니다.");
        }
    }
}
