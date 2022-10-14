package fet.datn.utils;

public enum OtpTypeEnum {
    LOGIN("LOGIN"),
    FORGOT_PASSWORD("FORGOT PASSWORD");

    private String value;

    OtpTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}