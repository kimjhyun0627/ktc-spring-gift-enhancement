package gift.entity.wish;

import gift.entity.member.value.MemberId;
import gift.entity.wish.value.Amount;
import gift.entity.wish.value.ProductId;
import gift.entity.wish.value.WishId;

public class Wish {

    private WishId id;

    private MemberId memberId;

    private ProductId productId;

    private Amount amount;

    protected Wish() {
    }

    private Wish(WishId id, MemberId member, ProductId productId, Amount amount) {
        this.id = id;
        this.memberId = member;
        this.productId = productId;
        this.amount = amount;
    }

    public static Wish of(Long memberId, Long productId, int amount) {
        return new Wish(
                null,
                new MemberId(memberId),
                new ProductId(productId),
                new Amount(amount)
        );
    }

    public Wish withId(Long id) {
        return new Wish(
                new WishId(id),
                this.memberId,
                this.productId,
                this.amount
        );
    }

    public Wish withMember(Long memberId) {
        return new Wish(
                this.id,
                new MemberId(memberId),
                this.productId,
                this.amount
        );
    }

    public Wish withProductId(Long productId) {
        return new Wish(
                this.id,
                this.memberId,
                new ProductId(productId),
                this.amount
        );
    }

    public Wish withAmount(int amount) {
        return new Wish(
                this.id,
                this.memberId,
                this.productId,
                new Amount(amount)
        );
    }

    public boolean isOwnedBy(Long memberId) {
        return this.memberId.id().equals(memberId);
    }

    public boolean isForProduct(Long productId) {
        return this.productId.productId().equals(productId);
    }

    public WishId getId() {
        return id == null ? null : new WishId(id.id());
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public ProductId getProductId() {
        return new ProductId(productId.productId());
    }

    public Amount getAmount() {
        return new Amount(amount.amount());
    }
}
