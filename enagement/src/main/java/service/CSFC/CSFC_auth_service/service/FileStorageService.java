package service.CSFC.CSFC_auth_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveImage(MultipartFile file);
    String updateImage(String existingFilePath, MultipartFile newFile);
    void deleteImage(String filePath);
}
