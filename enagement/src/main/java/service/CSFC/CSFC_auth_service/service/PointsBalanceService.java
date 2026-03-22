package service.CSFC.CSFC_auth_service.service;


import service.CSFC.CSFC_auth_service.model.dto.response.PointsBalanceResponse;

import java.util.UUID;

public interface PointsBalanceService {
    PointsBalanceResponse getPointsBalance(UUID customerId, Long franchiseId);
}
