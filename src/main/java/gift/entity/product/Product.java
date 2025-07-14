package gift.entity.product;

import gift.dto.product.ProductResponse;
import gift.entity.product.value.ProductId;
import gift.entity.product.value.ProductImageUrl;
import gift.entity.product.value.ProductName;
import gift.entity.product.value.ProductPrice;
import java.util.Objects;

public class Product {

    private final ProductId id;
    private final ProductName name;
    private final ProductPrice price;
    private final ProductImageUrl imageUrl;
    private final boolean hidden;

    private Product(ProductId id, ProductName name, ProductPrice price, ProductImageUrl imageUrl,
            boolean hidden) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.hidden = hidden;
    }

    public static Product of(Long id, String name, int price, String imageUrl, boolean hidden) {
        return new Product(
                new ProductId(id),
                new ProductName(name),
                new ProductPrice(price),
                new ProductImageUrl(imageUrl),
                hidden
        );
    }

    public static Product of(Long id, String name, int price, String imageUrl) {
        return of(id, name, price, imageUrl, false);
    }

    public static Product of(String name, int price, String imageUrl) {
        return new Product(
                null,
                new ProductName(name),
                new ProductPrice(price),
                new ProductImageUrl(imageUrl),
                false
        );
    }

    public Product withId(Long newId) {
        return new Product(new ProductId(newId), name, price, imageUrl, hidden);
    }

    public Product withName(String newName) {
        return new Product(id, new ProductName(newName), price, imageUrl, hidden);
    }

    public Product withPrice(int newPrice) {
        return new Product(id, name, new ProductPrice(newPrice), imageUrl, hidden);
    }

    public Product withImageUrl(String newUrl) {
        return new Product(id, name, price, new ProductImageUrl(newUrl), hidden);
    }

    public Product withHidden(boolean newHidden) {
        return new Product(id, name, price, imageUrl, newHidden);
    }

    public ProductResponse toResponse() {
        return new ProductResponse(id.id(), name.name(), price.price(), imageUrl.url());
    }

    public ProductId id() {
        return id;
    }

    public ProductName name() {
        return name;
    }

    public ProductPrice price() {
        return price;
    }

    public ProductImageUrl imageUrl() {
        return imageUrl;
    }

    public boolean hidden() {
        return hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
