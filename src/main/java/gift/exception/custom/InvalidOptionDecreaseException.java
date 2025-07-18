package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidOptionDecreaseException extends BaseException {

    public InvalidOptionDecreaseException() {
        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "잘못된 수량 수정 요청입니다");
    }
}
