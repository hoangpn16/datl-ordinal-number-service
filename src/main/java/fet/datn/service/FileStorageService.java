package fet.datn.service;

import fet.datn.request.UploadImage;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

public interface FileStorageService {
    String saveImage(Long id, UploadImage imageBase64);

    @SneakyThrows
    Resource loadImage(Long id, String fileName);

    boolean deleteFileByPath(Long id, String fileName);
}
