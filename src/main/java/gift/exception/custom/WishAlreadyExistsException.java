package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class WishAlreadyExistsException extends BaseException {

    public WishAlreadyExistsException(String message) {
        super(ErrorCode.CONFLICT, HttpStatus.CONFLICT, message);
    }
}
