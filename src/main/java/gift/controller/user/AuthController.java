package gift.controller.user;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.service.member.MemberService;
import gift.util.BasicAuthUtil;
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
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest) {
        AuthResponse resp = memberService.register(authRequest);
        return ResponseEntity.status(201).body(resp);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestHeader("Authorization") String authHeader) {
        BasicAuthUtil.Credentials cred = BasicAuthUtil.parse(authHeader);
        String email = cred.email();
        String password = cred.password();

        AuthResponse resp = memberService.login(email, password);
        return ResponseEntity.ok(resp);
    }
}
