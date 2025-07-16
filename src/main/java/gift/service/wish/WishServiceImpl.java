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
    @Transactional
    public Wish addWish(Member member, Long productId, int amount) {
        memberService.getMemberById(member.getId().id(), Role.ADMIN)
                .orElseThrow(() -> new MemberNotFoundException(member.getEmail().email()));

        Product existingProduct = productService.getProductById(productId, member.getRole())
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Wish newWish = Wish.of(member, existingProduct, amount);
        try {
            return wishRepository.save(newWish);
        } catch (DataIntegrityViolationException e) {
            throw new WishAlreadyExistsException("해당하는 위시리스트가 이미 존재합니다");
        }
    }

    @Override
    @Transactional
    public Wish changeWishAmount(Long wishId, Member member, Long productId, int amount) {
        Wish existingWish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException(wishId));
        if (!existingWish.isOwnedBy(member)) {
            throw new UnauthorizedWishAccessException(member.getId().id(),
                    existingWish.getMember().getId().id());
        }
        if (!existingWish.isForProduct(productId)) {
            throw new InvalidProductException("상품의 수량만 변경 가능합니다: " + productId);
        }
        return wishRepository.save(existingWish.withAmount(amount));
    }

    @Override
    public void removeWish(Long wishId, Member member) {
        Wish targetWish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException(wishId));

        if (!targetWish.isOwnedBy(member)) {
            throw new UnauthorizedWishAccessException(member.getId().id(), targetWish.getId().id());
        }
        wishRepository.delete(targetWish);
    }
}
