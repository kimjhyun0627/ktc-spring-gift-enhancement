// src/main/java/gift/repository/wish/WishRepository.java
package gift.repository.wish;

import gift.entity.member.Member;
import gift.entity.product.Product;
import gift.entity.wish.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @EntityGraph(attributePaths = {"member", "product"})
    List<Wish> findByMember(Member member);

    @EntityGraph(attributePaths = {"member", "product"})
    List<Wish> findByProduct(Product product);

    @EntityGraph(attributePaths = {"member", "product"})
    Optional<Wish> findByMemberAndProduct(Member member, Product product);
}
