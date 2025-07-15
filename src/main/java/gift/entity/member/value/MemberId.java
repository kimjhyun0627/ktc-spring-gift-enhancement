package gift.entity.member.value;

import gift.exception.custom.InvalidMemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public record MemberId(
        @Column(name = "id")
        Long id
) {

    public MemberId {
        if (id != null && id <= 0) {
            throw new InvalidMemberException("회원 ID는 양수여야 합니다.");
        }
    }
}
