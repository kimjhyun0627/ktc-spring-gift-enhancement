package gift.entity.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.member.value.Role;
import gift.exception.custom.InvalidMemberException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void register_setsDefaults() {
        String email = "test@example.com";
        String hash = "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";

        Member m = Member.register(email, hash);

        assertNull(m.getId(), "Newly registered member should have null id");
        assertEquals(email, m.getEmail().email(), "Email should be set correctly");
        assertEquals(hash, m.getPassword().password(), "Password hash should be set correctly");
        assertEquals(Role.USER, m.getRole(), "Default role should be USER");
        assertNotNull(m.getCreatedAt(), "createdAt should be initialized");
    }

    @Test
    void of_createsWithAllFields() {
        Long id = 42L;
        String email = "u@domain.com";
        String hash = "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";
        String roleInput = "ADMIN";
        LocalDateTime now = LocalDateTime.now();

        Member m = Member.of(id, email, hash, roleInput, now);

        assertNotNull(m.getId());
        assertEquals(id, m.getId().id(), "ID should be set correctly");
        assertEquals(email, m.getEmail().email(), "Email should be set correctly");
        assertEquals(hash, m.getPassword().password(), "Password hash should be set correctly");
        assertEquals(Role.ADMIN, m.getRole(), "Role should be parsed from roleInput");
        assertEquals(now, m.getCreatedAt(), "createdAt should match provided value");
    }

    @Test
    void withId_updatesIdOnly() {
        Member base = Member.register("a@b.com",
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789");
        assertNull(base.getId());

        Member with = base.withId(100L);
        assertNotNull(with.getId());
        assertEquals(100L, with.getId().id());
        assertEquals(base.getEmail(), with.getEmail());
        assertEquals(base.getPassword(), with.getPassword());
        assertEquals(base.getRole(), with.getRole());
        assertEquals(base.getCreatedAt(), with.getCreatedAt());
    }

    @Test
    void withEmail_updatesEmailOnly() {
        Member base = Member.of(1L, "old@x.com",
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789", "USER",
                LocalDateTime.now());
        Member changed = base.withEmail("new@x.com");
        assertEquals("new@x.com", changed.getEmail().email());
        assertEquals(base.getPassword(), changed.getPassword());
        assertEquals(base.getRole(), changed.getRole());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getCreatedAt(), changed.getCreatedAt());
    }

    @Test
    void withPasswordHash_updatesPasswordOnly() {
        String initialHash = "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";
        Member base = Member.of(2L, "e@e.com", initialHash, "USER", LocalDateTime.now());
        String newHash = "1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef";
        Member changed = base.withPasswordHash(newHash);
        assertEquals(newHash, changed.getPassword().password());
        assertEquals(base.getEmail(), changed.getEmail());
        assertEquals(base.getRole(), changed.getRole());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getCreatedAt(), changed.getCreatedAt());
    }

    @Test
    void withRole_updatesRoleOnly() {
        Member base = Member.of(3L, "r@r.com",
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789", "USER",
                LocalDateTime.now());
        Member changed = base.withRole(Role.ADMIN);
        assertEquals(Role.ADMIN, changed.getRole());
        assertEquals(base.getEmail(), changed.getEmail());
        assertEquals(base.getPassword(), changed.getPassword());
        assertEquals(base.getId(), changed.getId());
        assertEquals(base.getCreatedAt(), changed.getCreatedAt());
    }

    @Test
    void equals_and_hashCode_basedOnId() {
        Member m1 = Member.of(10L, "x@x.com",
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789", "USER",
                LocalDateTime.now());
        Member m2 = Member.of(10L, "y@y.com",
                "fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210", "ADMIN",
                LocalDateTime.now());
        Member m3 = Member.of(11L, "x@x.com",
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789", "USER",
                LocalDateTime.now());

        assertEquals(m1, m2, "Members with same id should be equal");
        assertEquals(m1.hashCode(), m2.hashCode(), "HashCodes should match for same id");
        assertNotEquals(m1, m3, "Different ids should not be equal");
    }

    @Test
    void invalidPasswordHash_throwsException() {
        String badHash = "short";
        assertThrows(InvalidMemberException.class, () ->
                Member.of(5L, "u@u.com", badHash, "USER", LocalDateTime.now())
        );
    }
}
