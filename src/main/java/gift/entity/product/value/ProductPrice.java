package gift.entity.product.value;

import gift.exception.custom.InvalidProductException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductPrice(
        @Column(name = "price", nullable = false)
        int price
) {

    public static final int MIN_PRICE = 1;

    public ProductPrice {
        if (price < MIN_PRICE) {
            throw new InvalidProductException("가격은 " + MIN_PRICE + "원 이상이어야 합니다.");
        }
    }
}
