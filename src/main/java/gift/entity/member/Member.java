package gift.entity.member;

import gift.entity.member.value.MemberEmail;
import gift.entity.member.value.MemberId;
import gift.entity.member.value.MemberPasswordHash;
import gift.entity.member.value.Role;
import gift.entity.member.value.RoleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Embedded
    private MemberEmail email;

    @Embedded
    private MemberPasswordHash passwordHash;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Member() {

    }

    private Member(
            Long id,                       // ← MemberId 대신 Long
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
                id,
                new MemberEmail(email),
                new MemberPasswordHash(passwordHash),
                Role.of(roleInput),
                createdAt
        );
    }

    public Member withId(Long newId) {
        return new Member(
                newId,
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
        return id == null ? null : new MemberId(id);
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
