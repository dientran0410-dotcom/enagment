package service.CSFC.CSFC_auth_service.service;


import service.CSFC.CSFC_auth_service.model.dto.response.PointsBalanceResponse;

public interface PointsBalanceService {
    PointsBalanceResponse getPointsBalance(Long customerId, Long franchiseId);
}
