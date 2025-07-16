package gift.service.wish;

import gift.entity.member.Member;
import gift.entity.wish.Wish;
import java.util.List;

public interface WishService {

    List<Wish> getWishes(Member member);

    Wish addWish(Member member, Long productId, int amount);

    Wish changeWishAmount(Long id, Member member, Long productId, int amount);

    void removeWish(Long wishId, Member member);


}
