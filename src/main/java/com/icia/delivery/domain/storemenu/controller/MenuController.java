package com.icia.delivery.domain.storemenu.controller;


import com.icia.delivery.domain.storemenu.service.MenuService;
import com.icia.delivery.domain.storemenu.dto.PreStoreMenuDTO;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    private final MenuService menuService;
    private final HttpSession session;
    /**
     * 특정 가게의 메뉴 리스트 조회
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<ApiResponse<List<PreStoreMenuDTO>>> getMenuByStoreId(@PathVariable Long storeId) {
        log.debug("Store menu list requested. storeId={}", storeId);
        List<PreStoreMenuDTO> menuList = menuService.getMenuByStoreId(storeId);
        return ResponseEntity.ok(ApiResponse.success(menuList));
    }
    @PostMapping("menu/{menuId}")
    public ResponseEntity<ApiResponse<List<PreStoreMenuDTO>>> getMenuBymenuId(@PathVariable("menuId") Long menuId) {
        List<PreStoreMenuDTO> menuList = menuService.getMenuBymenuId(menuId);
        session.setAttribute("menuId" , menuId);
        return ResponseEntity.ok(ApiResponse.success(menuList));
    }
}
