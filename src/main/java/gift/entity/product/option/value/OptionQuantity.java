package gift.entity.product.option.value;

import gift.exception.custom.InvalidOptionDecreaseException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Embeddable
public record OptionQuantity(
        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다")
        @Max(value = MAX_OPT_QUANTITY_SIZE,
                message = "수량은 " + (MAX_OPT_QUANTITY_SIZE + 1) + "개 미만이어야 합니다")
        int quantity
) {

    private static final int MAX_OPT_QUANTITY_SIZE = 99_999_999;

    public OptionQuantity {
    }

    public OptionQuantity decreaseBy(int amount) {
        int remaining = this.quantity - amount;
        if (remaining < 0) {
            throw new InvalidOptionDecreaseException();
        }
        return new OptionQuantity(remaining);
    }
}
