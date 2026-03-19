package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;

@Entity
@Table(name = "reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reward extends BaseEntity {

    @Column(name = "franchise_id", nullable = false)
    private Long franchiseId; // Phần thưởng thuộc về chuỗi nào

    @Column(nullable = false)
    private String name; // Tên phần thưởng

    @Column(name = "required_points", nullable = false)
    private Integer requiredPoints; // Số điểm cần để đổi

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết

    @Column(name = "is_active")
    private Boolean isActive = true; // Còn áp dụng hay không

    @Column(name = "image_url")
    private String imageUrl;
}