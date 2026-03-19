package service.CSFC.CSFC_auth_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.constants.RedemptionStatus;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.QRCheckResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;
import service.CSFC.CSFC_auth_service.service.RedemptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/engagement-service/redemption")
@RequiredArgsConstructor
public class RedemptionController {

    private final RedemptionService redemptionService;

    @PostMapping("/confirm/{rewardId}")
    public ResponseEntity<ApiResponse<RedemptionResponse>> confirmRedeem(
            @PathVariable Long rewardId
    ) throws Exception {

        RedemptionResponse response =
                redemptionService.confirmRedeem(rewardId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Redeem confirmed successfully")
        );
    }

    @GetMapping("/confirm/{code}")
    public ResponseEntity<?> checkQRCode(@PathVariable String code) {

        Optional<Redemption> redemptionOpt =
                redemptionService.findByRedemptionCode(code);

        // 1 QR không tồn tại
        if (redemptionOpt.isEmpty()) {
            return ResponseEntity.ok(
                    new QRCheckResponse(false,
                            "QR không tồn tại",
                            null,
                            null,
                            null)
            );
        }

        Redemption redemption = redemptionOpt.get();

        // 2 QR đã được sử dụng
        if (redemption.getStatus() == RedemptionStatus.COMPLETED) {
            return ResponseEntity.ok(
                    new QRCheckResponse(false,
                            "QR đã được sử dụng",
                            redemption.getRedemptionCode(),
                            redemption.getPointsUsed(),
                            redemption.getReward().getName())
            );
        }

        // 3 QR bị huỷ
        if (redemption.getStatus() == RedemptionStatus.CANCELLED) {
            return ResponseEntity.ok(
                    new QRCheckResponse(false,
                            "QR đã bị huỷ",
                            redemption.getRedemptionCode(),
                            redemption.getPointsUsed(),
                            redemption.getReward().getName())
            );
        }

        //Update QR thành completed
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.now());

        redemptionService.save(redemption);

        // 4 QR hợp lệ
        return ResponseEntity.ok(
                new QRCheckResponse(true,
                        "Redemption thành công",
                        redemption.getRedemptionCode(),
                        redemption.getPointsUsed(),
                        redemption.getReward().getName())
        );
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<RedemptionResponse>>> getAllRedemptions() {

        List<RedemptionResponse> data = redemptionService.getAll();
        return ResponseEntity.ok(ApiResponse.success(
                data,
                "Lấy danh sách redemption thành công"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RedemptionResponse>> getRedemptionById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(redemptionService.findById(id),"Lây redemption thành công")
        );

    }
}

