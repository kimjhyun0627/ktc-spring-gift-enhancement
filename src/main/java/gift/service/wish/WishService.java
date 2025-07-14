package gift.service.wish;

import gift.entity.wish.Wish;
import java.util.List;

public interface WishService {

    List<Wish> getWishes(Long memberId);

    Wish addWish(Long memberId, Long productId, int amount);

    Wish updateWish(Long id, Long memberId, Long productId, int amount);

    void removeWish(Long memberId, Long wishId);


}
