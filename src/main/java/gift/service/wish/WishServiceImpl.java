package gift.service.wish;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.entity.product.Product;
import gift.entity.wish.Wish;
import gift.exception.custom.InvalidProductException;
import gift.exception.custom.MemberNotFoundException;
import gift.exception.custom.ProductNotFoundException;
import gift.exception.custom.UnauthorizedWishAccessException;
import gift.exception.custom.WishAlreadyExistsException;
import gift.exception.custom.WishNotFoundException;
import gift.repository.wish.WishRepository;
import gift.service.member.MemberService;
import gift.service.product.ProductService;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Override
    @Transactional(readOnly = true)
    public List<Wish> getWishes(Member member) {
        memberService.getMemberById(member.getId().id(), Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(member.getEmail().email()));
        return wishRepository.findByMember(member);
    }

    @Override
    public Wish addWish(Member member, Long productId, int amount) {
        memberService.getMemberById(member.getId().id(), Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(member.getEmail().email()));

        Product existing = productService.getProductById(productId, member.getRole())
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Wish newWish = Wish.of(member, existing, amount);
        try {
            return wishRepository.save(newWish);
        } catch (DataIntegrityViolationException e) {
            throw new WishAlreadyExistsException("해당하는 위시리스트가 이미 존재합니다");
        }
    }

    @Override
    public Wish updateWish(Long wishId, Member member, Long productId, int amount) {
        Wish existing = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException(wishId));
        if (!existing.isOwnedBy(member)) {
            throw new UnauthorizedWishAccessException(member.getId().id(),
                    existing.getMember().getId().id());
        }
        if (!existing.isForProduct(productId)) {
            throw new InvalidProductException("상품의 수량만 변경 가능합니다: " + productId);
        }
        return wishRepository.save(existing.withAmount(amount));
    }

    @Override
    public void removeWish(Long wishId, Member member) {
        Wish existing = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException(wishId));

        if (!existing.isOwnedBy(member)) {
            throw new UnauthorizedWishAccessException(member.getId().id(), existing.getId().id());
        }
        wishRepository.delete(existing);
    }
}
