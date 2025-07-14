package gift.repository.wish;

import gift.entity.member.value.MemberId;
import gift.entity.wish.Wish;
import java.util.List;

public interface WishRepository {

    List<Wish> findByMember(MemberId memberId);

    Wish findById(long id);

    Wish create(Wish wish);

    Wish update(Wish wish);

    void delete(Long id);
}
