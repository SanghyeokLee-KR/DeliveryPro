package com.icia.delivery.domain.storemenu.service;

import com.icia.delivery.domain.storemenu.dto.PreStoreMenuDTO;
import com.icia.delivery.domain.storemenu.entity.PreStoreMenuEntity;
import com.icia.delivery.domain.storemenu.repository.StoreMenuRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final StoreMenuRepository ssmrepo;

    @Transactional(readOnly = true)
    public List<PreStoreMenuDTO> getMenuByStoreId(Long storeId) {

        // 1) storeId로 MenuEntity 리스트 조회
        List<PreStoreMenuEntity> menuEntities = ssmrepo.findBypreStoId(storeId);
        log.debug("Loaded store menus. storeId={}, count={}", storeId, menuEntities.size());

        // 2) Entity → DTO 변환 후 List로 반환
        return menuEntities.stream()
                .map(PreStoreMenuDTO::toDTO) // 변환 메서드 (예: MenuDTO.toDTO(MenuEntity))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PreStoreMenuDTO> getMenuBymenuId(Long menuId) {

        // 1) storeId로 MenuEntity 리스트 조회
        List<PreStoreMenuEntity> menuEntities = ssmrepo.findBymenuId(menuId);

        // 2) Entity → DTO 변환 후 List로 반환
        return menuEntities.stream()
                .map(PreStoreMenuDTO::toDTO) // 변환 메서드 (예: MenuDTO.toDTO(MenuEntity))
                .collect(Collectors.toList());
    }
}
