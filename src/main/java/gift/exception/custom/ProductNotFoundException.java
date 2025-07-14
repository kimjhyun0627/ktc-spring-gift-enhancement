package gift.exception.custom;

import gift.exception.BaseException;
import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {

    public ProductNotFoundException(Long id) {

        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다 " + id);
    }
}
