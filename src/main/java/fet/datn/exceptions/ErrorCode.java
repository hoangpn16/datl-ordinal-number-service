package fet.datn.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SUCCESS(HttpStatus.OK, "200", "Thành công"),
    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Thất bại"),
    ENTITY_NOT_EXISTS(HttpStatus.NOT_FOUND,"404","Thực thể không tồn tại"),
    DATE_NOT_VALIDATE(HttpStatus.BAD_REQUEST,"400","Ngày nhập vào không hợp lệ");


    private final HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
