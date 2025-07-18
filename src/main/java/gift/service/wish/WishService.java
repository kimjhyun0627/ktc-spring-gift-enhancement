package gift.service.wish;

import gift.entity.member.Member;
import gift.entity.wish.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishService {

    Page<Wish> getWishes(Member member, Pageable pageable);

    Wish addWish(Member member, Long productId, int amount);

    Wish changeWishAmount(Long id, Member member, Long productId, int amount);

    void removeWish(Long wishId, Member member);


}
