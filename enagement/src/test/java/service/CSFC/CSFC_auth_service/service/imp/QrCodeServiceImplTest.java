package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CSFC.CSFC_auth_service.common.exception.QrGenerationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class QrCodeServiceImplTest {

    private QrCodeServiceImpl qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = new QrCodeServiceImpl();
    }

    @Test
    void generateQrBase64_validContent_shouldReturnBase64Png() {
        String result = qrCodeService.generateQrBase64("HELLO-QR");

        assertNotNull(result);
        assertTrue(result.startsWith("data:image/png;base64,"));
        assertTrue(result.length() > "data:image/png;base64,".length());
    }

    @Test
    void generateQrBase64_nullContent_shouldThrow() {
        assertThrows(QrGenerationException.class,
                () -> qrCodeService.generateQrBase64(null));
    }

    @Test
    void generateQrBase64_emptyContent_shouldThrow() {
        assertThrows(QrGenerationException.class,
                () -> qrCodeService.generateQrBase64(""));
    }
}