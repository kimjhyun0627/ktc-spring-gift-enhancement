package gift.controller.user;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.product.ProductRequest;
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
@WebMvcTest(ProductController.class)
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
    private JwtUtil jwtUtil;
    @MockitoBean
    private BearerAuthUtil bearerAuthUtil;

    @Test
    @DisplayName("GET /api/products - 일반 사용자, 숨김 제외 리스트 반환")
    void listProductsAsUser() throws Exception {
        Product visible = ProductFixture.visible(1L, "A", 10, "http://example.com/a.png");
        Mockito.when(productService.getAllProducts(USER))
                .thenReturn(List.of(visible));

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(USER);

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("A")));
        }
    }

    @Test
    @DisplayName("GET /api/products - 관리자, 모든 상품 반환")
    void listProductsAsAdmin() throws Exception {
        Product visible = ProductFixture.visible(1L, "A", 10, "http://example.com/a.png");
        Product hidden = ProductFixture.hidden(2L, "B", 20, "http://example.com/b.png");
        Mockito.when(productService.getAllProducts(ADMIN))
                .thenReturn(List.of(visible, hidden));

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                    .andExpect(jsonPath("$[1].name", is("B")));
        }
    }

    @Test
    @DisplayName("GET /api/products/{id} - 숨김 상품 접근 시 NotFound for USER")
    void getHiddenProductForUser() throws Exception {
        Mockito.when(productService.getProductById(2L, USER))
                .thenReturn(Optional.empty());

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(USER);

            mockMvc.perform(get("/api/products/{id}", 2L))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("GET /api/products/{id} - 관리자 조회 숨김 상품 성공")
    void getHiddenProductForAdmin() throws Exception {
        Product hidden = ProductFixture.hidden(2L, "C", 30, "http://example.com/c.png");
        Mockito.when(productService.getProductById(2L, ADMIN))
                .thenReturn(Optional.of(hidden));

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(get("/api/products/{id}", 2L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("C")));
        }
    }

    @Test
    @DisplayName("POST /api/products - USER 권한, 금지된 이름 시 NotFound")
    void createProductForbiddenForUser() throws Exception {
        ProductRequest req = new ProductRequest("카카오톡", 40, "http://example.com/d.png");
        Mockito.when(
                productService.createProduct(req.name(), req.price(), req.imageUrl(), USER)
        ).thenThrow(new gift.exception.custom.ProductNotFoundException(null));

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(USER);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("POST /api/products - ADMIN 권한, 생성 성공")
    void createProductAsAdmin() throws Exception {
        ProductRequest req = new ProductRequest("D", 40, "http://example.com/d.png");
        Product created = ProductFixture.visible(3L, "D", 40, "http://example.com/d.png");
        Mockito.when(
                productService.createProduct(req.name(), req.price(), req.imageUrl(), ADMIN)
        ).thenReturn(created);

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(3)))
                    .andExpect(jsonPath("$.name", is("D")));
        }
    }

    @Test
    @DisplayName("PUT /api/products/{id} - USER 권한, 숨김 상품 수정 시 NotFound")
    void updateProductForbiddenForUser() throws Exception {
        ProductRequest req = new ProductRequest("E", 50, "http://example.com/e.png");
        Mockito.when(
                productService.updateProduct(2L, req.name(), req.price(), req.imageUrl(), USER)
        ).thenThrow(new gift.exception.custom.ProductNotFoundException(2L));

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(USER);

            mockMvc.perform(put("/api/products/{id}", 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }
    }


    @Test
    @DisplayName("PUT /api/products/{id} - ADMIN 권한, 수정 성공")
    void updateProductAsAdmin() throws Exception {
        ProductRequest req = new ProductRequest("E", 50, "http://example.com/e.png");
        Product updated = ProductFixture.visible(2L, "E", 50, "http://example.com/e.png");
        Mockito.when(
                productService.updateProduct(2L, req.name(), req.price(), req.imageUrl(), ADMIN)
        ).thenReturn(updated);

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(put("/api/products/{id}", 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("E")));
        }
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - USER 권한, 숨김 상품 삭제 시 Not Found")
    void deleteProductForbiddenForUser() throws Exception {
        Mockito.doThrow(new gift.exception.custom.ProductNotFoundException(2L))
                .when(productService).deleteProduct(2L, USER);

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(USER);

            mockMvc.perform(delete("/api/products/{id}", 2L))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - ADMIN 권한, 삭제 성공")
    void deleteProductAsAdmin() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(2L, ADMIN);

        try (MockedStatic<RoleUtil> utilities = Mockito.mockStatic(RoleUtil.class)) {
            utilities.when(() -> RoleUtil.extractRole(Mockito.any())).thenReturn(ADMIN);

            mockMvc.perform(delete("/api/products/{id}", 2L))
                    .andExpect(status().isNoContent());
        }

    }
}
