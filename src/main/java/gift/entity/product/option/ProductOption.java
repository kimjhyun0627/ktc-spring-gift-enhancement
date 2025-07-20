package gift.entity.product.option;

import gift.dto.product.option.OptionResponse;
import gift.entity.product.Product;
import gift.entity.product.option.value.OptionName;
import gift.entity.product.option.value.OptionQuantity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_option")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Embedded
    @Column(name = "name", nullable = false)
    private OptionName name;

    @Embedded
    @Column(name = "quantity", nullable = false)
    private OptionQuantity quantity;

    protected ProductOption() {
    }

    private ProductOption(OptionName name, OptionQuantity quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public static ProductOption of(String name, int quantity) {
        return new ProductOption(
                new OptionName(name),
                new OptionQuantity(quantity)
        );
    }

    public void assignTo(Product product) {
        this.product = product;
    }

    public void decreaseAmount(int amount) {
        this.quantity = this.quantity.decreaseBy(amount);
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public OptionName getName() {
        return name;
    }

    public OptionQuantity getQuantity() {
        return quantity;
    }

    public OptionResponse toResponse() {
        return OptionResponse.of(this);
    }
}
