package fet.datn.request;

import lombok.Data;

@Data
public class ChangePassword {
    private String username;
    private String password;
    private String passwordAgain;
}
