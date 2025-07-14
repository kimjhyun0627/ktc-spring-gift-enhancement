package gift.entity.product.value;

import gift.exception.custom.InvalidProductException;

public record ProductPrice(int price) {

    public static final int MIN_PRICE = 1;

    public ProductPrice {
        if (price < MIN_PRICE) {
            throw new InvalidProductException("가격은 1원 이상이어야 합니다.");
        }
    }
}
