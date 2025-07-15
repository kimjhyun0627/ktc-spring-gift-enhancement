package gift.repository.member;

import static gift.util.HashUtil.sha256;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final MemberRepository memberRepository;

    public AdminAccountInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createAdminAccountIfNotExists() {
        initAccount(
                "admin@email.com",
                "admin123",
                Role.ADMIN.name(),
                "Admin"
        );
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createUserAccountIfNotExists() {
        initAccount(
                "user@user.com",
                "user123",
                Role.USER.name(),
                "User"
        );
    }

    private void initAccount(String email, String rawPassword, String roleName, String label) {
        MDC.put("email", email);
        MDC.put("role", roleName);
        try {
            Optional<Member> existing = memberRepository.findByEmail_Email(email);
            if (existing.isEmpty()) {
                Member account = Member.of(
                        null,
                        email,
                        sha256(rawPassword),
                        roleName,
                        LocalDateTime.now()
                );
                memberRepository.save(account);
                logger.info("{} 계정 생성 완료", label);
            } else {
                logger.info("{} 계정이 이미 존재합니다", label);
            }
        } catch (DataIntegrityViolationException e) {
            // 중복 email 등 무결성 위반
            logger.warn("{} 계정 생성 중 데이터 무결성 위반: {}", label, e.getMessage());
        } catch (Exception e) {
            logger.error("{} 계정 초기화 중 오류 발생", label, e);
        } finally {
            MDC.clear();
        }
    }
}
