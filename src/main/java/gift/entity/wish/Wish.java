package gift.entity.wish;

import gift.entity.member.Member;
import gift.entity.product.Product;
import gift.entity.wish.value.Amount;
import gift.entity.wish.value.WishId;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table(
        name = "wish",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "product_id"})
)
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    private Amount amount;

    protected Wish() {

    }

    private Wish(Long id, Member member, Product product, Amount amount) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.amount = amount;
    }

    public static Wish of(Member member, Product product, int quantity) {
        return new Wish(
                null,
                member,
                product,
                new Amount(quantity)
        );
    }

    public Wish withId(Long newId) {
        return new Wish(
                new WishId(newId).id(),
                this.member,
                this.product,
                this.amount
        );
    }

    public Wish withAmount(int amount) {
        return new Wish(
                this.id,
                this.member,
                this.product,
                new Amount(amount)
        );
    }

    public boolean isOwnedBy(Member member) {
        return this.member.getId().id().equals(member.getId().id());
    }

    public boolean isForProduct(Long productId) {
        return this.product.getId().id().equals(productId);
    }

    public WishId getId() {
        return new WishId(id);
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wish w)) {
            return false;
        }
        return Objects.equals(id, w.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
