package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidProductException extends BaseException {

    public InvalidProductException(String message) {
        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, message);
    }
}
