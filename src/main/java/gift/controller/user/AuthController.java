package gift.controller.user;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.service.member.MemberService;
import gift.util.BasicAuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = memberService.register(authRequest);
        return ResponseEntity.status(201).body(authResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestHeader("Authorization") String authHeader) {
        BasicAuthUtil.Credentials parsed = BasicAuthUtil.parse(authHeader);
        String email = parsed.email();
        String password = parsed.password();

        AuthResponse authResponse = memberService.login(email, password);
        return ResponseEntity.ok(authResponse);
    }
}
