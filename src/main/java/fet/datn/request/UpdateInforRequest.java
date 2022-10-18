package fet.datn.request;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UpdateInforRequest {
    private String fullName;
    private String phone;
    private String email;
}
