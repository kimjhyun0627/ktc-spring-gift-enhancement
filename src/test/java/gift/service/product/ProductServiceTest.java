package gift.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.when;

import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.exception.custom.ProductNotFoundException;
import gift.fixture.ProductFixture;
import gift.repository.product.ProductRepository;
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
@DisplayName("ProductServiceImpl 단위 테스트")
class ProductServiceTest {

    private static final Role USER = Role.USER;
    private static final Role ADMIN = Role.ADMIN;

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductServiceImpl service;

    private Product visibleProduct;
    private Product hiddenProduct;

    @BeforeEach
    void setUp() {
        visibleProduct = ProductFixture.visible(1L, "Item", 100, "http://img.png");
        hiddenProduct = ProductFixture.hidden(2L, "Secret", 200, "http://img2.png");
    }

    @Test
    @DisplayName("getAllProducts: 일반 사용자, 숨김 상품 제외")
    void getAllProducts_asUser_filtersHidden() {
        when(repo.findAll()).thenReturn(List.of(visibleProduct, hiddenProduct));

        List<Product> result = service.getAllProducts(USER);

        assertThat(result).containsExactly(visibleProduct);
        then(repo).should().findAll();
    }

    @Test
    @DisplayName("getAllProducts: 관리자, 모든 상품 반환")
    void getAllProducts_asAdmin_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(visibleProduct, hiddenProduct));

        List<Product> result = service.getAllProducts(ADMIN);

        assertThat(result).containsExactly(visibleProduct, hiddenProduct);
    }

    @Test
    @DisplayName("getProductById: 일반 사용자, 숨김 상품 접근 시 예외")
    void getProductById_userCannotSeeHidden() {
        when(repo.findById(2L)).thenReturn(Optional.of(hiddenProduct));

        assertThatThrownBy(() -> service.getProductById(2L, USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("getProductById: 일반 사용자, 노출 상품 조회 성공")
    void getProductById_userSeesVisible() {
        when(repo.findById(1L)).thenReturn(Optional.of(visibleProduct));

        Optional<Product> result = service.getProductById(1L, USER);

        assertThat(result).isPresent().contains(visibleProduct);
    }

    @Test
    @DisplayName("getProductById: 관리자, 숨김 상품 조회 성공")
    void getProductById_adminSeesHidden() {
        when(repo.findById(2L)).thenReturn(Optional.of(hiddenProduct));

        Optional<Product> result = service.getProductById(2L, ADMIN);

        assertThat(result).isPresent().contains(hiddenProduct);
    }

    @Test
    @DisplayName("getProductById: 없는 상품 조회 시 예외")
    void getProductById_missing_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProductById(99L, ADMIN))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("createProduct: 일반 사용자, 금지된 이름 숨김 처리")
    void createProduct_userForbiddenName_hides() {
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct("카카오톡", 300, "http://image.png", USER);

        assertThat(result.hidden()).isTrue();
        then(repo).should().save(argThat(Product::hidden));
    }

    @Test
    @DisplayName("createProduct: 일반 사용자, 허용된 이름 노출 처리")
    void createProduct_userAllowed_shows() {
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct("Normal", 100, "http://image.png", USER);

        assertThat(result.hidden()).isFalse();
    }

    @Test
    @DisplayName("createProduct: 관리자, 금지된 이름도 노출 처리")
    void createProduct_adminIgnoresForbidden() {
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct("카카오톡", 300, "http://image.png", ADMIN);

        assertThat(result.hidden()).isFalse();
    }

    @Test
    @DisplayName("updateProduct: 일반 사용자, 숨김 상품 수정 시 예외")
    void updateProduct_userCannotUpdateHidden() {
        when(repo.findById(2L)).thenReturn(Optional.of(hiddenProduct));

        assertThatThrownBy(() ->
                service.updateProduct(2L, "New", 150, "http://image.png", USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("updateProduct: 일반 사용자, 금지된 새 이름 숨김 처리")
    void updateProduct_userForbiddenNewName_hides() {
        when(repo.findById(1L)).thenReturn(Optional.of(visibleProduct));
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.updateProduct(1L, "카카오톡", 150, "http://image.png", USER);

        assertThat(result.hidden()).isTrue();
    }

    @Test
    @DisplayName("updateProduct: 관리자, 항상 업데이트")
    void updateProduct_adminAlwaysUpdates() {
        when(repo.findById(1L)).thenReturn(Optional.of(visibleProduct));
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.updateProduct(1L, "NewName", 150, "http://image.png", ADMIN);

        assertThat(result.name().name()).isEqualTo("NewName");
    }

    @Test
    @DisplayName("deleteProduct: 일반 사용자, 숨김 상품 삭제 시 예외")
    void deleteProduct_userCannotDeleteHidden() {
        when(repo.findById(2L)).thenReturn(Optional.of(hiddenProduct));

        assertThatThrownBy(() -> service.deleteProduct(2L, USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProduct: 관리자, 상품 삭제 성공")
    void deleteProduct_adminDeletes() {
        when(repo.findById(1L)).thenReturn(Optional.of(visibleProduct));

        service.deleteProduct(1L, ADMIN);

        then(repo).should().deleteById(1L);
    }

    @Test
    @DisplayName("hideProduct/unhideProduct: 일반 사용자 접근 시 예외")
    void hideUnhide_asUser_throws() {
        assertThatThrownBy(() -> service.hideProduct(1L, USER))
                .isInstanceOf(ProductNotFoundException.class);
        assertThatThrownBy(() -> service.unhideProduct(1L, USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("hideProduct/unhideProduct: 관리자, 숨김 플래그 변경")
    void hideUnhide_asAdmin_changesHiddenFlag() {
        when(repo.findById(1L)).thenReturn(Optional.of(visibleProduct));
        service.hideProduct(1L, ADMIN);
        then(repo).should().save(argThat(Product::hidden));

        when(repo.findById(1L)).thenReturn(Optional.of(hiddenProduct));
        service.unhideProduct(1L, ADMIN);
        then(repo).should().save(argThat(p -> !p.hidden()));
    }
}
