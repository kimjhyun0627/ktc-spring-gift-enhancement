package gift.dto.product.option;

import jakarta.validation.constraints.NotNull;

public record OptionRequest(
        @NotNull(message = "상품 옵션은 null이 될 수 없습니다.")
        String name,

        @NotNull(message = "수량은 null이 될 수 없습니다.")
        Integer quantity
) {

}
