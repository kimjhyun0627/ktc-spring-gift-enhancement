package gift.entity.member;

import gift.entity.member.value.MemberEmail;
import gift.entity.member.value.MemberId;
import gift.entity.member.value.MemberPasswordHash;
import gift.entity.member.value.MemberRole;
import gift.entity.member.value.Role;
import java.time.LocalDateTime;
import java.util.Objects;

public class Member {

    private final MemberId id;
    private final MemberEmail email;
    private final MemberPasswordHash passwordHash;
    private final Role role;
    private final LocalDateTime createdAt;

    private Member(
            MemberId id,
            MemberEmail email,
            MemberPasswordHash passwordHash,
            Role role,
            LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static Member register(String email, String rawPasswordHash) {
        return new Member(
                null,
                new MemberEmail(email),
                new MemberPasswordHash(rawPasswordHash),
                Role.USER,
                LocalDateTime.now()
        );
    }

    public static Member of(
            Long id,
            String email,
            String passwordHash,
            String roleInput,
            LocalDateTime createdAt) {
        return new Member(
                id != null ? new MemberId(id) : null,
                new MemberEmail(email),
                new MemberPasswordHash(passwordHash),
                new MemberRole(roleInput).role(),
                createdAt
        );
    }

    public Member withId(Long newId) {
        return new Member(
                new MemberId(newId),
                this.email,
                this.passwordHash,
                this.role,
                this.createdAt
        );
    }

    public Member withEmail(String newEmail) {
        return new Member(
                this.id,
                new MemberEmail(newEmail),
                this.passwordHash,
                this.role,
                this.createdAt
        );
    }

    public Member withPasswordHash(String newPasswordHash) {
        return new Member(
                this.id,
                this.email,
                new MemberPasswordHash(newPasswordHash),
                this.role,
                this.createdAt
        );
    }

    public Member withRole(Role newRole) {
        return new Member(
                this.id,
                this.email,
                this.passwordHash,
                newRole,
                this.createdAt
        );
    }

    public MemberId getId() {
        return id;
    }

    public MemberEmail getEmail() {
        return email;
    }

    public MemberPasswordHash getPassword() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
