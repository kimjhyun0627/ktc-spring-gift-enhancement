package gift.exception;

import gift.exception.handler.ErrorCode;
import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException implements ErrorCodeAware {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
