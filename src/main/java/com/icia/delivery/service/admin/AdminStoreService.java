// src/main/java/com/icia/delivery/service/admin/AdminStoreService.java
package com.icia.delivery.service.admin;

import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.dto.president.PreStoreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminStoreService {

    @Autowired
    private StoreRepository storeRepository;

    /**
     * 모든 가게를 페이징하여 조회하는 메서드
     *
     * @param pageable 페이징 정보
     * @return 페이징된 가게 리스트 DTO
     */
    /* 현재는 DB에서 받아오는 값이 '승인'이 아닌 데이터들만 가져옴 */
    public Page<PreStoreDTO> getAllStores(Pageable pageable) {
        Page<PreStoreEntity> storeEntities = storeRepository.findStatusNone(pageable);
        return storeEntities.map(PreStoreDTO::toDTO);
    }

    /* 모든 가게 리스트를 가져오기 위한 메소드 */
    public Page<PreStoreDTO> getAllStoresList(Pageable pageable) {
        Page<PreStoreEntity> storeEntities = storeRepository.findApprovedStores(pageable);
        return storeEntities.map(PreStoreDTO::toDTO);
    }

    /**
     * 검색어와 필터링 조건을 기반으로 가게를 조회하는 메서드
     *
     * @param searchQuery 가게 이름 또는 카테고리 검색어
     * @param category    음식 카테고리 필터
     * @param status      가게 상태 필터
     * @param pageable    페이징 및 정렬 정보
     * @return 페이징된 필터링된 가게 리스트 DTO
     */
    public Page<PreStoreDTO> searchStores(String searchQuery, String category, String status, Pageable pageable) {
        Page<PreStoreEntity> storeEntities = storeRepository.findStores(searchQuery, category, status, pageable);
        return storeEntities.map(PreStoreDTO::toDTO);
    }

    /**
     * 특정 ID를 가진 가게를 조회하는 메서드
     *
     * @param id 가게 ID
     * @return 가게 DTO 또는 null
     */
    public PreStoreDTO getStoreById(Long id) {
        return storeRepository.findBypreStoIdOptional(id)
                .map(PreStoreDTO::toDTO)
                .orElse(null);
    }

    /**
     * 가게의 정보를 업데이트하는 메서드
     *
     * @param id        가게 ID
     * @param storeForm 수정된 가게 정보가 담긴 PreStoreDTO
     * @return 업데이트 성공 여부
     */
    public boolean updateStoreInfo(Long id, PreStoreDTO storeForm) {
        return storeRepository.findBypreStoIdOptional(id).map(store -> {
            // 필수 필드 체크 (예: preStoName이 null이면 예외 처리)
            if (storeForm.getPreStoName() == null || storeForm.getPreStoName().isEmpty()) {
                throw new IllegalArgumentException("가게 이름은 필수입니다.");
            }

            // 수정 가능한 필드 업데이트 (값이 null인 경우 기본값 처리 또는 기존 값 유지)
            store.setPreStoName(storeForm.getPreStoName() != null ? storeForm.getPreStoName() : store.getPreStoName());
            store.setPreStoCategory(storeForm.getPreStoCategory() != null ? storeForm.getPreStoCategory() : store.getPreStoCategory());
            store.setPreStoAddress(storeForm.getPreStoAddress() != null ? storeForm.getPreStoAddress() : store.getPreStoAddress());
            store.setPreStoPhone(storeForm.getPreStoPhone() != null ? storeForm.getPreStoPhone() : store.getPreStoPhone());
            store.setPreStoIntro(storeForm.getPreStoIntro() != null ? storeForm.getPreStoIntro() : store.getPreStoIntro());
            store.setPreStoMinOrderAmount(storeForm.getPreStoMinOrderAmount() != null ? storeForm.getPreStoMinOrderAmount() : store.getPreStoMinOrderAmount());
            store.setPreStoDeliveryFee(storeForm.getPreStoDeliveryFee() != null ? storeForm.getPreStoDeliveryFee() : store.getPreStoDeliveryFee());
            store.setPreStoDeliveryTimeMin(storeForm.getPreStoDeliveryTimeMin() != null ? storeForm.getPreStoDeliveryTimeMin() : store.getPreStoDeliveryTimeMin());
            store.setPreStoDeliveryTimeMax(storeForm.getPreStoDeliveryTimeMax() != null ? storeForm.getPreStoDeliveryTimeMax() : store.getPreStoDeliveryTimeMax());
            store.setPreStoRating(storeForm.getPreStoRating() != null ? storeForm.getPreStoRating() : store.getPreStoRating());
            store.setPreStoReviewCount(storeForm.getPreStoReviewCount() != null ? storeForm.getPreStoReviewCount() : store.getPreStoReviewCount());
            store.setPreStoStatus(storeForm.getPreStoStatus() != null ? storeForm.getPreStoStatus() : store.getPreStoStatus());
            store.setPreStoOpeningHours(storeForm.getPreStoOpeningHours() != null ? storeForm.getPreStoOpeningHours() : store.getPreStoOpeningHours());
            store.setPreStoDayOff(storeForm.getPreStoDayOff() != null ? storeForm.getPreStoDayOff() : store.getPreStoDayOff());
            store.setPreStoDeliveryArea(storeForm.getPreStoDeliveryArea() != null ? storeForm.getPreStoDeliveryArea() : store.getPreStoDeliveryArea());
            store.setPreStoOperatingDays(storeForm.getPreStoOperatingDays() != null ? storeForm.getPreStoOperatingDays() : store.getPreStoOperatingDays());
            store.setPreStoHolidayWeek(storeForm.getPreStoHolidayWeek() != null ? storeForm.getPreStoHolidayWeek() : store.getPreStoHolidayWeek());

            // 필요에 따라 추가적인 필드 업데이트
            storeRepository.save(store);
            return true;
        }).orElse(false);
    }




    // 필요 시 추가적인 메서드들 (예: 가게 삭제, 가게 생성 등)
}
