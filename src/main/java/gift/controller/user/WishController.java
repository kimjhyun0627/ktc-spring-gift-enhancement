package gift.controller.user;

import gift.annotation.LoginMember;
import gift.dto.wish.WishRequest;
import gift.dto.wish.WishResponse;
import gift.entity.member.Member;
import gift.entity.wish.Wish;
import gift.service.wish.WishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public Page<WishResponse> list(
            @LoginMember Member member,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return wishService.getWishes(member, pageable)
                .map(w -> WishResponse.of(
                        w.getId(),
                        w.getProduct().getId(),
                        w.getAmount()
                ));
    }

    @PostMapping
    public WishResponse create(@RequestBody WishRequest request,
            @LoginMember Member member) {
        Wish wish = wishService.addWish(member, request.productId(), request.amount());
        return WishResponse.of(wish.getId(), wish.getProduct().getId(), wish.getAmount());
    }

    @PutMapping("/{wishId}")
    public WishResponse update(@PathVariable("wishId") Long wishId,
            @RequestBody WishRequest request,
            @LoginMember Member member) {
        Wish wish = wishService.changeWishAmount(wishId, member, request.productId(),
                request.amount());
        return WishResponse.of(wish.getId(), wish.getProduct().getId(), wish.getAmount());
    }

    @DeleteMapping("/{wishId}")
    public void delete(@PathVariable Long wishId,
            @LoginMember Member member) {
        wishService.removeWish(wishId, member);
    }
}
