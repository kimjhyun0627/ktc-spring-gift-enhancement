package gift.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import gift.dto.product.option.OptionResponse;
import gift.entity.product.Product;
import gift.entity.product.option.ProductOption;
import gift.entity.product.value.ProductId;
import gift.exception.custom.OptionAlreadyExistException;
import gift.exception.custom.OptionNotFoundException;
import gift.exception.custom.ProductNotFoundException;
import gift.repository.product.ProductRepository;
import gift.repository.product.option.ProductOptionRepository;
import gift.service.product.option.ProductOptionServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductOptionServiceImpl 단위 테스트")
class ProductOptionServiceTest {

    private final Long PRODUCT_ID = 1L;
    private final ProductId PRODUCT_ID_WRAPPER = new ProductId(PRODUCT_ID);
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductOptionRepository optionRepository;
    @InjectMocks
    private ProductOptionServiceImpl service;
    private Product dummyProduct;

    @BeforeEach
    void setUp() {
        dummyProduct = mock(Product.class);
    }

    @Test
    @DisplayName("getOptions: 존재하는 상품 시 옵션 리스트 반환")
    void getOptions_success() {
        // given
        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));

        ProductOption o1 = mock(ProductOption.class);
        ProductOption o2 = mock(ProductOption.class);
        OptionResponse r1 = new OptionResponse(10L, "Opt1", 5);
        OptionResponse r2 = new OptionResponse(11L, "Opt2", 3);
        willReturn(r1).given(o1).toResponse();
        willReturn(r2).given(o2).toResponse();

        willReturn(List.of(o1, o2))
                .given(optionRepository).findAllByProduct_Id(eq(PRODUCT_ID_WRAPPER));

        // when
        List<OptionResponse> responses = service.getOptions(PRODUCT_ID);

        // then
        assertThat(responses)
                .containsExactly(r1, r2);
        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
        then(optionRepository).should().findAllByProduct_Id(eq(PRODUCT_ID_WRAPPER));
    }

    @Test
    @DisplayName("getOptions: 없는 상품 조회 시 ProductNotFoundException")
    void getOptions_productNotFound() {
        willReturn(Optional.empty())
                .given(productRepository).findById(any(ProductId.class));

        assertThatThrownBy(() -> service.getOptions(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class);

        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
    }

    @Test
    @DisplayName("addOption: 신규 옵션 저장 후 응답 반환")
    void addOption_success() {
        // given
        String name = "NewOpt";
        int qty = 7;
        ProductOption unsaved = ProductOption.of(name, qty);
        ProductOption saved = mock(ProductOption.class);
        OptionResponse expected = new OptionResponse(20L, name, qty);

        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));
        willReturn(false)
                .given(optionRepository)
                .existsByProduct_IdAndName_Name(eq(PRODUCT_ID_WRAPPER), eq(name));
        willReturn(saved)
                .given(optionRepository).save(any(ProductOption.class));
        willReturn(expected)
                .given(saved).toResponse();

        // when
        OptionResponse actual = service.addOption(PRODUCT_ID, name, qty);

        // then
        assertThat(actual).isEqualTo(expected);
        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
        then(optionRepository).should()
                .existsByProduct_IdAndName_Name(eq(PRODUCT_ID_WRAPPER), eq(name));
        then(optionRepository).should().save(any(ProductOption.class));
    }

    @Test
    @DisplayName("addOption: 이미 존재하는 옵션이면 OptionAlreadyExistException")
    void addOption_alreadyExists() {
        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));
        willReturn(true)
                .given(optionRepository)
                .existsByProduct_IdAndName_Name(eq(PRODUCT_ID_WRAPPER), eq("dup"));

        assertThatThrownBy(() -> service.addOption(PRODUCT_ID, "dup", 1))
                .isInstanceOf(OptionAlreadyExistException.class);

        then(optionRepository).should()
                .existsByProduct_IdAndName_Name(eq(PRODUCT_ID_WRAPPER), eq("dup"));
    }

    @Test
    @DisplayName("addOption: 상품 없음 시 ProductNotFoundException")
    void addOption_productNotFound() {
        willReturn(Optional.empty())
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));

        assertThatThrownBy(() -> service.addOption(PRODUCT_ID, "x", 1))
                .isInstanceOf(ProductNotFoundException.class);

        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
    }

    @Test
    @DisplayName("decreaseOption: 옵션 존재 시 decrease 호출")
    void decreaseOption_success() {
        // given
        ProductOption opt = mock(ProductOption.class);
        int amount = 2;
        willReturn(Optional.of(opt))
                .given(optionRepository).findById(eq(amount + 5L)); // arbitrary id

        // when
        service.decreaseOption(amount + 5L, amount);

        // then
        verify(opt).decrease(amount);
        then(optionRepository).should().findById(eq(amount + 5L));
    }

    @Test
    @DisplayName("decreaseOption: 옵션 없으면 OptionNotFoundException")
    void decreaseOption_notFound() {
        long optId = 77L;
        willReturn(Optional.empty())
                .given(optionRepository).findById(eq(optId));

        assertThatThrownBy(() -> service.decreaseOption(optId, 1))
                .isInstanceOf(OptionNotFoundException.class);

        then(optionRepository).should().findById(eq(optId));
    }
}
