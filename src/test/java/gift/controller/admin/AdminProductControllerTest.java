package gift.controller.admin;

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
import gift.dto.product.ProductForm;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.fixture.ProductFixture;
import gift.service.member.MemberService;
import gift.service.product.ProductService;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import gift.util.RoleUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminProductController 단위 테스트 (Fixture 적용)")
class AdminProductControllerTest {

    private static final Role ADMIN = Role.ADMIN;

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private BearerAuthUtil bearerAuthUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /admin/products - 관리자 리스트 조회")
    void listAsAdmin() throws Exception {
        Product p = ProductFixture.visible(1L, "A", 10, "http://example.com/image.png");
        given(productService.getAllProducts(ADMIN)).willReturn(List.of(p));

        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(get("/admin/products"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/product_list"))
                    .andExpect(model().attributeExists("products"));
        }
    }

    @Test
    @DisplayName("GET /admin/products/new - 폼 표시")
    void newFormAsAdmin() throws Exception {
        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(get("/admin/products/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/product_form"))
                    .andExpect(model().attributeExists("productForm"));
        }
    }

    @Test
    @DisplayName("POST /admin/products/new - 폼 에러 시 BAD_REQUEST & form view")
    void createValidationError() throws Exception {
        ProductForm form = new ProductForm(null, "", null, "");
        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(post("/admin/products/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("name", form.getName())
                            .param("price", form.getPrice() == null ? "" : form.getPrice().toString())
                            .param("imageUrl", form.getImageUrl()))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("admin/product_form"));
        }
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

        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(post("/admin/products/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("name", form.getName())
                            .param("price", form.getPrice().toString())
                            .param("imageUrl", form.getImageUrl()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/products"));
        }
    }

    @Test
    @DisplayName("GET /admin/products/{id}/edit - 폼 표시 및 데이터 바인딩")
    void editFormAsAdmin() throws Exception {
        Product p = ProductFixture.create(2L, "B", 20, "http://example.com/image.png", false);
        given(productService.getProductById(2L, ADMIN)).willReturn(Optional.of(p));

        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(get("/admin/products/2/edit"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/product_form"))
                    .andExpect(model().attributeExists("productForm"));
        }
    }

    @Test
    @DisplayName("PUT /admin/products/{id} - 폼 에러 시 BAD_REQUEST & form view")
    void updateValidationError() throws Exception {
        ProductForm form = new ProductForm(2L, "", null, "");
        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(put("/admin/products/2")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("name", form.getName())
                            .param("price", form.getPrice() == null ? "" : form.getPrice().toString())
                            .param("imageUrl", form.getImageUrl()))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("admin/product_form"));
        }
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

        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(put("/admin/products/2")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("name", form.getName())
                            .param("price", form.getPrice().toString())
                            .param("imageUrl", form.getImageUrl()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/products"));
        }
    }

    @Test
    @DisplayName("DELETE /admin/products/{id} - 삭제 후 리다이렉트")
    void deleteAsAdmin() throws Exception {
        willDoNothing().given(productService).deleteProduct(3L, ADMIN);

        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(delete("/admin/products/3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/products"));
        }
    }

    @Test
    @DisplayName("POST /admin/products/{id}/hide & /unhide - 상태 변경 후 리다이렉트")
    void hideUnhideAsAdmin() throws Exception {
        willDoNothing().given(productService).hideProduct(4L, ADMIN);
        willDoNothing().given(productService).unhideProduct(5L, ADMIN);

        try (MockedStatic<RoleUtil> util = Mockito.mockStatic(RoleUtil.class)) {
            util.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(post("/admin/products/4/hide"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/products"));

            mockMvc.perform(post("/admin/products/5/unhide"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/products"));
        }
    }
}
