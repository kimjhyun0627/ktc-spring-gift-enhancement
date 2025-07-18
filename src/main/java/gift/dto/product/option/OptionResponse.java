package gift.dto.product.option;

public record OptionResponse(
        Long id,
        String name,
        int quantity
) {

    public static OptionResponse of(gift.entity.product.option.ProductOption entity) {
        return new OptionResponse(
                entity.getId(),
                entity.getName().name(),
                entity.getQuantity().quantity()
        );
    }
}
