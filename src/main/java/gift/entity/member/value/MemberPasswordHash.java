package gift.entity.member.value;

import gift.exception.custom.InvalidMemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public record MemberPasswordHash(
        @Column(name = "password_hash", nullable = false)
        String passwordHash
) {

    public static final int PASSWORD_LENGTH = 64;

    public MemberPasswordHash {
        Objects.requireNonNull(passwordHash, "비밀번호는 null일 수 없습니다.");
        if (passwordHash.isEmpty()) {
            throw new InvalidMemberException("비밀번호는 공백일 수 없습니다.");
        }
        if (passwordHash.length() < PASSWORD_LENGTH) {
            throw new InvalidMemberException("비밀번호는 " + PASSWORD_LENGTH + " 이하여야 합니다.");
        }
    }
}
