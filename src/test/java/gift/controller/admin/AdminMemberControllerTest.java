package gift.controller.admin;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.fixture.MemberFixture;
import gift.service.member.MemberService;
import gift.service.member.MemberServiceImpl;
import gift.util.BearerAuthUtil;
import gift.util.JwtUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminMemberControllerTest {

    private static final Role ADMIN = Role.ADMIN;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private BearerAuthUtil bearerAuthUtil;

    @Nested
    @DisplayName("GET /admin/members")
    class ListMembers {

        @Test
        @DisplayName("정상 조회 - members 모델에 넣고 admin/member_list 뷰 반환")
        void 리스트조회_성공() throws Exception {
            Member m1 = MemberFixture.newRegisteredMember(
                    1L,
                    "a@example.com",
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    Role.USER
            );
            Member m2 = MemberFixture.newRegisteredMember(
                    2L,
                    "b@example.com",
                    "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                    Role.USER
            );

            given(memberService.getAllMembers(ADMIN))
                    .willReturn(List.of(m1, m2));

            mockMvc.perform(get("/admin/members")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/member_list"))
                    .andExpect(model().attribute("members", List.of(m1, m2)));
        }
    }

    @Nested
    @DisplayName("GET /admin/members/new")
    class NewForm {

        @Test
        @DisplayName("회원 생성 폼")
        void 회원생성폼() throws Exception {
            mockMvc.perform(get("/admin/members/new")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/member_form"))
                    .andExpect(model().attributeExists("memberForm"));
        }
    }

    @Nested
    @DisplayName("POST /admin/members/new")
    class CreateMember {

        @Test
        @DisplayName("유효한 입력 - 생성 후 리다이렉트")
        void 생성_성공() throws Exception {
            String raw = "abcdef";
            String hash = MemberServiceImpl.sha256(raw);
            Member created = MemberFixture.newRegisteredMember(
                    1L,
                    "new@ex.com",
                    hash,
                    Role.USER
            );
            given(memberService.createMember(
                    eq("new@ex.com"), anyString(), eq(Role.USER), eq(ADMIN)))
                    .willReturn(created);

            mockMvc.perform(post("/admin/members/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("email", "new@ex.com")
                            .param("password", raw)
                            .param("role", String.valueOf(Role.USER))
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/members"))
                    .andExpect(flash().attributeExists("info"));
        }

        @Test
        @DisplayName("검증 실패 - 폼 뷰 재진입")
        void 생성_검증실패() throws Exception {
            mockMvc.perform(post("/admin/members/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("email", "")
                            .param("password", "123")
                            .param("role", "")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/member_form"))
                    .andExpect(model().attributeHasFieldErrors(
                            "memberForm", "email", "password", "role"));
        }
    }

    @Nested
    @DisplayName("GET /admin/members/{id}/edit")
    class EditForm {

        @Test
        @DisplayName("존재하는 회원 - 폼에 채워서 반환")
        void 수정폼_성공() throws Exception {
            Member existing = MemberFixture.newRegisteredMember(
                    5L,
                    "e@e.com",
                    MemberServiceImpl.sha256("password"),
                    Role.ADMIN
            );
            given(memberService.getMemberById(5L, ADMIN))
                    .willReturn(Optional.of(existing));

            mockMvc.perform(get("/admin/members/5/edit")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/member_form"))
                    .andExpect(model().attribute("memberForm",
                            is(new gift.dto.member.MemberForm(
                                    5L,
                                    "e@e.com",
                                    existing.getPassword().password(),
                                    Role.ADMIN
                            ))
                    ));
        }

        @Test
        @DisplayName("없는 회원 - MemberNotFoundException -> 404")
        void 수정폼_404() throws Exception {
            given(memberService.getMemberById(99L, ADMIN))
                    .willReturn(Optional.empty());

            mockMvc.perform(get("/admin/members/99/edit")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /admin/members/{id}")
    class UpdateMember {

        @Test
        @DisplayName("정상 수정")
        void 수정_성공() throws Exception {
            Member updated = MemberFixture.newRegisteredMember(
                    5L,
                    "up@up.com",
                    MemberServiceImpl.sha256("newpass"),
                    Role.USER
            );
            given(memberService.updateMember(
                    eq(5L), eq("up@up.com"), anyString(), eq(Role.USER), eq(ADMIN)))
                    .willReturn(updated);

            mockMvc.perform(put("/admin/members/5")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("id", "5")
                            .param("email", "up@up.com")
                            .param("password", "newpass")
                            .param("role", String.valueOf(Role.USER))
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/members"))
                    .andExpect(flash().attributeExists("info"));
        }

        @Test
        @DisplayName("검증 오류")
        void 수정_검증실패() throws Exception {
            mockMvc.perform(put("/admin/members/5")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("email", "")
                            .param("password", "123")
                            .param("role", "")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/member_form"))
                    .andExpect(model().attributeHasFieldErrors(
                            "memberForm", "email", "role"));
        }
    }

    @Nested
    @DisplayName("DELETE /admin/members/{id}")
    class DeleteMember {

        @Test
        @DisplayName("정상 삭제")
        void 삭제_성공() throws Exception {
            willDoNothing().given(memberService).deleteMember(7L, ADMIN);

            mockMvc.perform(delete("/admin/members/7")
                            .requestAttr("authClaims", TestUtils.mockClaims(String.valueOf(ADMIN))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/members"))
                    .andExpect(flash().attributeExists("info"));
        }
    }
}
