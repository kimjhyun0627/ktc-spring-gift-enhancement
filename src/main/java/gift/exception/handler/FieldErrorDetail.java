package gift.exception.handler;

public record FieldErrorDetail(
        String field,
        String message,
        Object rejectedValue,
        String errorCode
) {

}
