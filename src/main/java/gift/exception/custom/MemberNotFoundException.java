package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException(Long id) {

        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다: " + id);
    }

    public MemberNotFoundException(String email) {

        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다: " + email);
    }
}
