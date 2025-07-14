package gift.entity.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gift.dto.product.ProductResponse;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void of_withAllFields_setsFieldsCorrectly() {
        Long id = 10L;
        String name = "Widget";
        int price = 5000;
        String url = "http://example.com/images/widget.png";
        boolean hidden = true;

        Product p = Product.of(id, name, price, url, hidden);

        assertNotNull(p.id(), "ProductId should not be null");
        assertEquals(id, p.id().id(), "ID should match");
        assertEquals(name, p.name().name(), "Name should match");
        assertEquals(price, p.price().price(), "Price should match");
        assertEquals(url, p.imageUrl().url(), "Image URL should match");
        assertTrue(p.hidden(), "Hidden flag should match");
    }

    @Test
    void of_withoutHidden_defaultsToNotHidden() {
        String url = "https://cdn.test.com/images/default.png";
        Product p = Product.of(1L, "A", 1000, url);
        assertFalse(p.hidden(), "Default hidden should be false");
        assertEquals(url, p.imageUrl().url(), "Image URL should match provided url");
    }

    @Test
    void of_withoutId_createsNullId() {
        String url = "https://cdn.test.com/images/item.png";
        Product p = Product.of("B", 2000, url);
        assertNull(p.id(), "ID should be null when created without id");
        assertEquals("B", p.name().name());
        assertEquals(2000, p.price().price());
        assertEquals(url, p.imageUrl().url());
    }

    @Test
    void withId_setsNewIdOnly() {
        Product base = Product.of("C", 1500, "http://assets.test.com/c.png");
        Product changed = base.withId(99L);
        assertNotNull(changed.id());
        assertEquals(99L, changed.id().id());
        assertEquals(base.name(), changed.name());
        assertEquals(base.price(), changed.price());
        assertEquals(base.imageUrl(), changed.imageUrl());
        assertEquals(base.hidden(), changed.hidden());
    }

    @Test
    void withName_updatesNameOnly() {
        String url = "http://cdn.test.com/old.png";
        Product base = Product.of(5L, "Old", 3000, url, false);
        Product changed = base.withName("New");
        assertEquals("New", changed.name().name());
        assertEquals(base.id(), changed.id());
        assertEquals(base.price(), changed.price());
        assertEquals(base.imageUrl(), changed.imageUrl());
        assertEquals(base.hidden(), changed.hidden());
    }

    @Test
    void withPrice_updatesPriceOnly() {
        String url = "https://cdn.test.com/y.png";
        Product base = Product.of(6L, "P", 4000, url, true);
        Product changed = base.withPrice(4500);
        assertEquals(4500, changed.price().price());
        assertEquals(base.id(), changed.id());
        assertEquals(base.name(), changed.name());
        assertEquals(base.imageUrl(), changed.imageUrl());
        assertEquals(base.hidden(), changed.hidden());
    }

    @Test
    void withImageUrl_updatesImageOnly() {
        Product base = Product.of(7L, "Q", 2500, "http://cdn.test.com/oldUrl.jpg", false);
        String newUrl = "http://cdn.test.com/newUrl.jpg";
        Product changed = base.withImageUrl(newUrl);
        assertEquals(newUrl, changed.imageUrl().url());
        assertEquals(base.id(), changed.id());
        assertEquals(base.name(), changed.name());
        assertEquals(base.price(), changed.price());
        assertEquals(base.hidden(), changed.hidden());
    }

    @Test
    void withHidden_updatesHiddenOnly() {
        String url = "http://cdn.test.com/z.png";
        Product base = Product.of(8L, "R", 3500, url, false);
        Product changed = base.withHidden(true);
        assertTrue(changed.hidden());
        assertEquals(base.id(), changed.id());
        assertEquals(base.name(), changed.name());
        assertEquals(base.price(), changed.price());
        assertEquals(base.imageUrl(), changed.imageUrl());
    }

    @Test
    void toResponse_mapsCorrectly() {
        String url = "http://example.com/images/productS.png";
        Product p = Product.of(3L, "S", 750, url, false);
        ProductResponse resp = p.toResponse();

        assertEquals(p.id().id(), resp.id());
        assertEquals(p.name().name(), resp.name());
        assertEquals(p.price().price(), resp.price());
        assertEquals(p.imageUrl().url(), resp.imageUrl());
    }

    @Test
    void equalsAndHashCode_basedOnId() {
        Product p1 = Product.of(100L, "X", 100, "http://cdn.test.com/u.png", false);
        Product p2 = Product.of(100L, "Y", 200, "http://cdn.test.com/v.png", true);
        Product p3 = Product.of(101L, "X", 100, "http://cdn.test.com/u.png", false);

        assertEquals(p1, p2, "Products with same id should be equal");
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
    }
}
