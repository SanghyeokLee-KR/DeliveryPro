package com.icia.delivery.domain.deliveryaddress.controller;

import com.icia.delivery.domain.deliveryaddress.dto.DeliveryAddressDTO;
import com.icia.delivery.domain.deliveryaddress.service.DeliveryAddressService;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService addressService;

    /**
     * 배송지 추가
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryAddressDTO>> addAddress(@Validated @RequestBody DeliveryAddressDTO dto) {
        DeliveryAddressDTO savedAddress = addressService.addAddress(dto);
        return ResponseEntity.ok(ApiResponse.success(savedAddress));
    }

    /**
     * 배송지 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryAddressDTO>> updateAddress(
            @PathVariable Long id,
            @Validated @RequestBody DeliveryAddressDTO dto
    ) {
        DeliveryAddressDTO updatedAddress = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedAddress));
    }

    /**
     * 배송지 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 특정 배송지를 메인으로 설정
     */
    @PutMapping("/set-main/{addrId}")
    public ResponseEntity<ApiResponse<DeliveryAddressDTO>> setMainAddress(
            @PathVariable Long addrId,
            @RequestParam Long memberId
    ) {
        DeliveryAddressDTO updatedAddress = addressService.setMainAddress(memberId, addrId);
        return ResponseEntity.ok(ApiResponse.success(updatedAddress));
    }

    /**
     * 회원 번호로 모든 배송지 조회
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<DeliveryAddressDTO>>> getAddressesByMemberId(@PathVariable Long memberId) {
        List<DeliveryAddressDTO> addresses = addressService.getAddressesByMemberId(memberId);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }
}
