package gift.fixture;

import static gift.util.HashUtil.sha256;

import gift.entity.member.Member;
import gift.entity.member.value.Role;

public final class MemberFixture {

    private MemberFixture() {
    }

    public static Member newRegisteredMember(
            Long id, String email, String password, Role role) {

        String passwordHash = sha256(password);

        Member member = Member.register(email, passwordHash)
                .withRole(role);
        return member.withId(id);
    }

    public static Member visible() {
        return newRegisteredMember(
                1L,
                "user@example.com",
                "password",
                Role.USER
        );
    }
}
