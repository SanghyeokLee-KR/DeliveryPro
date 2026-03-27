package com.icia.delivery.dto.admin;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {

    private Long id;

    @NotBlank(message = "쿠폰 코드는 필수 입력 항목입니다.")
    @Size(max = 255, message = "쿠폰 코드는 최대 255자까지 입력 가능합니다.")
    private String code;

    @NotBlank(message = "쿠폰 이름은 필수 입력 항목입니다.")
    @Size(max = 255, message = "쿠폰 이름은 최대 255자까지 입력 가능합니다.")
    private String name;

    @NotBlank(message = "쿠폰 내용은 필수 입력 항목입니다.")
    @Size(max = 255, message = "쿠폰 내용은 최대 255자까지 입력 가능합니다.")
    private String content;

    @NotNull(message = "할인 금액은 필수 입력 항목입니다.")
    @Min(value = 0, message = "할인 금액은 0 이상이어야 합니다.")
    private Long deductPrice;

    @NotNull(message = "최소 구매 금액은 필수 입력 항목입니다.")
    @Min(value = 0, message = "최소 구매 금액은 0 이상이어야 합니다.")
    private Long minPrice;

    @NotBlank(message = "주문 유형은 필수 입력 항목입니다.")
    @Size(max = 255, message = "주문 유형은 최대 255자까지 입력 가능합니다.")
    private String orderType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "만료일은 필수 입력 항목입니다.")
    private LocalDate expiredDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    @NotBlank(message = "쿠폰 상태는 필수 입력 항목입니다.")
    @Pattern(regexp = "Y|N|E", message = "쿠폰 상태는 'Y', 'N', 또는 'E'이어야 합니다.")
    private String status;

    public static CouponDTO toDTO(CouponEntity entity){
        CouponDTO dto = new CouponDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setDeductPrice(entity.getDeductPrice());
        dto.setMinPrice(entity.getMinPrice());
        dto.setOrderType(entity.getOrderType());
         return dto;
    }

}
