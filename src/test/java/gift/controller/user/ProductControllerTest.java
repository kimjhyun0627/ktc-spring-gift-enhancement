package gift.controller.user;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.ResolverConfig;
import gift.dto.product.ProductRequest;
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

@WebMvcTest(ProductController.class)
@Import(ResolverConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProductController 단위 테스트")
class ProductControllerTest {

    private static final Role USER = Role.USER;
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
    @DisplayName("GET /api/products - 일반 사용자")
    void listProductsAsUser() throws Exception {
        Product visible = ProductFixture.visible(1L, "A", 10, "http://example.com/a.png");
        PageImpl<Product> page = new PageImpl<>(List.of(visible));
        when(productService.getAllProducts(any(Pageable.class), eq(USER))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("A")));
    }

    @Test
    @DisplayName("GET /api/products - 관리자")
    void listProductsAsAdmin() throws Exception {
        Product visible = ProductFixture.visible(1L, "A", 10, "http://example.com/a.png");
        Product hidden = ProductFixture.hidden(2L, "B", 20, "http://example.com/b.png");
        PageImpl<Product> page = new PageImpl<>(List.of(visible, hidden));
        when(productService.getAllProducts(any(Pageable.class), eq(ADMIN))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[1].name", is("B")));
    }

    @Test
    @DisplayName("GET /api/products/{id} - 숨김 상품 USER → 404")
    void getHiddenProductForUser() throws Exception {
        when(productService.getProductById(2L, USER)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/2")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products/{id} - 숨김 상품 ADMIN → 200")
    void getHiddenProductForAdmin() throws Exception {
        Product hidden = ProductFixture.hidden(2L, "C", 30, "http://example.com/c.png");
        when(productService.getProductById(2L, ADMIN)).thenReturn(Optional.of(hidden));

        mockMvc.perform(get("/api/products/2")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("C")));
    }

    @Test
    @DisplayName("POST /api/products - USER 금지 → 404")
    void createProductForbiddenForUser() throws Exception {
        ProductRequest req = new ProductRequest("카카오톡", 40, "http://example.com/d.png");
        when(productService.createProduct(req.name(), req.price(), req.imageUrl(), USER))
                .thenThrow(new gift.exception.custom.ProductNotFoundException(null));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/products - ADMIN 생성 → 201")
    void createProductAsAdmin() throws Exception {
        ProductRequest req = new ProductRequest("D", 40, "http://example.com/d.png");
        Product created = ProductFixture.visible(3L, "D", 40, req.imageUrl());
        when(productService.createProduct(req.name(), req.price(), req.imageUrl(), ADMIN))
                .thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("D")));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - USER 숨김 수정 → 404")
    void updateProductForbiddenForUser() throws Exception {
        ProductRequest req = new ProductRequest("E", 50, "http://example.com/e.png");
        when(productService.updateProduct(2L, req.name(), req.price(), req.imageUrl(), USER))
                .thenThrow(new gift.exception.custom.ProductNotFoundException(2L));

        mockMvc.perform(put("/api/products/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - ADMIN 수정 → 200")
    void updateProductAsAdmin() throws Exception {
        ProductRequest req = new ProductRequest("E", 50, "http://example.com/e.png");
        Product updated = ProductFixture.visible(2L, req.name(), req.price(), req.imageUrl());
        when(productService.updateProduct(2L, req.name(), req.price(), req.imageUrl(), ADMIN))
                .thenReturn(updated);

        mockMvc.perform(put("/api/products/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("E")));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - USER 숨김 삭제 → 404")
    void deleteProductForbiddenForUser() throws Exception {
        doThrow(new gift.exception.custom.ProductNotFoundException(2L))
                .when(productService).deleteProduct(2L, USER);

        mockMvc.perform(delete("/api/products/2")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - ADMIN 삭제 → 204")
    void deleteProductAsAdmin() throws Exception {
        doNothing().when(productService).deleteProduct(2L, ADMIN);

        mockMvc.perform(delete("/api/products/2")
                        .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                .andExpect(status().isNoContent());
    }
}
