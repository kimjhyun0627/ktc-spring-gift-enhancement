package gift.entity.wish.value;

import java.util.Objects;

public record WishId(Long id) {

    public WishId {
        Objects.requireNonNull(id, "WishId 는 null일 수 없습니다.");
    }
}
