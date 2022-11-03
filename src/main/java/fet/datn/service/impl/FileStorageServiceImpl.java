package fet.datn.service.impl;

import fet.datn.config.FileStorageProperties;
import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.exceptions.FileStorageException;
import fet.datn.request.UploadImage;
import fet.datn.service.FileStorageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private Path fileStorageLocation;

    @Autowired
    FileStorageProperties fileStorageProperties;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        if (!Files.exists(this.fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new FileStorageException(
                        "Could not create the directory where the uploaded files will be stored.", ex);
            }
        }

    }

    @Override
    public String saveImage(Long id, UploadImage imageBase64) {
        String name = imageBase64.getImageName().replaceAll("\\s+", "");
        String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(name);

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path shopFolder = this.fileStorageLocation.resolve(String.valueOf(id));

            if (!Files.exists(shopFolder)) {
                Files.createDirectories(shopFolder);
            }

            Path targetLocation = shopFolder.resolve(fileName);

            File file = getImageFromBase64(imageBase64.getImageBase64(), imageBase64.getImageName());

            Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            file.delete();
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    @SneakyThrows
    public Resource loadImage(Long id, String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(String.valueOf(id)).resolve(fileName)
                    .normalize();
            log.info("URI: " + filePath.toUri());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (Exception ex) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
    }

    @Override
    public boolean deleteFileByPath(Long id, String fileName) {
        File file = null;
        try {
            String absolutePath = this.fileStorageLocation + "/" + id + "/" + fileName;
            file = new File(absolutePath);
            log.info("Delete file [{}]", fileName);
        } catch (Exception e) {
            log.info("Can not delete [{}]", fileName);
            e.printStackTrace();
        }

        return file.delete();
    }


    private File getImageFromBase64(String base64String, String fileName) {
        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) { // check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default: // should write cases for more images types
                extension = "jpg";
                break;
        }

        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
        File file = new File(fileName + extension);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


}

