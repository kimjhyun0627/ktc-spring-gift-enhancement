package gift.service.member;

import static gift.util.HashUtil.sha256;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.InvalidAuthExeption;
import gift.exception.custom.MemberAlreadyExistsException;
import gift.exception.custom.MemberNotFoundException;
import gift.repository.member.MemberRepository;
import gift.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    public AuthResponse register(AuthRequest authRequest) {
        String hash = sha256(authRequest.password());
        Member member = Member.register(authRequest.email(), hash);
        try {
            member = memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new MemberAlreadyExistsException(authRequest.email());
        }
        String token = jwtUtil.generateToken(member.getId().id(), member.getRole());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(String email, String rawPassword) {
        Member member = memberRepository.findByEmail_Email(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
        if (!member.getPassword().passwordHash().equals(sha256(rawPassword))) {
            throw new MemberNotFoundException(email);
        }
        String token = jwtUtil.generateToken(member.getId().id(), member.getRole());
        return new AuthResponse(token);
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
        Member newMember = Member.register(email, hash)
                .withRole(newRole);
        return memberRepository.save(newMember);
    }

    @Override
    public Member updateMember(
            Long id,
            String email,
            String rawPassword,
            Role newRole,
            Role role) {
        checkAdmin(role);
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id.toString()));
        String hash = rawPassword != null && !rawPassword.isBlank()
                ? sha256(rawPassword)
                : existingMember.getPassword().passwordHash();
        existingMember.changeEmail(email);
        existingMember.changePasswordHash(hash);
        existingMember.changeRole(newRole);
        return existingMember;
    }

    @Override
    public void deleteMember(Long id, Role role) {
        checkAdmin(role);
        memberRepository.deleteById(id);
    }

    private void checkAdmin(Role role) {
        if (role.isUser()) {
            throw new InvalidAuthExeption("관리자 권한이 필요합니다.");
        }
    }
}
