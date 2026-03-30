package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.mapper.RewardMapper;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.repository.CustomerFranchiseRepository;
import service.CSFC.CSFC_auth_service.repository.PromotionRepository;
import service.CSFC.CSFC_auth_service.repository.RewardRepository;
import service.CSFC.CSFC_auth_service.service.FileStorageService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private RewardMapper rewardMapper;
    @Mock
    private CustomerFranchiseRepository customerFranchiseRepository;
    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private UUID customerId;
    private UUID franchiseId;
    private Reward reward;
    private RewardResponse response;

    @BeforeEach
    void setup() {
        customerId = UUID.randomUUID();
        franchiseId = UUID.randomUUID();

        reward = new Reward();
        reward.setId(1L);
        reward.setFranchiseId(franchiseId);
        reward.setName("Free Coffee");
        reward.setRequiredPoints(100);
        reward.setDescription("Test");
        reward.setIsActive(true);
        reward.setImageUrl("img-url");

        response = RewardResponse.builder()
                .id(1L)
                .franchiseId(franchiseId)
                .name("Free Coffee")
                .requiredPoints(100)
                .description("Test")
                .active(true)
                .imageUrl("img-url")
                .build();
    }

    // ================= getAllReward =================
    @Test
    void getAllReward_success() {
        when(rewardRepository.findAll()).thenReturn(List.of(reward));
        when(rewardMapper.toResponse(reward)).thenReturn(response);

        List<RewardResponse> result = rewardService.getAllReward();

        assertEquals(1, result.size());
        assertEquals("Free Coffee", result.get(0).getName());
    }

    // ================= getActiveRewards =================
    @Test
    void getActiveRewards_success() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setFranchiseId(franchiseId);

        when(customerFranchiseRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(cf));

        when(promotionRepository.findActivePromotionsByFranchiseNow(
                eq(franchiseId), any(LocalDateTime.class), any()))
                .thenReturn(List.of(new Promotion()));

        when(rewardRepository.findByFranchiseIdAndIsActiveTrue(franchiseId))
                .thenReturn(List.of(reward));

        when(rewardMapper.toResponse(reward)).thenReturn(response);

        List<RewardResponse> result = rewardService.getActiveRewards(customerId);

        assertEquals(1, result.size());
    }

    @Test
    void getActiveRewards_noPromotion_returnEmpty() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setFranchiseId(franchiseId);

        when(customerFranchiseRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(cf));

        when(promotionRepository.findActivePromotionsByFranchiseNow(
                eq(franchiseId), any(LocalDateTime.class), any()))
                .thenReturn(Collections.emptyList());

        List<RewardResponse> result = rewardService.getActiveRewards(customerId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveRewards_customerNotInFranchise_throwException() {
        when(customerFranchiseRepository.findByCustomerId(customerId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> rewardService.getActiveRewards(customerId));
    }

    // ================= createReward =================
    @Test
    void createReward_withImage_success() {
        RewardRequest request = new RewardRequest();
        request.setFranchiseId(franchiseId);
        request.setName("Free Coffee");
        request.setRequiredPoints(100);
        request.setDescription("Test");
        request.setActive(true);

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(fileStorageService.saveImage(file)).thenReturn("img-url");
        when(rewardRepository.save(any())).thenReturn(reward);
        when(rewardMapper.toResponse(reward)).thenReturn(response);

        RewardResponse result = rewardService.createReward(request, file);

        assertNotNull(result);
        verify(fileStorageService).saveImage(file);
        verify(rewardRepository).save(any());
    }

    @Test
    void createReward_withoutImage_success() {
        RewardRequest request = new RewardRequest();
        request.setFranchiseId(franchiseId);
        request.setName("Free Coffee");
        request.setRequiredPoints(100);
        request.setDescription("Test");
        request.setActive(true);

        when(rewardRepository.save(any())).thenReturn(reward);
        when(rewardMapper.toResponse(reward)).thenReturn(response);

        RewardResponse result = rewardService.createReward(request, null);

        assertNotNull(result);
        verify(fileStorageService, never()).saveImage(any());
    }

    // ================= updateReward =================
    @Test
    void updateReward_success() {
        RewardRequest request = new RewardRequest();
        request.setFranchiseId(franchiseId);
        request.setName("Updated");
        request.setRequiredPoints(200);
        request.setDescription("Updated");
        request.setActive(false);

        MultipartFile file = mock(MultipartFile.class);

        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(fileStorageService.updateImage(any(), eq(file))).thenReturn("new-img");
        when(rewardRepository.save(any())).thenReturn(reward);
        when(rewardMapper.toResponse(reward)).thenReturn(response);

        RewardResponse result = rewardService.updateReward(1L, request, file);

        assertNotNull(result);
        verify(fileStorageService).updateImage(any(), eq(file));
    }

    // ================= deleteReward =================
    @Test
    void deleteReward_success() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));

        rewardService.deleteReward(1L);

        verify(fileStorageService).deleteImage("img-url");
        verify(rewardRepository).delete(reward);
    }

    // ================= getRewardById =================
    @Test
    void getRewardById_success() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(rewardMapper.toResponse(reward)).thenReturn(response);

        RewardResponse result = rewardService.getRewardById(1L);

        assertEquals("Free Coffee", result.getName());
    }
}