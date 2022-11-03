package fet.datn.interceptor;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
public class Payload {
    private Long userId;
    private String userName;
    private String token;
    private Date expiredTime;
}
