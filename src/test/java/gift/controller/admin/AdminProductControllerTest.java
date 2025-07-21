package gift.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.ResolverConfig;
import gift.dto.product.ProductForm;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.fixture.ProductFixture;
import gift.service.member.MemberService;
import gift.service.product.ProductService;
import gift.util.BearerAuthUtil;
import gift.util.TestUtils;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminProductController.class)
@Import(ResolverConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminProductController 단위 테스트")
class AdminProductControllerTest {

    private static final Role ADMIN = Role.ADMIN;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private BearerAuthUtil bearerAuthUtil;

    @Test
    @DisplayName("GET /admin/products - 관리자 리스트 조회")
    void listAsAdmin() throws Exception {
        Product p = ProductFixture.visible(1L, "A", 10, "http://example.com/image.png");
        PageImpl<Product> page = new PageImpl<>(List.of(p));
        given(productService.getAllProducts(any(Pageable.class), eq(ADMIN))).willReturn(page);

        mockMvc.perform(get("/admin/products")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product_list"))
                .andExpect(model().attributeExists("productsPage"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @DisplayName("GET /admin/products/new - 폼 표시")
    void newFormAsAdmin() throws Exception {
        mockMvc.perform(get("/admin/products/new")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product_form"))
                .andExpect(model().attributeExists("productForm"));
    }

    @Test
    @DisplayName("POST /admin/products/new - 폼 에러 시 BAD_REQUEST")
    void createValidationError() throws Exception {
        mockMvc.perform(post("/admin/products/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("price", "")
                        .param("imageUrl", "")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("admin/product_form"));
    }

    @Test
    @DisplayName("POST /admin/products/new - 정상 생성 후 리다이렉트")
    void createSuccess() throws Exception {
        ProductForm form = new ProductForm(null, "A", 10, "http://example.com/image.png");
        Product created = ProductFixture.create(1L, form.getName(), form.getPrice(),
                form.getImageUrl(), false);
        given(productService.createProduct(form.getName(), form.getPrice(), form.getImageUrl(),
                ADMIN))
                .willReturn(created);

        mockMvc.perform(post("/admin/products/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", form.getName())
                        .param("price", form.getPrice().toString())
                        .param("imageUrl", form.getImageUrl())
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("GET /admin/products/{id}/edit - 폼 표시")
    void editFormAsAdmin() throws Exception {
        Product p = ProductFixture.create(2L, "B", 20, "http://example.com/image.png", false);
        given(productService.getProductById(2L, ADMIN)).willReturn(Optional.of(p));

        mockMvc.perform(get("/admin/products/2/edit")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product_form"))
                .andExpect(model().attributeExists("productForm"));
    }

    @Test
    @DisplayName("PUT /admin/products/{id} - 폼 에러 시 BAD_REQUEST")
    void updateValidationError() throws Exception {
        mockMvc.perform(put("/admin/products/2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("price", "")
                        .param("imageUrl", "")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("admin/product_form"));
    }

    @Test
    @DisplayName("PUT /admin/products/{id} - 정상 수정 후 리다이렉트")
    void updateSuccess() throws Exception {
        ProductForm form = new ProductForm(2L, "B", 20, "http://example.com/image.png");
        Product updated = ProductFixture.create(2L, form.getName(), form.getPrice(),
                form.getImageUrl(), false);
        given(productService.updateProduct(2L, form.getName(), form.getPrice(), form.getImageUrl(),
                ADMIN))
                .willReturn(updated);

        mockMvc.perform(put("/admin/products/2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", form.getName())
                        .param("price", form.getPrice().toString())
                        .param("imageUrl", form.getImageUrl())
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("DELETE /admin/products/{id} - 삭제 후 리다이렉트")
    void deleteAsAdmin() throws Exception {
        willDoNothing().given(productService).deleteProduct(3L, ADMIN);

        mockMvc.perform(delete("/admin/products/3")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("POST /admin/products/{id}/hide & /unhide")
    void hideUnhideAsAdmin() throws Exception {
        willDoNothing().given(productService).hideProduct(4L, ADMIN);
        willDoNothing().given(productService).unhideProduct(5L, ADMIN);

        mockMvc.perform(post("/admin/products/4/hide")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        mockMvc.perform(post("/admin/products/5/unhide")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }
}
