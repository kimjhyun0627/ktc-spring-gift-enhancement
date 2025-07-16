package gift.repository.wish;

import gift.entity.wish.Wish;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @EntityGraph(attributePaths = {"member", "product"})
    List<Wish> findByMember_Id(Long memberId);
}
