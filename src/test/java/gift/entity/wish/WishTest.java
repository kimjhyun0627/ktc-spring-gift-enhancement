package gift.entity.wish;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.member.value.MemberId;
import gift.entity.wish.value.Amount;
import gift.entity.wish.value.ProductId;
import gift.entity.wish.value.WishId;
import gift.exception.custom.InvalidWishException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WishTest {

    @Test
    @DisplayName("static of() should create Wish with null id and correct values")
    void testOf() {
        Long memberId = 10L;
        Long productId = 20L;
        int amount = 5;

        Wish wish = Wish.of(memberId, productId, amount);

        assertNull(wish.getId(), "id should be null before persistence");
        assertEquals(new MemberId(memberId), wish.getMemberId());
        assertEquals(new ProductId(productId), wish.getProductId());
        assertEquals(new Amount(amount), wish.getAmount());
    }

    @Test
    @DisplayName("withId() should return new Wish with given id and keep other fields")
    void testWithId() {
        Wish original = Wish.of(1L, 2L, 3);
        Wish updated = original.withId(99L);

        assertNotSame(original, updated);
        assertEquals(new WishId(99L), updated.getId());
        // other fields unchanged
        assertEquals(original.getMemberId(), updated.getMemberId());
        assertEquals(original.getProductId(), updated.getProductId());
        assertEquals(original.getAmount(), updated.getAmount());
    }

    @Test
    @DisplayName("withMember() should return new Wish with updated memberId")
    void testWithMember() {
        Wish original = Wish.of(1L, 2L, 3).withId(10L);
        Wish updated = original.withMember(42L);

        assertNotSame(original, updated);
        assertEquals(new MemberId(42L), updated.getMemberId());
        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getProductId(), updated.getProductId());
        assertEquals(original.getAmount(), updated.getAmount());
    }

    @Test
    @DisplayName("withProductId() should return new Wish with updated productId")
    void testWithProductId() {
        Wish original = Wish.of(1L, 2L, 3).withId(10L);
        Wish updated = original.withProductId(99L);

        assertNotSame(original, updated);
        assertEquals(new ProductId(99L), updated.getProductId());
        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getMemberId(), updated.getMemberId());
        assertEquals(original.getAmount(), updated.getAmount());
    }

    @Test
    @DisplayName("withAmount() should return new Wish with updated amount")
    void testWithAmount() {
        Wish original = Wish.of(1L, 2L, 3).withId(10L);
        Wish updated = original.withAmount(77);

        assertNotSame(original, updated);
        assertEquals(new Amount(77), updated.getAmount());
        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getMemberId(), updated.getMemberId());
        assertEquals(original.getProductId(), updated.getProductId());
    }

    @Test
    @DisplayName("getters should return defensive copies")
    void testGetters() {
        Wish wish = Wish.of(1L, 2L, 3).withId(5L);

        WishId idCopy = wish.getId();
        assertEquals(new WishId(5L), idCopy);

        MemberId memberCopy = wish.getMemberId();
        assertEquals(new MemberId(1L), memberCopy);

        ProductId productCopy = wish.getProductId();
        assertEquals(new ProductId(2L), productCopy);

        Amount amountCopy = wish.getAmount();
        assertEquals(new Amount(3), amountCopy);
    }

    @Test
    @DisplayName("constructor should enforce non-null and valid values")
    void testInvalidArguments() {
        assertThrows(NullPointerException.class, () -> Wish.of(null, 2L, 3));
        assertThrows(NullPointerException.class, () -> Wish.of(1L, null, 3));
        assertThrows(InvalidWishException.class, () -> Wish.of(1L, 2L, 0));
    }
}
