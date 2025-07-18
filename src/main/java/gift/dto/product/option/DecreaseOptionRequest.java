package gift.dto.product.option;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DecreaseOptionRequest(
        @NotNull(message = "수량은 null이 될 수 없습니다.")
        @Min(value = 1, message = "최소 1개 이상의 수량이 필요합니다.")
        Integer amount
) {

}
