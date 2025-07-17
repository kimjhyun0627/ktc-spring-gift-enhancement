package gift.dto.wish;

import gift.entity.product.value.ProductId;
import gift.entity.wish.value.Amount;
import gift.entity.wish.value.WishId;

public record WishResponse(Long wishId, Long productId, int amount) {

    public static WishResponse of(WishId id, ProductId productId, Amount amount) {
        return new WishResponse(id.id(), productId.id(), amount.amount());
    }
}
