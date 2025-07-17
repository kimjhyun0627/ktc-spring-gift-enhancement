package gift.repository.wish;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.entity.member.Member;
import gift.entity.product.Product;
import gift.entity.wish.Wish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class WishRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private WishRepository wishRepository;

    private Member createAndPersistMember(String suffix) {
        String email = "user" + suffix + "@example.com";
        Member m = Member.register(email, "a".repeat(64));
        return em.persistAndFlush(m);
    }

    private Product createAndPersistProduct(Long id) {
        Product p = Product.of(id, "Test Product", 500, "http://img.url");
        return em.persistAndFlush(p);
    }

    @Test
    @DisplayName("Wish 저장 후 ID 생성 및 필드 확인")
    void saveAndGenerateId() {
        Member member = createAndPersistMember("1");
        Product product = createAndPersistProduct(100L);

        Wish wish = Wish.of(member, product, 3);
        Wish saved = wishRepository.saveAndFlush(wish);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMember().getId().id()).isEqualTo(member.getId().id());
        assertThat(saved.getProduct().getId().id()).isEqualTo(product.getId().id());
        assertThat(saved.getAmount().amount()).isEqualTo(3);
    }


    @Test
    @DisplayName("findByMember: 회원으로 조회 (페이징)")
    void findByMember_withPaging() {
        Member member = createAndPersistMember("2");
        Product p1 = createAndPersistProduct(101L);
        Product p2 = createAndPersistProduct(102L);

        wishRepository.saveAndFlush(Wish.of(member, p1, 1));
        wishRepository.saveAndFlush(Wish.of(member, p2, 2));

        Pageable pageable = PageRequest.of(0, 10);  // 0페이지, 사이즈 10
        Page<Wish> page = wishRepository.findByMember_Id(member.getId().id(), pageable);

        assertThat(page.getContent()).hasSize(2)
                .extracting(w -> w.getProduct().getId().id())
                .containsExactlyInAnyOrder(101L, 102L);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotalPages()).isEqualTo(1);
    }


    @Test
    @DisplayName("중복(member+product) 저장 시 예외 발생")
    void duplicateMemberProduct_throwsException() {
        Member member = createAndPersistMember("6");
        Product product = createAndPersistProduct(400L);

        wishRepository.saveAndFlush(Wish.of(member, product, 1));

        assertThrows(DataIntegrityViolationException.class,
                () -> wishRepository.saveAndFlush(Wish.of(member, product, 2)));
    }
}
