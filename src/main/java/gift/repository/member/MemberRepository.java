package gift.repository.member;

import gift.entity.member.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member register(Member member);

    Member updateMember(Member member);

    Optional<Member> findByEmail(String email);

    List<Member> findAll();

    Optional<Member> findById(Long id);

    void deleteById(Long id);
}
