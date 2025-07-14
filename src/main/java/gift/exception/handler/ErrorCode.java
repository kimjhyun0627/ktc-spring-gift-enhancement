package gift.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ErrorCode {
    VALIDATION_FAILED,
    FORBIDDEN,
    NOT_FOUND,
    BAD_REQUEST,
    UNAUTHORIZED,
    DATABASE_ERROR,
    INTERNAL_ERROR
}
