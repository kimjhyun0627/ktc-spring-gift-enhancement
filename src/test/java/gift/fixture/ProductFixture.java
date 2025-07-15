package gift.fixture;

import gift.entity.product.Product;

public final class ProductFixture {

    private ProductFixture() {
    }

    public static Product create(
            Long id,
            String name,
            int price,
            String imageUrl,
            boolean hidden
    ) {
        return Product.of(id, name, price, imageUrl, hidden);
    }

    public static Product createNoId(String name, int price, String imageUrl, boolean hidden) {
        return Product.of(null, name, price, imageUrl, hidden);
    }

    public static Product visible(
            Long id,
            String name,
            int price,
            String imageUrl
    ) {
        return create(id, name, price, imageUrl, false);
    }

    public static Product hidden(
            Long id,
            String name,
            int price,
            String imageUrl
    ) {
        return create(id, name, price, imageUrl, true);
    }

    public static Product visible() {
        return create(1L, "Product", 100, "http://asdf.png", false);
    }

    public static Product hidden() {
        return create(1L, "Product", 100, "http://asdf.png", true);
    }
}
