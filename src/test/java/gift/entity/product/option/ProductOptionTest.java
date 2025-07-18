package gift.entity.product.option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.entity.product.Product;
import gift.exception.custom.InvalidOptionDecreaseException;
import org.junit.jupiter.api.Test;

class ProductOptionTest {

    @Test
    void ofShouldCreateWithCorrectValues() {
        ProductOption opt = ProductOption.of("Test", 20);
        assertThat(opt.getId()).isNull();
        assertThat(opt.getName().name()).isEqualTo("Test");
        assertThat(opt.getQuantity().quantity()).isEqualTo(20);
    }

    @Test
    void assignToShouldSetProduct() {
        Product product = Product.of("P", 100, "http://asdf.png");
        ProductOption opt = ProductOption.of("Opt", 5);
        opt.assignTo(product);
        assertThat(opt.getProduct()).isEqualTo(product);
    }

    @Test
    void decreaseShouldReduceQuantity() {
        ProductOption opt = ProductOption.of("Opt", 10);
        opt.decrease(4);
        assertThat(opt.getQuantity().quantity()).isEqualTo(6);
    }

    @Test
    void decreaseTooMuchShouldThrow() {
        ProductOption opt = ProductOption.of("Opt", 2);
        assertThatThrownBy(() -> opt.decrease(5))
                .isInstanceOf(InvalidOptionDecreaseException.class);
    }
}
