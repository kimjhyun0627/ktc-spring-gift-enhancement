package gift.controller.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import gift.service.member.MemberService;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminDashboardController 단위 테스트")
class AdminDashboardControllerTest {

    private static final String EMAIL = "user@example.com";
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private Claims claims;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private BearerAuthUtil bearerAuthUtil;

    @MockitoBean
    private MemberService memberService;

    private RequestPostProcessor withAuthClaims(Claims claims) {
        return request -> {
            request.setAttribute("authClaims", claims);
            return request;
        };
    }

    @Test
    @DisplayName("GET /admin/dashboard - authClaims 없으면 뷰만 반환")
    void dashboardWithoutClaims() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeDoesNotExist("email"));
    }

    @Test
    @DisplayName("GET /admin/dashboard - authClaims 있으면 이메일 모델에 추가")
    void dashboardWithClaims() throws Exception {
        Mockito.when(claims.getSubject()).thenReturn(EMAIL);

        mockMvc.perform(get("/admin/dashboard").with(withAuthClaims(claims)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("email", EMAIL));
    }
}
