package com.icia.delivery.domain.storemenu.service;

import com.icia.delivery.domain.storemenu.dto.PreStoreMenuDTO;
import com.icia.delivery.domain.storemenu.entity.PreStoreMenuEntity;
import com.icia.delivery.domain.storemenu.repository.StoreMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final StoreMenuRepository ssmrepo;

    public List<PreStoreMenuDTO> getMenuByStoreId(Long storeId) {

        // 1) storeId로 MenuEntity 리스트 조회
        List<PreStoreMenuEntity> menuEntities = ssmrepo.findBypreStoId(storeId);
        System.out.println("확인차 엔티티: " + menuEntities);

        // 2) Entity → DTO 변환 후 List로 반환
        return menuEntities.stream()
                .map(PreStoreMenuDTO::toDTO) // 변환 메서드 (예: MenuDTO.toDTO(MenuEntity))
                .collect(Collectors.toList());
    }

    public List<PreStoreMenuDTO> getMenuBymenuId(Long menuId) {

        // 1) storeId로 MenuEntity 리스트 조회
        List<PreStoreMenuEntity> menuEntities = ssmrepo.findBymenuId(menuId);

        // 2) Entity → DTO 변환 후 List로 반환
        return menuEntities.stream()
                .map(PreStoreMenuDTO::toDTO) // 변환 메서드 (예: MenuDTO.toDTO(MenuEntity))
                .collect(Collectors.toList());
    }
}
