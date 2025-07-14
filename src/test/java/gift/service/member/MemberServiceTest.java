package gift.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.InvalidAuthExeption;
import gift.exception.custom.MemberNotFoundException;
import gift.fixture.MemberFixture;
import gift.repository.member.MemberRepository;
import gift.util.JwtUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberServiceImpl 단위 테스트")
class MemberServiceTest {

    private static final Role USER = Role.USER;
    private static final Role ADMIN = Role.ADMIN;

    @Mock
    private MemberRepository memberRepo;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberServiceImpl service;

    private Member existing;
    private AuthRequest authReq;

    @BeforeEach
    void setUp() {
        existing = MemberFixture.newRegisteredMember(
                1L,
                "user@test.com",
                MemberServiceImpl.sha256("password"),
                Role.USER
        );
        authReq = new AuthRequest("user@test.com", "password");
    }

    @Nested
    @DisplayName("회원가입 및 로그인")
    class AuthTests {

        @Test
        @DisplayName("register: 신규 회원 토큰 발급")
        void registerSuccess() {
            given(memberRepo.register(any())).willReturn(existing);
            given(jwtUtil.generateToken(1L, USER)).willReturn("token123");

            AuthResponse res = service.register(authReq);

            assertThat(res.token()).isEqualTo("token123");
            then(memberRepo).should().register(argThat(m ->
                    m.getEmail().email().equals("user@test.com") &&
                            m.getPassword().password().equals(MemberServiceImpl.sha256("password"))
            ));
        }

        @Test
        @DisplayName("login: 존재하지 않는 이메일 예외")
        void loginEmailNotFound() {
            given(memberRepo.findByEmail("user@test.com")).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.login("user@test.com", "password"))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining("user@test.com");
        }

        @Test
        @DisplayName("login: 비밀번호 불일치 예외")
        void loginBadPassword() {
            Member wrong = MemberFixture.newRegisteredMember(
                    2L,
                    "user@test.com",
                    MemberServiceImpl.sha256("wrong"),
                    Role.USER
            );
            given(memberRepo.findByEmail("user@test.com")).willReturn(Optional.of(wrong));

            assertThatThrownBy(() -> service.login("user@test.com", "password"))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining("user@test.com");
        }

        @Test
        @DisplayName("login: 정상 로그인 시 토큰 반환")
        void loginSuccess() {
            given(memberRepo.findByEmail("user@test.com")).willReturn(Optional.of(existing));
            given(jwtUtil.generateToken(1L, USER)).willReturn("token123");

            AuthResponse res = service.login("user@test.com", "password");

            assertThat(res.token()).isEqualTo("token123");
        }
    }

    @Nested
    @DisplayName("회원 관리(ADMIN 권한)")
    class AdminTests {

        @Test
        @DisplayName("getAllMembers: 관리자 조회 성공")
        void getAllMembersAsAdmin() {
            given(memberRepo.findAll()).willReturn(List.of(existing));

            List<Member> list = service.getAllMembers(ADMIN);

            assertThat(list).containsExactly(existing);
        }

        @Test
        @DisplayName("getAllMembers: 일반 사용자 예외")
        void getAllMembersAsUserThrows() {
            assertThatThrownBy(() -> service.getAllMembers(USER))
                    .isInstanceOf(InvalidAuthExeption.class);
        }

        @Test
        @DisplayName("getMemberById: 관리자 조회 성공")
        void getMemberByIdAsAdmin() {
            given(memberRepo.findById(1L)).willReturn(Optional.of(existing));

            Optional<Member> opt = service.getMemberById(1L, ADMIN);

            assertThat(opt).isPresent().contains(existing);
        }

        @Test
        @DisplayName("getMemberById: 일반 사용자 예외")
        void getMemberByIdAsUserThrows() {
            assertThatThrownBy(() -> service.getMemberById(1L, USER))
                    .isInstanceOf(InvalidAuthExeption.class);
        }

        @Test
        @DisplayName("createMember: 관리자 생성 성공")
        void createMemberAsAdmin() {
            Member newM = MemberFixture.newRegisteredMember(
                    2L,
                    "a@b.com",
                    MemberServiceImpl.sha256("pw"),
                    Role.USER
            );
            given(memberRepo.register(any())).willReturn(newM);

            Member res = service.createMember("a@b.com", "pw", USER, ADMIN);

            assertThat(res.getEmail().email()).isEqualTo("a@b.com");
            assertThat(res.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("createMember: 일반 사용자 예외")
        void createMemberAsUserThrows() {
            assertThatThrownBy(() -> service.createMember("a@b.com", "pw", USER, USER))
                    .isInstanceOf(InvalidAuthExeption.class);
        }

        @Test
        @DisplayName("updateMember: 관리자 업데이트 성공")
        void updateMemberAsAdmin() {
            Member updated = existing.withEmail("new@t.com").withRole(Role.ADMIN);
            given(memberRepo.findById(1L)).willReturn(Optional.of(existing));
            given(memberRepo.updateMember(any())).willReturn(updated);

            Member res = service.updateMember(1L, "new@t.com", null, ADMIN, ADMIN);

            assertThat(res.getEmail().email()).isEqualTo("new@t.com");
            assertThat(res.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("updateMember: 없는 회원 예외")
        void updateMemberNotFound() {
            given(memberRepo.findById(2L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateMember(2L, "x", "pw", USER, ADMIN))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("deleteMember: 관리자 삭제 호출")
        void deleteMemberAsAdmin() {
            service.deleteMember(1L, ADMIN);
            then(memberRepo).should().deleteById(1L);
        }

        @Test
        @DisplayName("deleteMember: 일반 사용자 예외")
        void deleteMemberAsUserThrows() {
            assertThatThrownBy(() -> service.deleteMember(1L, USER))
                    .isInstanceOf(InvalidAuthExeption.class);
        }
    }
}
