package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class MemberAlreadyExistsException extends BaseException {

    public MemberAlreadyExistsException(String message) {
        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다: " + message);
    }
}
