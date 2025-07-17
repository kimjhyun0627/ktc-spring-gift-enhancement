package gift.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.when;

import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.product.value.ProductId;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
        when(repo.findByHiddenFalse(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(visibleProduct)));

        Page<Product> resultPage = service.getAllProducts(Pageable.unpaged(), USER);
        assertThat(resultPage.getContent())
                .containsExactly(visibleProduct);

        then(repo).should().findByHiddenFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("getAllProducts: 관리자, 모든 상품 반환")
    void getAllProducts_asAdmin_returnsAll() {
        Page<Product> page = new PageImpl<>(List.of(visibleProduct, hiddenProduct));
        when(repo.findAll(any(Pageable.class))).thenReturn(page);

        Page<Product> result = service.getAllProducts(Pageable.unpaged(), ADMIN);

        assertThat(result.getContent()).containsExactly(visibleProduct, hiddenProduct);
        then(repo).should().findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getProductById: 일반 사용자, 숨김 상품 접근 시 예외")
    void getProductById_userCannotSeeHidden() {
        when(repo.findById(new ProductId(2L))).thenReturn(Optional.of(hiddenProduct));

        assertThatThrownBy(() -> service.getProductById(2L, USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("getProductById: 일반 사용자, 노출 상품 조회 성공")
    void getProductById_userSeesVisible() {
        when(repo.findById(new ProductId(1L))).thenReturn(Optional.of(visibleProduct));

        Optional<Product> result = service.getProductById(1L, USER);

        assertThat(result).isPresent().contains(visibleProduct);
    }

    @Test
    @DisplayName("getProductById: 관리자, 숨김 상품 조회 성공")
    void getProductById_adminSeesHidden() {
        when(repo.findById(new ProductId(2L))).thenReturn(Optional.of(hiddenProduct));

        Optional<Product> result = service.getProductById(2L, ADMIN);

        assertThat(result).isPresent().contains(hiddenProduct);
    }

    @Test
    @DisplayName("getProductById: 없는 상품 조회 시 예외")
    void getProductById_missing_throws() {
        when(repo.findById(new ProductId(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProductById(99L, ADMIN))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("createProduct: 일반 사용자, 금지된 이름 숨김 처리")
    void createProduct_userForbiddenName_hides() {
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct("카카오톡", 300, "http://image.png", USER);

        assertThat(result.isHidden()).isTrue();
        then(repo).should().save(argThat(Product::isHidden));
    }

    @Test
    @DisplayName("createProduct: 일반 사용자, 허용된 이름 노출 처리")
    void createProduct_userAllowed_shows() {
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct("Normal", 100, "http://image.png", USER);

        assertThat(result.isHidden()).isFalse();
    }

    @Test
    @DisplayName("createProduct: 관리자, 금지된 이름도 노출 처리")
    void createProduct_adminIgnoresForbidden() {
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct("카카오톡", 300, "http://image.png", ADMIN);

        assertThat(result.isHidden()).isFalse();
    }

    @Test
    @DisplayName("updateProduct: 일반 사용자, 숨김 상품 수정 시 예외")
    void updateProduct_userCannotUpdateHidden() {
        when(repo.findById(new ProductId(2L))).thenReturn(Optional.of(hiddenProduct));

        assertThatThrownBy(() ->
                service.updateProduct(2L, "New", 150, "http://image.png", USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("updateProduct: 일반 사용자, 금지된 새 이름 숨김 처리")
    void updateProduct_userForbiddenNewName_hides() {
        when(repo.findById(new ProductId(1L))).thenReturn(Optional.of(visibleProduct));

        Product result = service.updateProduct(1L, "카카오톡", 150, "http://image.png", USER);

        assertThat(result.isHidden()).isTrue();
    }

    @Test
    @DisplayName("updateProduct: 관리자, 항상 업데이트")
    void updateProduct_adminAlwaysUpdates() {
        when(repo.findById(new ProductId(1L))).thenReturn(Optional.of(visibleProduct));

        Product result = service.updateProduct(1L, "NewName", 150, "http://image.png", ADMIN);

        assertThat(result.getName().name()).isEqualTo("NewName");
    }

    @Test
    @DisplayName("deleteProduct: 일반 사용자, 숨김 상품 삭제 시 예외")
    void deleteProduct_userCannotDeleteHidden() {
        when(repo.findById(new ProductId(2L))).thenReturn(Optional.of(hiddenProduct));

        assertThatThrownBy(() -> service.deleteProduct(2L, USER))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProduct: 관리자, 상품 삭제 성공")
    void deleteProduct_adminDeletes() {
        when(repo.findById(new ProductId(1L))).thenReturn(Optional.of(visibleProduct));

        service.deleteProduct(1L, ADMIN);

        then(repo).should().deleteById(new ProductId(1L));
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
        when(repo.findById(new ProductId(1L))).thenReturn(Optional.of(visibleProduct));
        service.hideProduct(1L, ADMIN);
        then(repo).should().save(argThat(Product::isHidden));

        when(repo.findById(new ProductId(1L))).thenReturn(Optional.of(hiddenProduct));
        service.unhideProduct(1L, ADMIN);
        then(repo).should().save(argThat(p -> !p.isHidden()));
    }
}
