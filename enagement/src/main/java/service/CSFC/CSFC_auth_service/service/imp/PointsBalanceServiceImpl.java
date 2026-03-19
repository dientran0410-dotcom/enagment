package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.mapper.PointsBalanceMapper;
import service.CSFC.CSFC_auth_service.model.dto.response.PointsBalanceResponse;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.repository.CustomerFranchiseRepository;
import service.CSFC.CSFC_auth_service.service.PointsBalanceService;

@Service
@RequiredArgsConstructor
public class PointsBalanceServiceImpl implements PointsBalanceService {

    private final CustomerFranchiseRepository customerFranchiseRepository;

    private final PointsBalanceMapper pointsBalanceMapper;

    @Override
    public PointsBalanceResponse getPointsBalance(Long customerId, Long franchiseId) {
        if (customerId == null || franchiseId == null) {
            return null;
        }

        CustomerFranchise customerFranchise = customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId)
                .orElse(null);

        if (customerFranchise == null) {
            return null;
        }

        return pointsBalanceMapper.toDTO(customerFranchise);
    }

}
