package gift.controller.admin;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.filter.AdminCookieFilter;
import gift.service.member.MemberService;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminAuthController 단위 테스트")
class AdminAuthControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private BearerAuthUtil bearerAuthUtil;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        AdminAuthController controller = new AdminAuthController(memberService);
        AdminCookieFilter adminCookieFilter = new AdminCookieFilter();

        mockMvc = standaloneSetup(controller)
                .addFilter(adminCookieFilter, "/admin/*")
                .build();
    }

    @Test
    @DisplayName("GET /admin/login - 로그인 폼 표시 및 에러 없음")
    void loginFormNoError() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login_form"))
                .andExpect(model().attributeExists("authRequest"))
                .andExpect(model().attributeDoesNotExist("error"));
    }

    @Test
    @DisplayName("GET /admin/login?error - 로그인 폼 표시 및 오류 메시지")
    void loginFormWithError() throws Exception {
        mockMvc.perform(get("/admin/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login_form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("POST /admin/login - 유효성 오류 시 폼 뷰")
    void loginSubmitValidationError() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid-email")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login_form"));
    }

    @Test
    @DisplayName("POST /admin/login - 성공 시 쿠키 추가 및 리다이렉트")
    void loginSubmitSuccess() throws Exception {
        AuthRequest form = new AuthRequest("user@x.com", "pwd");
        AuthResponse resp = new AuthResponse("jwt-token");
        given(memberService.login(form.email(), form.password())).willReturn(resp);

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", form.email())
                        .param("password", form.password()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(cookie().exists("AUTH_TOKEN"));
    }

    @Test
    @DisplayName("POST /admin/login - 인증 실패 시 에러 모델 추가")
    void loginSubmitAuthFailure() throws Exception {
        AuthRequest form = new AuthRequest("user@x.com", "wrong");
        given(memberService.login(form.email(), form.password()))
                .willThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", form.email())
                        .param("password", form.password()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login_form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("GET /admin/logout - 쿠키 제거 및 리다이렉트")
    void logout() throws Exception {
        mockMvc.perform(get("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"))
                .andExpect(cookie().maxAge("AUTH_TOKEN", 0));
    }
}
