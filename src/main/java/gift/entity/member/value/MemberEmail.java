package gift.entity.member.value;

import gift.exception.custom.InvalidMemberException;
import java.util.Objects;
import java.util.regex.Pattern;

public record MemberEmail(String email) {

    public static final String EMAIL_REGEX =
            "^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$";

    public MemberEmail {
        Objects.requireNonNull(email, "이메일은 필수 입력값입니다.");
        if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            throw new InvalidMemberException("유효한 이메일 형식이 아닙니다.");
        }
    }
}
