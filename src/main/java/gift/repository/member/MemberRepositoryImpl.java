package gift.repository.member;

import gift.entity.member.Member;
import gift.exception.custom.MemberAlreadyExistException;
import gift.exception.custom.MemberNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final DataSource dataSource;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public MemberRepositoryImpl(DataSource ds) {
        this.dataSource = ds;
        this.simpleJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Member register(Member member) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", member.getEmail().email());
        params.put("password_hash", member.getPassword().password());
        params.put("role", member.getRole().name());
        params.put("created_at", Timestamp.valueOf(member.getCreatedAt()));

        try {
            Number newId = simpleJdbcInsert.executeAndReturnKey(params);
            return member.withId(newId.longValue());
        } catch (DataIntegrityViolationException e) {
            throw new MemberAlreadyExistException("이미 가입된 이메일입니다.");
        }
    }

    @Override
    public Member updateMember(Member member) {
        String sql = """
                UPDATE member
                   SET email = ?, password_hash = ?, role = ?
                 WHERE id = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getEmail().email());
            ps.setString(2, member.getPassword().password());
            ps.setString(3, member.getRole().name());
            ps.setLong(4, member.getId().id());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new MemberNotFoundException(member.getId().toString());
            }
            return member;
        } catch (SQLException e) {
            throw new MemberNotFoundException(member.getEmail().email());
        }
    }


    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, email, password_hash, role, created_at FROM member WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new MemberNotFoundException(email);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT id, email, password_hash, role, created_at FROM member";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Member> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, email, password_hash, role, created_at FROM member WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new MemberNotFoundException(id);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM member WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MemberNotFoundException(id);
        }
    }

    private Member mapRow(ResultSet resultSet) throws Exception {
        return Member.of(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                resultSet.getString("role"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
