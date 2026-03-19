package service.CSFC.CSFC_auth_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createAccountByAdmin_success() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("test@example.com", "Test User", "Some address", null);
        UserResponse expected = new UserResponse(UUID.randomUUID(), "Test User", "test@example.com", "USER", "Some address");

        when(userService.CreateUserWithRoleByAdmin(request)).thenReturn(expected);

        // Act
        var responseEntity = userController.createAccountByAdmin(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        BaseResponse<UserResponse> body = responseEntity.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Tạo tài khoản thành công", body.getMessage());
        assertEquals(expected, body.getData());
    }
}
