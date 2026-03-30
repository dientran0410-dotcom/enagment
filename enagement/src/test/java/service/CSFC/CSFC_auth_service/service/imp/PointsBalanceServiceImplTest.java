package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CSFC.CSFC_auth_service.mapper.PointsBalanceMapper;
import service.CSFC.CSFC_auth_service.model.dto.response.PointsBalanceResponse;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.repository.CustomerFranchiseRepository;

@ExtendWith(MockitoExtension.class)
class PointsBalanceServiceImplTest {

    @Mock
    private CustomerFranchiseRepository customerFranchiseRepository;

    @Mock
    private PointsBalanceMapper pointsBalanceMapper;

    @InjectMocks
    private PointsBalanceServiceImpl pointsBalanceService;

    private UUID customerId;
    private UUID franchiseId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        franchiseId = UUID.randomUUID();
    }

    // Case 1: customerId null -> return null
    @Test
    void getPointsBalance_customerIdNull_shouldReturnNull() {
        PointsBalanceResponse res =
                pointsBalanceService.getPointsBalance(null, franchiseId);

        assertNull(res);
        verifyNoInteractions(customerFranchiseRepository);
    }

    // Case 2: franchiseId null -> return null
    @Test
    void getPointsBalance_franchiseIdNull_shouldReturnNull() {
        PointsBalanceResponse res =
                pointsBalanceService.getPointsBalance(customerId, null);

        assertNull(res);
        verifyNoInteractions(customerFranchiseRepository);
    }

    // Case 3: customerFranchise không tồn tại -> return null
    @Test
    void getPointsBalance_notFound_shouldReturnNull() {
        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        PointsBalanceResponse res =
                pointsBalanceService.getPointsBalance(customerId, franchiseId);

        assertNull(res);
        verify(customerFranchiseRepository)
                .findByCustomerIdAndFranchiseId(customerId, franchiseId);
        verifyNoInteractions(pointsBalanceMapper);
    }

    // Case 4: tìm thấy customerFranchise -> map sang DTO
    @Test
    void getPointsBalance_found_shouldReturnDTO() {
        CustomerFranchise customerFranchise = new CustomerFranchise();
        PointsBalanceResponse expectedResponse = new PointsBalanceResponse();

        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(customerFranchise));

        when(pointsBalanceMapper.toDTO(customerFranchise))
                .thenReturn(expectedResponse);

        PointsBalanceResponse res =
                pointsBalanceService.getPointsBalance(customerId, franchiseId);

        assertNotNull(res);
        assertEquals(expectedResponse, res);

        verify(customerFranchiseRepository)
                .findByCustomerIdAndFranchiseId(customerId, franchiseId);
        verify(pointsBalanceMapper).toDTO(customerFranchise);
    }
}
