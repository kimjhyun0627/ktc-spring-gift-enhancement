package gift.entity.member.value;

import gift.exception.custom.InvalidMemberException;
import java.util.Objects;

public record MemberId(Long id) {

    public MemberId {
        Objects.requireNonNull(id, "회원 ID는 null일 수 없습니다.");
        if (id <= 0) {
            throw new InvalidMemberException("회원 ID는 양수여야 합니다.");
        }
    }
}
