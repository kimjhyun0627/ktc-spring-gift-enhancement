package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class OptionAlreadyExistException extends BaseException {

    public OptionAlreadyExistException() {

        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "해당 옵션이 이미 존재합니다.");
    }
}
