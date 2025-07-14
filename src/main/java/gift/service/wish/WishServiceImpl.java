package gift.service.wish;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.wish.Wish;
import gift.exception.custom.InvalidProductException;
import gift.exception.custom.MemberNotFoundException;
import gift.exception.custom.ProductNotFoundException;
import gift.exception.custom.UnauthorizedWishAccessException;
import gift.exception.custom.WishNotFoundException;
import gift.repository.wish.WishRepository;
import gift.service.member.MemberService;
import gift.service.product.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;
    private final MemberService memberService;

    public WishServiceImpl(
            WishRepository wishRepository,
            ProductService productService,
            MemberService memberService
    ) {
        this.wishRepository = wishRepository;
        this.productService = productService;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public List<Wish> getWishes(Long memberId) {
        Member member = memberService.getMemberById(memberId, Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return wishRepository.findByMember(member.getId());
    }

    public Wish addWish(Long memberId, Long productId, int amount) {
        Member member = memberService.getMemberById(memberId, Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Product existing = productService.getProductById(productId, member.getRole())
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return wishRepository.create(
                Wish.of(member.getId().id(), existing.id().id(), amount)
        );
    }

    public Wish updateWish(Long id, Long memberId, Long productId, int amount) {
        Wish existing = wishRepository.findById(id);
        if (existing == null) {
            throw new WishNotFoundException(id);
        }
        if (!existing.isOwnedBy(memberId)) {
            throw new UnauthorizedWishAccessException(memberId, id);
        }
        if (!existing.isForProduct(productId)) {
            throw new InvalidProductException("상품의 수량만 변경 가능합니다: " + productId);
        }
        existing.withAmount(amount);
        return wishRepository.update(existing);
    }

    public void removeWish(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId);
        if (!wish.isOwnedBy(memberId)) {
            throw new UnauthorizedWishAccessException(memberId, wish.getId().id());
        }
        wishRepository.delete(wish.getId().id());
    }
}
