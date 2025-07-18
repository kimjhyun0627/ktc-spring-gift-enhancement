package gift.controller.user;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.product.option.DecreaseOptionRequest;
import gift.dto.product.option.OptionRequest;
import gift.dto.product.option.OptionResponse;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.service.member.MemberService;
import gift.service.product.ProductService;
import gift.service.product.option.ProductOptionService;
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
@WebMvcTest(ProductOptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProductOptionController 단위 테스트")
class ProductOptionControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductService productService;
    @MockitoBean
    ProductOptionService optionService;
    @MockitoBean
    MemberService memberService;
    @MockitoBean
    JwtUtil jwtUtil;
    @MockitoBean
    BearerAuthUtil bearerAuthUtil;

    @Test
    @DisplayName("GET 옵션 리스트 조회 - 성공 (200)")
    void getOptions_success() throws Exception {
        long productId = 10L;
        // 목 Product 인스턴스 (authorizeAccess 통과용)
        Product dummy = Mockito.mock(Product.class);

        List<OptionResponse> options = List.of(
                new OptionResponse(1L, "M", 5),
                new OptionResponse(2L, "L", 3)
        );

        // authorizeAccess stub: anyLong(), any(Role.class)
        given(productService.getProductById(
                eq(productId),
                any(Role.class))
        ).willReturn(Optional.of(dummy));

        given(optionService.getOptions(productId)).willReturn(options);

        try (MockedStatic<RoleUtil> rs = Mockito.mockStatic(RoleUtil.class)) {
            rs.when(() -> RoleUtil.extractRole(any()))
                    .thenReturn(Role.USER);

            mockMvc.perform(get("/api/products/{productId}/options", productId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("M")))
                    .andExpect(jsonPath("$[1].quantity", is(3)));
        }
    }

    @Test
    @DisplayName("GET 옵션 리스트 조회 - 상품 없음 (404)")
    void getOptions_notFound() throws Exception {
        long productId = 99L;
        given(productService.getProductById(
                eq(productId),
                any(Role.class))
        ).willReturn(Optional.empty());

        try (MockedStatic<RoleUtil> rs = Mockito.mockStatic(RoleUtil.class)) {
            rs.when(() -> RoleUtil.extractRole(any()))
                    .thenReturn(Role.USER);

            mockMvc.perform(get("/api/products/{productId}/options", productId))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("POST 옵션 추가 - 성공 (201)")
    void addOption_success() throws Exception {
        long productId = 20L;
        Product dummy = Mockito.mock(Product.class);
        OptionRequest req = new OptionRequest("Red", 8);
        OptionResponse resp = new OptionResponse(5L, "Red", 8);

        given(productService.getProductById(
                eq(productId),
                any(Role.class))
        ).willReturn(Optional.of(dummy));

        given(optionService.addOption(productId, req.name(), req.quantity()))
                .willReturn(resp);

        try (MockedStatic<RoleUtil> rs = Mockito.mockStatic(RoleUtil.class)) {
            rs.when(() -> RoleUtil.extractRole(any()))
                    .thenReturn(Role.ADMIN);

            mockMvc.perform(post("/api/products/{productId}/options", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.name", is("Red")))
                    .andExpect(jsonPath("$.quantity", is(8)));
        }
    }

    @Test
    @DisplayName("PATCH 옵션 수량 차감 - 성공 (204)")
    void decreaseOption_success() throws Exception {
        long productId = 30L, optionId = 7L;
        Product dummy = Mockito.mock(Product.class);
        DecreaseOptionRequest req = new DecreaseOptionRequest(2);

        given(productService.getProductById(
                eq(productId),
                any(Role.class))
        ).willReturn(Optional.of(dummy));

        doNothing().when(optionService).decreaseOption(optionId, req.amount());

        try (MockedStatic<RoleUtil> rs = Mockito.mockStatic(RoleUtil.class)) {
            rs.when(() -> RoleUtil.extractRole(any()))
                    .thenReturn(Role.USER);

            mockMvc.perform(patch("/api/products/{productId}/options/{optionId}/decrease",
                            productId, optionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("PATCH 옵션 수량 차감 - 상품 없음 (404)")
    void decreaseOption_notFound() throws Exception {
        long productId = 40L, optionId = 8L;
        DecreaseOptionRequest req = new DecreaseOptionRequest(1);

        given(productService.getProductById(
                eq(productId),
                any(Role.class))
        ).willReturn(Optional.empty());

        try (MockedStatic<RoleUtil> rs = Mockito.mockStatic(RoleUtil.class)) {
            rs.when(() -> RoleUtil.extractRole(any()))
                    .thenReturn(Role.USER);

            mockMvc.perform(patch("/api/products/{productId}/options/{optionId}/decrease",
                            productId, optionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }
    }
}
