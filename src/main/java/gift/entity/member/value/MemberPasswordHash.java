package gift.entity.member.value;

import gift.exception.custom.InvalidMemberException;
import java.util.Objects;

public record MemberPasswordHash(String password) {

    public static final int PASSWORD_LENGTH = 64;

    public MemberPasswordHash {
        Objects.requireNonNull(password, "비밀번호는 null일 수 없습니다.");
        if (password.isEmpty()) {
            throw new InvalidMemberException("비밀번호는 공백일 수 없습니다.");
        }
        if (password.length() < PASSWORD_LENGTH) {
            throw new InvalidMemberException("비밀번호는 " + PASSWORD_LENGTH + " 이하여야 합니다.");
        }
    }
}
