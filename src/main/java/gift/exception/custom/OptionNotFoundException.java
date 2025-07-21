package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class OptionNotFoundException extends BaseException {

    public OptionNotFoundException(Long optionId) {
        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "해당하는 옵션을 찾을 수 없습니다: " + optionId);
    }
}
