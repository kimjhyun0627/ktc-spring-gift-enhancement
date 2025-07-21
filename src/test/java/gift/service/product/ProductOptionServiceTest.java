package gift.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import gift.dto.product.option.OptionResponse;
import gift.entity.member.value.Role;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

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
        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));

        ProductOption o1 = ProductOption.of(dummyProduct, "Opt1", 5);
        ProductOption o2 = ProductOption.of(dummyProduct, "Opt2", 3);
        willReturn(List.of(o1, o2))
                .given(optionRepository)
                .findAllByProduct_Id(eq(PRODUCT_ID_WRAPPER));

        List<OptionResponse> responses = service.getOptions(PRODUCT_ID, Role.ADMIN);

        OptionResponse expected1 = OptionResponse.of(o1);
        OptionResponse expected2 = OptionResponse.of(o2);
        assertThat(responses).containsExactly(expected1, expected2);

        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
        then(optionRepository).should().findAllByProduct_Id(eq(PRODUCT_ID_WRAPPER));
    }

    @Test
    @DisplayName("getOptions: 없는 상품 조회 시 ProductNotFoundException")
    void getOptions_productNotFound() {
        willReturn(Optional.empty())
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));

        assertThatThrownBy(() -> service.getOptions(PRODUCT_ID, Role.USER))
                .isInstanceOf(ProductNotFoundException.class);

        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
    }

    @Test
    @DisplayName("addOption: 신규 옵션 저장 후 응답 반환")
    void addOption_success() {
        String name = "NewOpt";
        int qty = 7;

        willReturn(Optional.of(dummyProduct))
                .given(productRepository)
                .findById(eq(PRODUCT_ID_WRAPPER));

        ProductOption created = ProductOption.of(dummyProduct, name, qty);
        ReflectionTestUtils.setField(created, "id", 20L);
        willReturn(created)
                .given(optionRepository)
                .save(any(ProductOption.class));

        OptionResponse actual = service.addOption(PRODUCT_ID, name, qty, Role.ADMIN);

        OptionResponse expected = OptionResponse.of(created);
        assertThat(actual).isEqualTo(expected);

        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
        then(optionRepository).should(times(1)).save(any(ProductOption.class));
        then(optionRepository).should(never())
                .existsByProduct_IdAndName_Name(any(), any());
    }


    @Test
    @DisplayName("addOption: 이미 존재하는 옵션이면 OptionAlreadyExistException")
    void addOption_alreadyExists() {
        willReturn(Optional.of(dummyProduct))
                .given(productRepository)
                .findById(eq(PRODUCT_ID_WRAPPER));

        willThrow(new DataIntegrityViolationException("uk_option_product_name"))
                .given(optionRepository)
                .save(any(ProductOption.class));

        assertThatThrownBy(() ->
                service.addOption(PRODUCT_ID, "dup", 1, Role.ADMIN)
        ).isInstanceOf(OptionAlreadyExistException.class);

        then(optionRepository).should().save(any(ProductOption.class));
        then(optionRepository).should(never()).existsByProduct_IdAndName_Name(any(), any());
    }


    @Test
    @DisplayName("addOption: 상품 없음 시 ProductNotFoundException")
    void addOption_productNotFound() {
        willReturn(Optional.empty())
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));

        assertThatThrownBy(() -> service.addOption(PRODUCT_ID, "x", 1, Role.ADMIN))
                .isInstanceOf(ProductNotFoundException.class);

        then(productRepository).should().findById(eq(PRODUCT_ID_WRAPPER));
    }

    @Test
    @DisplayName("decreaseOption: 옵션이 해당 상품에 속하면 decrease 호출")
    void decreaseOption_success() {
        long optionId = 5L;
        int amount = 2;

        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));

        ProductOption opt = mock(ProductOption.class);
        willReturn(Optional.of(opt))
                .given(optionRepository).findById(eq(optionId));

        willReturn(dummyProduct).given(opt).getProduct();
        willReturn(PRODUCT_ID_WRAPPER).given(dummyProduct).getId();

        service.decreaseOptionAmount(PRODUCT_ID, optionId, amount, Role.ADMIN);

        then(optionRepository).should().findById(eq(optionId));
        then(opt).should().decreaseAmount(amount);
    }

    @Test
    @DisplayName("decreaseOption: 옵션이 다른 상품에 속하면 OptionNotFoundException")
    void decreaseOption_wrongProduct() {
        long optionId = 5L;
        int amount = 1;

        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));
        ProductOption opt = mock(ProductOption.class);
        willReturn(Optional.of(opt))
                .given(optionRepository).findById(eq(optionId));

        Product otherProduct = mock(Product.class);
        willReturn(new ProductId(2L)).given(otherProduct).getId();
        willReturn(otherProduct).given(opt).getProduct();

        assertThatThrownBy(() ->
                service.decreaseOptionAmount(PRODUCT_ID, optionId, amount, Role.ADMIN)
        ).isInstanceOf(OptionNotFoundException.class);
    }

    @Test
    @DisplayName("decreaseOption: 옵션 없으면 OptionNotFoundException")
    void decreaseOption_notFound() {
        long optionId = 77L;
        willReturn(Optional.of(dummyProduct))
                .given(productRepository).findById(eq(PRODUCT_ID_WRAPPER));
        willReturn(Optional.empty())
                .given(optionRepository).findById(eq(optionId));

        assertThatThrownBy(() -> service.decreaseOptionAmount(PRODUCT_ID, optionId, 1, Role.ADMIN))
                .isInstanceOf(OptionNotFoundException.class);
    }
}
