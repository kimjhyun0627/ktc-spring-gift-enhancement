package gift.entity.member.value;

import gift.exception.custom.InvalidMemberException;

public record MemberRole(Role role) {

    public MemberRole(String input) {
        this(parse(input));
    }

    private static Role parse(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidMemberException("역할은 필수 입력값입니다.");
        }
        try {
            return Role.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberException("유효하지 않은 역할값입니다: " + input);
        }
    }
}
