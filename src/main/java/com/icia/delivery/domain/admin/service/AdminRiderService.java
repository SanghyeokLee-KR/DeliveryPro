package com.icia.delivery.domain.admin.service;

import com.icia.delivery.domain.rider.dto.RiderDTO;
import com.icia.delivery.domain.rider.entity.RiderEntity;
import com.icia.delivery.domain.rider.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminRiderService {

    @Autowired
    private final RiderRepository repository;

    public Page<RiderDTO> searchRiders(String searchQuery, String status, Pageable pageable) {
        Page<RiderEntity> riderEntities = repository.findStores(searchQuery, status, pageable);
        return riderEntities.map(RiderDTO::toDTO);
    }

    public Page<RiderDTO> getAllRidersList1(Pageable pageable) {
        Page<RiderEntity> riderEntities = repository.findApprovedRider1(pageable);
        return riderEntities.map(RiderDTO::toDTO);
    }

    public Page<RiderDTO> getAllRidersList2(Pageable pageable) {
        Page<RiderEntity> riderEntities = repository.findApprovedRider2(pageable);
        return riderEntities.map(RiderDTO::toDTO);
    }

    public RiderDTO getRiderById(Long id) {
        return repository.findByRiderNoOptional(id)
                .map(RiderDTO::toDTO)
                .orElse(null);
    }

    @Transactional
    public boolean updateRiderInfo(Long riderNo, RiderDTO riderForm) {
        return repository.findByRiderNoOptional(riderNo).map(rider -> {
            // 필수 필드 체크 (예: riderName이 null이면 예외 처리)
            if (riderForm.getRiderName() == null || riderForm.getRiderName().isEmpty()) {
                throw new IllegalArgumentException("라이더 이름은 필수입니다.");
            }

            // 수정 가능한 필드 업데이트 (값이 null인 경우 기본값 처리 또는 기존 값 유지)
            rider.setRiderName(riderForm.getRiderName() != null ? riderForm.getRiderName() : rider.getRiderName());
            rider.setRiderPhone(riderForm.getRiderPhone() != null ? riderForm.getRiderPhone() : rider.getRiderPhone());
            rider.setVehicleType(riderForm.getVehicleType() != null ? riderForm.getVehicleType() : rider.getVehicleType());
            rider.setRiderGender(riderForm.getRiderGender() != null ? riderForm.getRiderGender() : rider.getRiderGender());
            rider.setRiderBirth(riderForm.getRiderBirth() != null ? riderForm.getRiderBirth() : rider.getRiderBirth());
            rider.setTotalDeliveries(riderForm.getTotalDeliveries() != null ? riderForm.getTotalDeliveries() : rider.getTotalDeliveries());
            rider.setIsAvailable(riderForm.getIsAvailable() != null ? riderForm.getIsAvailable() : rider.getIsAvailable());

            // 필요에 따라 추가적인 필드 업데이트
            repository.save(rider);
            return true;
        }).orElse(false);
    }

}
