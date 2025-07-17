package gift.entity.wish;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.wish.value.Amount;
import gift.entity.wish.value.WishId;
import gift.exception.custom.InvalidWishException;
import gift.fixture.MemberFixture;
import gift.fixture.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WishTest {

    @Test
    @DisplayName("of(Member, Product, int) 팩토리 메서드는 null id와 올바른 필드 값을 설정한다")
    void testOf() {
        Member member = MemberFixture.newRegisteredMember(10L, "user@example.com", "asdfasdf",
                Role.USER);
        Product product = ProductFixture.create(20L, "P", 100, "http://asdf.png", false);
        int quantity = 5;

        Wish wish = Wish.of(member, product, quantity).withId(1L);

        assertThat(wish.getId()).isEqualTo(new WishId(1L));
        assertThat(wish.getMember().getId().id()).isEqualTo(member.getId().id());
        assertThat(wish.getProduct().getId().id()).isEqualTo(product.getId().id());
        assertThat(wish.getAmount()).isEqualTo(new Amount(quantity));
    }

    @Test
    @DisplayName("withId은 새로운 id를 가진 복사본을 반환한다")
    void testWithId() {
        Member member = MemberFixture.newRegisteredMember(1L, "a@a.com", "asdfasdf", Role.USER);
        Product product = ProductFixture.visible();
        Wish original = Wish.of(member, product, 3);

        Wish updated = original.withId(99L);

        assertThat(updated).isNotSameAs(original);
        assertThat(updated.getId()).isEqualTo(new WishId(99L));
        assertThat(updated.getMember()).isEqualTo(original.getMember());
        assertThat(updated.getProduct()).isEqualTo(original.getProduct());
        assertThat(updated.getAmount()).isEqualTo(original.getAmount());
    }

    @Test
    @DisplayName("withAmount은 새로운 amount를 가진 복사본을 반환한다")
    void testWithAmount() {
        Member member = MemberFixture.newRegisteredMember(1L, "a@a.com", "asdfasdf", Role.USER);
        Product product = ProductFixture.visible();
        Wish original = Wish.of(member, product, 3).withId(5L);

        Wish updated = original.withAmount(7);

        assertThat(updated).isNotSameAs(original);
        assertThat(updated.getAmount()).isEqualTo(new Amount(7));
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getMember()).isEqualTo(original.getMember());
        assertThat(updated.getProduct()).isEqualTo(original.getProduct());
    }

    @Test
    @DisplayName("isOwnedBy는 소유한 멤버를 올바르게 판별한다")
    void testIsOwnedBy() {
        Member owner = MemberFixture.newRegisteredMember(1L, "u@u.com", "asdfasdf", Role.USER);
        Member other = MemberFixture.newRegisteredMember(2L, "v@v.com", "asdfasdf", Role.USER);
        Wish wish = Wish.of(owner, ProductFixture.visible(), 1);

        assertThat(wish.isOwnedBy(owner)).isTrue();
        assertThat(wish.isOwnedBy(other)).isFalse();
    }

    @Test
    @DisplayName("isForProduct는 해당 상품인지 올바르게 판별한다")
    void testIsForProduct() {
        Product p1 = ProductFixture.create(100L, "Name", 1000, "http://asdf.png", false);
        Wish wish = Wish.of(MemberFixture.visible(), p1, 2);

        assertThat(wish.isForProduct(100L)).isTrue();
        assertThat(wish.isForProduct(999L)).isFalse();
    }

    @Test
    @DisplayName("of 호출 시 null member/product 또는 0 이하 amount는 예외를 던진다")
    void testInvalidArguments() {
        Member member = MemberFixture.visible();
        Product product = ProductFixture.visible();

        assertThrows(InvalidWishException.class, () -> Wish.of(member, product, 0));
    }
}
