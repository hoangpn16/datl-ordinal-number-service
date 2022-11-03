package fet.datn.request;

import lombok.Data;

@Data
public class UploadImage {
    private String imageBase64;
    private String imageName;
}
