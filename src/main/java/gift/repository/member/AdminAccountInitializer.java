package gift.repository.member;

import static gift.service.member.MemberServiceImpl.sha256;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import java.time.LocalDateTime;
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
        String adminEmail = "admin@email.com";
        String adminPassword = "admin123";

        MDC.put("email", adminEmail);
        MDC.put("role", Role.ADMIN.name());
        try {
            if (memberRepository.findByEmail(adminEmail).isEmpty()) {
                Member admin = Member.of(
                        null,
                        adminEmail,
                        sha256(adminPassword),
                        Role.ADMIN.name(),
                        LocalDateTime.now()
                );
                memberRepository.register(admin);
                logger.info("Admin 계정 생성 완료");
            } else {
                logger.info("Admin 계정이 이미 존재합니다");
            }
        } catch (DataIntegrityViolationException e) {
            logger.warn("Admin 계정 생성 중 데이터 무결성 위반", e);
        } catch (Exception e) {
            logger.error("Admin 계정 초기화 중 오류 발생", e);
        } finally {
            MDC.clear();
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createUserAccountIfNotExists() {
        String userEmail = "user@user.com";
        String userPassword = "user123";

        MDC.put("email", userEmail);
        MDC.put("role", Role.USER.name());
        try {
            if (memberRepository.findByEmail(userEmail).isEmpty()) {
                Member user = Member.of(
                        null,
                        userEmail,
                        sha256(userPassword),
                        Role.USER.name(),
                        LocalDateTime.now()
                );
                memberRepository.register(user);
                logger.info("User 계정 생성 완료");
            } else {
                logger.info("User 계정이 이미 존재합니다");
            }
        } catch (DataIntegrityViolationException e) {
            logger.warn("User 계정 생성 중 데이터 무결성 위반", e);
        } catch (Exception e) {
            logger.error("User 계정 초기화 중 오류 발생", e);
        } finally {
            MDC.clear();
        }
    }
}
