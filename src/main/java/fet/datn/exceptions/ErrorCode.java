package fet.datn.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SUCCESS(HttpStatus.OK, "200", "Thành công"),
    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SSHOP-500", "Có lỗi xảy ra, xin vui lòng thử lại sau ít phút"),
    ENTITY_NOT_EXISTS(HttpStatus.NOT_FOUND, "SSHOP-404", "Thực thể không tồn tại"),
    ENTITY_EXISTED(HttpStatus.BAD_REQUEST, "SSHOP-409", "Thực thể đã tồn tại"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "SSHOP-402", "Truyền sai tham số"),
    INVALID_SHOPID(HttpStatus.BAD_REQUEST, "SSHOP-403", "ShopId isn't config in pod service"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "SSHOP-405", "Mã truy cập hết hạn"),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "SSHOP-406", "Không tìm thấy mã truy cập"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "SSHOP-407", "Mã truy cập không hợp lệ"),
    CANNOT_SEND_MESSAGE(HttpStatus.BAD_REQUEST, "SSHOP-408", "Không thể gửi thông báo"),
    UNAUTHORIZED(HttpStatus.BAD_REQUEST, "SSHOP-409", "Thông tin xác thực bị thiếu hoặc không chính xác"),
    INVALID_USER_PASS(HttpStatus.UNAUTHORIZED, "SSHOP-401", "Tài khoản hoặc mật khẩu không đúng, quý khách vui lòng kiểm tra và đăng nhập lại"),
    DATE_NOT_VALID(HttpStatus.BAD_REQUEST, "SSHOP-500", "Thời gian truyền vào không hợp lệ"),

    CANT_SEND_OTP(HttpStatus.SERVICE_UNAVAILABLE, "SSHOP-601", "Lỗi khi gửi tin nhắn mã OTP"), //"Error when send otp notification message"
    OTP_REFERENCE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "SSHOP-602", "Không tìm thấy mã tham chiếu OTP"), //OTP reference id is not found
    ERROR_SEND_OTP(HttpStatus.BAD_REQUEST, "SSHOP-603", "Gửi tin nhắn thông báo OTP không thành công"), //"send otp notification message is not successful"
    OTP_REFERENCE_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "SSHOP-604", "Yêu cầu mã tham chiếu OTP"), //Otp_reference_id must be specified
    MAXIMUM_OTP_GENERATION_REACHED(HttpStatus.BAD_REQUEST, "SSHOP-605", "Đã đạt tối đa số lần gửi OTP"), //Maximum OTP generation reached
    CANT_INSTANT_REGENERATE_OTP_AFTER_CREATE(HttpStatus.BAD_REQUEST, "SSHOP-606", "Vui lòng không gửi lại quá nhanh"),
    OTP_USED(HttpStatus.BAD_REQUEST, "SSHOP-607", "Mã OTP đã được sử dụng"), //OTP code is already used
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "SSHOP-608", "Mã OTP đã hết hạn"), //OTP expired
    INVALID_OTP_REGISTER_REQUEST(HttpStatus.BAD_REQUEST, "SSHOP-609", "Yêu cầu số điện thoại khi đăng ký"), //Invalid OTP request
    INVALID_OTP_TYPE(HttpStatus.BAD_REQUEST, "SSHOP-610", "Kiểu OTP không hợp lệ"), //Invalid OTP type
    INVALID_MOBILE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "SSHOP-611", "Định dạng số điện thoại không hợp lệ"), //Invalid mobile number format
    OTP_VERIFICATION_FAIL(HttpStatus.BAD_REQUEST, "SSHOP-612", "Xác minh mã OTP không thành công"), //OTP Verification fail
    MOBILE_REGISTERED(HttpStatus.BAD_REQUEST, "SSHOP-613", "Số điện thoại đã được đăng ký"), //"phone_number_registered", "Phone number is already registered before"
    MOBILE_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "SSHOP-614", "Số điện thoại chưa được đăng ký"), //"phone_number_not_registered", "Phone number is not registered"
    MAXIMUM_OTP_VERIFICATION_REACHED(HttpStatus.BAD_REQUEST, "SSHOP-615", "Đã đạt tối đa số lần gửi OTP"), //Maximum OTP generation reached
    MOBILE_BLOCKED(HttpStatus.FORBIDDEN, "SSHOP-616", "Số điện thoại này đã bị chặn do yêu cầu quá nhiều OTP"), //"blocked_mobile", "This mobile is blocked because request too many OTP"
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "SSHOP-617", "Mật khẩu không hợp lệ"), //"blocked_mobile", "This mobile is blocked because request too many OTP"
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "SSHOP-618", "Yêu cầu không hợp lệ");


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
