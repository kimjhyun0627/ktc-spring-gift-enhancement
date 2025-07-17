package gift.entity.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        assertNotNull(p.getId(), "ProductId should not be null");
        assertEquals(id, p.getId().id(), "ID should match");
        assertEquals(name, p.getName().name(), "Name should match");
        assertEquals(price, p.getPrice().price(), "Price should match");
        assertEquals(url, p.getImageUrl().url(), "Image URL should match");
        assertTrue(p.isHidden(), "Hidden flag should match");
    }

    @Test
    void of_withoutHidden_defaultsToNotHidden() {
        String url = "https://cdn.test.com/images/default.png";
        Product p = Product.of(1L, "A", 1000, url);
        assertFalse(p.isHidden(), "Default hidden should be false");
        assertEquals(url, p.getImageUrl().url(), "Image URL should match provided url");
    }

    @Test
    void of_withoutId_createsNullId() {
        String url = "https://cdn.test.com/images/item.png";
        Product p = Product.of("B", 2000, url);
        assertNull(p.getId(), "ID should be null when created without id");
        assertEquals("B", p.getName().name());
        assertEquals(2000, p.getPrice().price());
        assertEquals(url, p.getImageUrl().url());
    }

    @Test
    void withId_setsNewIdOnly() {
        Product base = Product.of("C", 1500, "http://assets.test.com/c.png");
        Product changed = base.withId(99L);
        assertNotNull(changed.getId());
        assertEquals(99L, changed.getId().id());
        assertEquals(base.getName(), changed.getName());
        assertEquals(base.getPrice(), changed.getPrice());
        assertEquals(base.getImageUrl(), changed.getImageUrl());
        assertEquals(base.isHidden(), changed.isHidden());
    }

    @Test
    void withName_updatesNameOnly() {
        String url = "http://cdn.test.com/old.png";
        Product base = Product.of(5L, "Old", 3000, url, false);
        Product changed = base.withName("New");
        assertEquals("New", changed.getName().name());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getPrice(), changed.getPrice());
        assertEquals(base.getImageUrl(), changed.getImageUrl());
        assertEquals(base.isHidden(), changed.isHidden());
    }

    @Test
    void withPrice_updatesPriceOnly() {
        String url = "https://cdn.test.com/y.png";
        Product base = Product.of(6L, "P", 4000, url, true);
        Product changed = base.withPrice(4500);
        assertEquals(4500, changed.getPrice().price());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getName(), changed.getName());
        assertEquals(base.getImageUrl(), changed.getImageUrl());
        assertEquals(base.isHidden(), changed.isHidden());
    }

    @Test
    void withImageUrl_updatesImageOnly() {
        Product base = Product.of(7L, "Q", 2500, "http://cdn.test.com/oldUrl.jpg", false);
        String newUrl = "http://cdn.test.com/newUrl.jpg";
        Product changed = base.withImageUrl(newUrl);
        assertEquals(newUrl, changed.getImageUrl().url());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getName(), changed.getName());
        assertEquals(base.getPrice(), changed.getPrice());
        assertEquals(base.isHidden(), changed.isHidden());
    }

    @Test
    void withHidden_updatesHiddenOnly() {
        String url = "http://cdn.test.com/z.png";
        Product base = Product.of(8L, "R", 3500, url, false);
        Product changed = base.withHidden(true);
        assertTrue(changed.isHidden());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getName(), changed.getName());
        assertEquals(base.getPrice(), changed.getPrice());
        assertEquals(base.getImageUrl(), changed.getImageUrl());
    }

    @Test
    void toResponse_mapsCorrectly() {
        String url = "http://example.com/images/productS.png";
        Product p = Product.of(3L, "S", 750, url, false);
        ProductResponse resp = p.toResponse();

        assertEquals(p.getId().id(), resp.id());
        assertEquals(p.getName().name(), resp.name());
        assertEquals(p.getPrice().price(), resp.price());
        assertEquals(p.getImageUrl().url(), resp.imageUrl());
    }
}
