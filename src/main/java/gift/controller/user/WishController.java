package gift.controller.user;

import gift.annotation.LoginMember;
import gift.dto.wish.WishRequest;
import gift.dto.wish.WishResponse;
import gift.entity.member.Member;
import gift.entity.wish.Wish;
import gift.service.wish.WishService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public List<WishResponse> list(@LoginMember Member member) {
        System.out.println("memberId: " + member.getId().id());
        return wishService.getWishes(member.getId().id()).stream()
                .map(w -> WishResponse.of(w.getId(), w.getProductId(), w.getAmount()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public WishResponse create(@RequestBody WishRequest request,
            @LoginMember Member member) {
        Wish w = wishService.addWish(member.getId().id(), request.productId(), request.amount());
        return WishResponse.of(w.getId(), w.getProductId(), w.getAmount());
    }

    @PutMapping("/{wishId}")
    public WishResponse update(@PathVariable("wishId") Long wishId,
            @RequestBody WishRequest request,
            @LoginMember Member member) {
        Wish wish = wishService.updateWish(wishId, member.getId().id(), request.productId(),
                request.amount());
        return WishResponse.of(wish.getId(), wish.getProductId(), wish.getAmount());
    }

    @DeleteMapping("/{wishId}")
    public void delete(@PathVariable Long wishId,
            @LoginMember Member member) {
        wishService.removeWish(member.getId().id(), wishId);
    }
}
