package gift.fixture;

import gift.entity.member.Member;
import gift.entity.member.value.Role;

public final class MemberFixture {

    private MemberFixture() {
    }

    public static Member newRegisteredMember(
            long id, String email, String passwordHash, Role role) {

        Member m = Member.register(email, passwordHash)
                .withRole(role);
        m = m.withId(id);
        return m;
    }
}
