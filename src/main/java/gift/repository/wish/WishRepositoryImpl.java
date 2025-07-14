package gift.repository.wish;

import gift.entity.member.value.MemberId;
import gift.entity.wish.Wish;
import gift.exception.custom.WishNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class WishRepositoryImpl implements WishRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insert;
    private final RowMapper<Wish> rowMapper = new WishRowMapper();

    public WishRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(dataSource)
                .withTableName("wish")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<Wish> findByMember(MemberId memberId) {
        String sql = "SELECT * FROM wish WHERE member_id = ?";
        return jdbcTemplate.query(sql, rowMapper, memberId.id());
    }

    @Override
    public Wish findById(long id) {
        String sql = "SELECT * FROM wish WHERE id = ?";
        List<Wish> results = jdbcTemplate.query(sql, rowMapper, id);
        if (results.isEmpty()) {
            throw new WishNotFoundException(id);
        }
        return results.getFirst();
    }


    @Override
    public Wish create(Wish wish) {
        Map<String, Object> params = new HashMap<>();
        params.put("member_id", wish.getMemberId().id());
        params.put("product_id", wish.getProductId().productId());
        params.put("amount", wish.getAmount().amount());
        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(params));
        return wish.withId(key.longValue());
    }

    @Override
    public Wish update(Wish wish) {
        String sql = "UPDATE wish SET product_id = ?, amount = ? WHERE id = ?";
        int updated = jdbcTemplate.update(
                sql,
                wish.getProductId().productId(),
                wish.getAmount().amount(),
                wish.getId().id()
        );
        if (updated == 0) {
            throw new WishNotFoundException(wish.getId().id());
        }
        return wish;
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 null이거나 0 이하일 수 없습니다.");
        }
        int deleted = jdbcTemplate.update("DELETE FROM wish WHERE id = ?", id);
        if (deleted == 0) {
            throw new WishNotFoundException(id);
        }
    }
}
