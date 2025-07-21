package gift.dto.product.option;

import gift.entity.product.option.ProductOption;

public record OptionResponse(
        Long id,
        String name,
        int quantity
) {

    public static OptionResponse of(ProductOption option) {
        return new OptionResponse(
                option.getId(),
                option.getName().name(),
                option.getQuantity().quantity()
        );
    }
}
