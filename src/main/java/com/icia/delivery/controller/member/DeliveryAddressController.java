package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.DeliveryAddressDTO;
import com.icia.delivery.service.member.DeliveryAddressService;
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
    public ResponseEntity<DeliveryAddressDTO> addAddress(@Validated @RequestBody DeliveryAddressDTO dto) {
        DeliveryAddressDTO savedAddress = addressService.addAddress(dto);
        return ResponseEntity.ok(savedAddress);
    }

    /**
     * 배송지 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryAddressDTO> updateAddress(@PathVariable Long id, @Validated @RequestBody DeliveryAddressDTO dto) {
        DeliveryAddressDTO updatedAddress = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * 배송지 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 배송지를 메인으로 설정
     */
    @PutMapping("/set-main/{addrId}")
    public ResponseEntity<DeliveryAddressDTO> setMainAddress(@PathVariable Long addrId, @RequestParam Long memberId) {
        DeliveryAddressDTO updatedAddress = addressService.setMainAddress(memberId, addrId);
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * 회원 번호로 모든 배송지 조회
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<DeliveryAddressDTO>> getAddressesByMemberId(@PathVariable Long memberId) {
        List<DeliveryAddressDTO> addresses = addressService.getAddressesByMemberId(memberId);
        return ResponseEntity.ok(addresses);
    }
}
