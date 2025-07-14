package gift.repository.wish;

import gift.entity.member.value.MemberId;
import gift.entity.wish.Wish;
import gift.entity.wish.value.Amount;
import gift.entity.wish.value.ProductId;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WishRowMapper implements RowMapper<Wish> {

    @Override
    public Wish mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        MemberId memberId = new MemberId(rs.getLong("member_id"));
        ProductId productId = new ProductId(rs.getLong("product_id"));
        Amount amount = new Amount(rs.getInt("amount"));
        Wish wish = Wish.of(memberId.id(), productId.productId(), amount.amount());
        return wish.withId(id);
    }
}
