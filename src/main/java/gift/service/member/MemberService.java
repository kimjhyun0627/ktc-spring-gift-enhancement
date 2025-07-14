package gift.service.member;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import java.util.List;
import java.util.Optional;

public interface MemberService {

    AuthResponse register(AuthRequest authRequest);

    AuthResponse login(String email, String rawPassword);

    List<Member> getAllMembers(Role role);

    Optional<Member> getMemberById(Long id, Role role);

    Member createMember(String email, String rawPassword, Role newRole, Role role);

    Member updateMember(Long id, String email, String rawPassword, Role newRole, Role role);

    void deleteMember(Long id, Role role);
}
