package service.CSFC.CSFC_auth_service.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    // ========== saveImage SUCCESS ==========
    @Test
    void saveImage_success() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy-image".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url",
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/abc.jpg");

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(uploadResult);

        String result = fileStorageService.saveImage(file);

        assertEquals(
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/abc.jpg",
                result
        );
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    // ========== saveImage FAIL: file null ==========
    @Test
    void saveImage_fileNull_throwException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileStorageService.saveImage(null));

        assertEquals("File is empty", ex.getMessage());
        verifyNoInteractions(cloudinary, uploader);
    }

    // ========== saveImage FAIL: not image ==========
    @Test
    void saveImage_notImage_throwException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileStorageService.saveImage(file));

        assertEquals("File must be an image", ex.getMessage());
        verifyNoInteractions(cloudinary, uploader);
    }

    // ========== saveImage FAIL: IOException ==========
    @Test
    void saveImage_uploadIOException_throwException() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenThrow(new IOException("Upload failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileStorageService.saveImage(file));

        assertTrue(ex.getMessage().contains("Upload image failed"));
    }

    // ========== updateImage: newFile null ==========
    @Test
    void updateImage_newFileNull_returnOldUrl() {
        String oldUrl =
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/abc.jpg";

        String result = fileStorageService.updateImage(oldUrl, null);

        assertEquals(oldUrl, result);
        verifyNoInteractions(cloudinary, uploader);
    }

    // ========== updateImage: newFile exists ==========
    @Test
    void updateImage_newFileExists_deleteOldAndUploadNew() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);

        String oldUrl =
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/abc.jpg";

        MockMultipartFile newFile = new MockMultipartFile(
                "file",
                "new.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url",
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/new.jpg");

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(uploadResult);

        String result = fileStorageService.updateImage(oldUrl, newFile);

        assertEquals(
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/new.jpg",
                result
        );
        verify(uploader).destroy(eq("rewards/abc"), anyMap());
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    // ========== deleteImage: url null ==========
    @Test
    void deleteImage_urlNull_doNothing() {
        fileStorageService.deleteImage(null);
        verifyNoInteractions(cloudinary, uploader);
    }

    // ========== deleteImage SUCCESS ==========
    @Test
    void deleteImage_success() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);

        String url =
                "https://res.cloudinary.com/demo/image/upload/v1/rewards/abc.jpg";

        fileStorageService.deleteImage(url);

        verify(uploader).destroy(eq("rewards/abc"), anyMap());
    }

    // ========= IO EXCEPTION & EDGE CASE TESTS ==========

    @Test
    void saveImage_invalidContentType_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());

        assertThrows(RuntimeException.class, () -> fileStorageService.saveImage(file));
        verifyNoInteractions(cloudinary);
    }

    @Test
    void saveImage_nullContentType_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", null, "test".getBytes());

        assertThrows(RuntimeException.class, () -> fileStorageService.saveImage(file));
    }

    @Test
    void saveImage_cloudinaryIOException_shouldThrow() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test content".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Network error"));

        assertThrows(RuntimeException.class, () -> fileStorageService.saveImage(file));
    }

    @Test
    void saveImage_largeBinaryFile_success() throws IOException {
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", largeContent);

        Map mockResult = new HashMap<>();
        mockResult.put("secure_url", "https://example.com/large.jpg");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(mockResult);

        String result = fileStorageService.saveImage(file);

        assertEquals("https://example.com/large.jpg", result);
    }

    @Test
    void updateImage_newFileNull_returnExisting() {
        String existingUrl = "https://example.com/old.jpg";

        String result = fileStorageService.updateImage(existingUrl, null);

        assertEquals(existingUrl, result);
        verifyNoInteractions(cloudinary);
    }

    @Test
    void updateImage_newFileEmpty_returnExisting() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);
        String existingUrl = "https://example.com/old.jpg";

        String result = fileStorageService.updateImage(existingUrl, emptyFile);

        assertEquals(existingUrl, result);
    }

    @Test
    void updateImage_success_deleteOldAndUploadNew() throws IOException {
        String existingUrl = "https://example.com/old.jpg";
        MockMultipartFile newFile = new MockMultipartFile(
                "file", "new.jpg", "image/jpeg", "new content".getBytes());

        Map mockUploadResult = new HashMap<>();
        mockUploadResult.put("secure_url", "https://example.com/new.jpg");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(mockUploadResult);

        String result = fileStorageService.updateImage(existingUrl, newFile);

        assertEquals("https://example.com/new.jpg", result);
        verify(uploader).destroy(anyString(), any());
    }

    @Test
    void deleteImage_null_shouldNotThrow() {
        fileStorageService.deleteImage(null);

        verifyNoInteractions(cloudinary);
    }

    @Test
    void deleteImage_blank_shouldNotThrow() {
        fileStorageService.deleteImage("   ");

        verifyNoInteractions(cloudinary);
    }

    @Test
    void deleteImage_cloudinaryException_shouldThrow() throws Exception {
        String url = "https://example.com/rewards/image.jpg";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), any())).thenThrow(new RuntimeException("Delete failed"));

        assertThrows(RuntimeException.class, () -> fileStorageService.deleteImage(url));
    }

    @Test
    void saveImage_pngFormat_success() throws IOException {
        MockMultipartFile pngFile = new MockMultipartFile(
                "file", "test.png", "image/png", "png content".getBytes());

        Map mockResult = new HashMap<>();
        mockResult.put("secure_url", "https://example.com/test.png");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(mockResult);

        String result = fileStorageService.saveImage(pngFile);

        assertEquals("https://example.com/test.png", result);
    }

    @Test
    void saveImage_gifFormat_success() throws IOException {
        MockMultipartFile gifFile = new MockMultipartFile(
                "file", "test.gif", "image/gif", "gif content".getBytes());

        Map mockResult = new HashMap<>();
        mockResult.put("secure_url", "https://example.com/test.gif");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(mockResult);

        String result = fileStorageService.saveImage(gifFile);

        assertEquals("https://example.com/test.gif", result);
    }

    @Test
    void saveImage_uploadResultMissingUrl_shouldThrow() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test".getBytes());

        Map mockResult = new HashMap<>(); // No secure_url key

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(mockResult);

        assertThrows(Exception.class, () -> fileStorageService.saveImage(file));
    }

    @Test
    void updateImage_uploadFails_shouldThrow() throws IOException {
        String existingUrl = "https://example.com/old.jpg";
        MockMultipartFile newFile = new MockMultipartFile(
                "file", "new.jpg", "image/jpeg", "new".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), any())).thenReturn(null);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Upload failed"));

        assertThrows(RuntimeException.class, () -> fileStorageService.updateImage(existingUrl, newFile));
    }
}