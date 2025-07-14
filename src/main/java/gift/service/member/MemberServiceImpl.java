package gift.service.member;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.InvalidAuthExeption;
import gift.exception.custom.MemberNotFoundException;
import gift.repository.member.MemberRepository;
import gift.util.JwtUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    public static String sha256(String input) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthResponse register(AuthRequest authRequest) {
        String hash = sha256(authRequest.password());
        Member m = Member.register(authRequest.email(), hash);
        m = memberRepository.register(m);
        String token = jwtUtil.generateToken(m.getId().id(), m.getRole());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(String email, String rawPassword) {
        Member m = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
        if (!m.getPassword().password().equals(sha256(rawPassword))) {
            throw new MemberNotFoundException(email);
        }
        String token = jwtUtil.generateToken(m.getId().id(), m.getRole());
        return new AuthResponse(token);
    }

    private void checkAdmin(Role role) {
        if (role == Role.USER) {
            throw new InvalidAuthExeption("관리자 권한이 필요합니다.");
        }
    }

    @Override
    public List<Member> getAllMembers(Role role) {
        checkAdmin(role);
        return memberRepository.findAll();
    }

    @Override
    public Optional<Member> getMemberById(Long id, Role role) {
        checkAdmin(role);
        return memberRepository.findById(id);
    }

    @Override
    public Member createMember(
            String email, String rawPassword, Role newRole, Role role) {
        checkAdmin(role);
        String hash = sha256(rawPassword);
        Member m = Member.register(email, hash)
                .withRole(newRole);
        return memberRepository.register(m);
    }

    @Override
    public Member updateMember(
            Long id,
            String email,
            String rawPassword,
            Role newRole,
            Role role) {
        checkAdmin(role);
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id.toString()));
        String hash = rawPassword != null && !rawPassword.isBlank()
                ? sha256(rawPassword)
                : existing.getPassword().password();
        Member updated = existing.withEmail(email)
                .withPasswordHash(hash)
                .withRole(newRole);
        return memberRepository.updateMember(updated);
    }

    @Override
    public void deleteMember(Long id, Role role) {
        checkAdmin(role);
        memberRepository.deleteById(id);
    }
}
