package gift.exception.handler;

import gift.exception.BaseException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex) {
        ErrorResponse body = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(body);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<FieldErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorDetail(
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getRejectedValue(),
                        fe.getCode()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                ErrorCode.VALIDATION_FAILED,
                "입력값 검증에 실패했습니다.",
                details,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return handleMethodArgumentNotValid(
                new MethodArgumentNotValidException(null, ex.getBindingResult()),
                headers, status, request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {
        List<FieldErrorDetail> details = ex.getConstraintViolations().stream()
                .map(cv -> new FieldErrorDetail(
                        cv.getPropertyPath().toString(),
                        cv.getMessage(),
                        cv.getInvalidValue(),
                        cv.getConstraintDescriptor()
                                .getAnnotation()
                                .annotationType()
                                .getSimpleName()))
                .toList();

        ErrorResponse body = new ErrorResponse(
                ErrorCode.VALIDATION_FAILED,
                "데이터 무결성 검사에 실패했습니다.",
                details,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorResponse> handleServletException(ServletException ex) {
        ErrorResponse body = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                "서버 처리 중 오류가 발생했습니다.",
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        ErrorResponse body = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                "입출력 처리 중 오류가 발생했습니다.",
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        ErrorResponse body = new ErrorResponse(
                ErrorCode.UNAUTHORIZED,
                "유효하지 않은 토큰입니다.",
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse body = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR,
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
