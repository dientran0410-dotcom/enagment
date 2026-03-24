package service.CSFC.CSFC_auth_service.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.service.FileStorageService;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File must be an image");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "rewards",
                            "resource_type", "image"
                    )
            );

            // URL ảnh
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Upload image failed", e);
        }
    }

    @Override
    public String updateImage(String existingImageUrl, MultipartFile newFile) {

        if (newFile == null || newFile.isEmpty()) {
            return existingImageUrl;
        }

        // XÓA ẢNH CŨ TRÊN CLOUD
        deleteImage(existingImageUrl);

        return saveImage(newFile);
    }

    @Override
    public void deleteImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            // Lấy public_id từ URL
            String publicId = extractPublicId(imageUrl);

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa ảnh trên Cloudinary", e);
        }
    }

    private String extractPublicId(String imageUrl) {

        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1]; // abc.jpg
        String folder = parts[parts.length - 2];   // rewards

        return folder + "/" + fileName.substring(0, fileName.lastIndexOf("."));
    }
}