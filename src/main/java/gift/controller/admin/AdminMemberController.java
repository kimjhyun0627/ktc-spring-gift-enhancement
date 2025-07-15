package gift.controller.admin;

import gift.annotation.CurrentRole;
import gift.dto.member.MemberForm;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.MemberNotFoundException;
import gift.service.member.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String list(@CurrentRole Role role, Model model) {
        List<Member> members = memberService.getAllMembers(role);
        model.addAttribute("members", members);
        return "admin/member_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute(
                "memberForm",
                new MemberForm(null, "", "", Role.USER));
        return "admin/member_form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("memberForm") MemberForm memberForm,
            BindingResult br,
            RedirectAttributes ra,
            @CurrentRole Role role
    ) {
        if (br.hasErrors()) {
            return "admin/member_form";
        }
        Member created = memberService.createMember(
                memberForm.email(), memberForm.password(), memberForm.role(), role
        );
        ra.addFlashAttribute("info", "회원이 등록되었습니다: " + created.getEmail());
        return "redirect:/admin/members";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            @CurrentRole Role role
    ) {
        Member m = memberService.getMemberById(id, role)
                .orElseThrow(() -> new MemberNotFoundException(id.toString()));
        model.addAttribute("memberForm", new MemberForm(
                m.getId().id(), m.getEmail().email(), m.getPassword().passwordHash(), m.getRole()
        ));
        return "admin/member_form";
    }

    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("memberForm") MemberForm memberForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @CurrentRole Role role
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/member_form";
        }
        Member updated = memberService.updateMember(
                id, memberForm.email(), memberForm.password(), memberForm.role(), role
        );
        redirectAttributes.addFlashAttribute("info", "회원 정보가 수정되었습니다: " + updated.getEmail());
        return "redirect:/admin/members";
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @CurrentRole Role role
    ) {
        memberService.deleteMember(id, role);
        redirectAttributes.addFlashAttribute("info", "회원이 삭제되었습니다. ID=" + id);
        return "redirect:/admin/members";
    }
}
