package gift.entity.product.option.value;

import gift.exception.custom.InvalidOptionDecreaseException;
import gift.exception.custom.InvalidOptionException;
import jakarta.persistence.Embeddable;

@Embeddable
public record OptionQuantity(
        int quantity
) {

    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 100_000_000 - 1;

    public OptionQuantity {
        if (quantity < MIN_QUANTITY) {
            throw new InvalidOptionException("수량은 최소 " + MIN_QUANTITY + "개 이상이어야 합니다");
        }
        if (quantity > MAX_QUANTITY) {
            throw new InvalidOptionException("수량은 " + (MAX_QUANTITY + 1) + "개 미만이어야 합니다");
        }
    }

    public OptionQuantity decreaseBy(int amount) {
        int remaining = this.quantity - amount;
        if (remaining < 0) {
            throw new InvalidOptionDecreaseException();
        }
        return new OptionQuantity(remaining);
    }
}
