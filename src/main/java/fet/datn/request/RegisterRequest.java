package fet.datn.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String passwordRepeat;
    private String role;
}
