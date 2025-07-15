package gift.dto.member;

import static gift.entity.member.value.MemberPasswordHash.PASSWORD_LENGTH;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotNull(message = "이메일은 필수 입력값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,
        @NotNull(message = "비밀번호는 null일 수 없습니다.")
        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        @Size(max = PASSWORD_LENGTH, message = "비밀번호는 " + PASSWORD_LENGTH + " 이하여야 합니다.")
        String password
) {

}
