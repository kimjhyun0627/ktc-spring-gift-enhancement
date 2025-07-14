package gift.controller.admin;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private String extractId(Claims claims) {
        return claims.getSubject();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest httpServletRequest, Model model) {
        Claims claims = (Claims) httpServletRequest.getAttribute("authClaims");
        if (claims != null) {
            model.addAttribute("email", extractId(claims));
        }
        return "admin/dashboard";
    }
}
