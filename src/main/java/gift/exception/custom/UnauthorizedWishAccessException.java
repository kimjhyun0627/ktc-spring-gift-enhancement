package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedWishAccessException extends BaseException {

    public UnauthorizedWishAccessException(Long memberId, Long wishId) {
        super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED,
                "유효하지 않은 접근입니다: ID " + memberId + " != " + wishId);
    }
}
