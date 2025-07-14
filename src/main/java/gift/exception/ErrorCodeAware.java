package gift.exception;

import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public interface ErrorCodeAware {

    ErrorCode getErrorCode();

    HttpStatus getHttpStatus();
}
