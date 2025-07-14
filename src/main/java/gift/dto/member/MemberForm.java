package gift.dto.member;

import gift.entity.member.value.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberForm(
        Long id,

        @Email(message = "유효한 이메일을 입력하세요")
        @NotBlank(message = "이메일은 필수입니다")
        @Size(max = 64, message = "이메일은 최대 64자입니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, max = 64, message = "비밀번호는 6~64자여야 합니다")
        String password,

        @NotNull(message = "역할을 선택하세요")
        Role role
) {

}
