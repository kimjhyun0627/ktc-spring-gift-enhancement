package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidMemberException extends BaseException {

    public InvalidMemberException(String message) {

        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, message);
    }
}
