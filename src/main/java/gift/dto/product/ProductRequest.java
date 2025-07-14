package gift.dto.product;

import gift.entity.product.Product;
import gift.entity.product.value.ProductName;
import gift.entity.product.value.ProductPrice;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank(message = "상품 이름은 필수 입력값입니다.")
        @Size(max = ProductName.MAX_LENGTH,
                message = "상품 이름은 공백 포함 최대 " + ProductName.MAX_LENGTH + "자까지 입력할 수 있습니다.")
        @Pattern(regexp = ProductName.ALLOWED_CHAR,
                message = "상품 이름에 허용되지 않는 문자가 포함되었습니다.")
        String name,

        @Min(value = ProductPrice.MIN_PRICE,
                message = "가격은 " + ProductPrice.MIN_PRICE + "원 이상이어야 합니다.")
        int price,

        @NotBlank(message = "이미지 URL은 필수 입력값입니다.")
        String imageUrl
) {

    public Product toEntity() {
        return Product.of(this.name, this.price, this.imageUrl);
    }
}
