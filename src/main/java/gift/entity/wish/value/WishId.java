package gift.entity.wish.value;

import gift.exception.custom.InvalidAuthExeption;
import jakarta.persistence.Column;
import java.util.Objects;

public record WishId(
        @Column(name = "id")
        Long id
) {

    public WishId {
        Objects.requireNonNull(id, "WishId 는 null일 수 없습니다.");
        if (id <= 0) {
            throw new InvalidAuthExeption("WishId는 0 이하일 수 없습니다.");
        }
    }
}
