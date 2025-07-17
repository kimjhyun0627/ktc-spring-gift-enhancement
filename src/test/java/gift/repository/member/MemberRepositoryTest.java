package gift.repository.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.member.Member;
import gift.exception.custom.InvalidMemberException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class MemberRepositoryTest {

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD_HASH = "a".repeat(64);
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("저장 후 ID 생성 및 기본 필드 검증")
    void saveAndFindById() {
        Member member = Member.register(VALID_EMAIL, VALID_PASSWORD_HASH);
        Member saved = memberRepository.saveAndFlush(member);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<Member> found = memberRepository.findById(saved.getId().id());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail().email()).isEqualTo(VALID_EMAIL);
    }

    @Test
    @DisplayName("이메일로 조회 성공")
    void findByEmail_success() {
        Member member = Member.register(VALID_EMAIL, VALID_PASSWORD_HASH);
        memberRepository.saveAndFlush(member);

        Optional<Member> found = memberRepository.findByEmail_Email(VALID_EMAIL);
        assertThat(found).isPresent();
        assertThat(found.get().getEmail().email()).isEqualTo(VALID_EMAIL);
    }

    @Test
    @DisplayName("이메일 조회 실패 - 결과 없음")
    void findByEmail_notFound() {
        Optional<Member> found = memberRepository.findByEmail_Email("none@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("중복 이메일 저장 시 예외 발생")
    void duplicateEmail_throwsException() {
        Member first = Member.register(VALID_EMAIL, VALID_PASSWORD_HASH);
        memberRepository.saveAndFlush(first);

        Member duplicate = Member.register(VALID_EMAIL, VALID_PASSWORD_HASH);
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("유효하지 않은 이메일로 회원 등록 시 예외 발생")
    void invalidEmail_throwsException() {
        assertThrows(InvalidMemberException.class, () -> {
            Member.register("invalid-email", VALID_PASSWORD_HASH);
        });
    }

    @Test
    @DisplayName("해시 길이 부족 시 예외 발생")
    void shortPasswordHash_throwsException() {
        String shortHash = "b".repeat(10);
        assertThrows(InvalidMemberException.class, () -> {
            Member.register(VALID_EMAIL, shortHash);
        });
    }
}
