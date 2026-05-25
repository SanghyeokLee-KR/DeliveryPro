package com.icia.delivery.domain.store.controller;

import com.icia.delivery.domain.store.dto.PreStoreDTO;
import com.icia.delivery.domain.storemenu.dto.PreStoreMenuDTO;
import com.icia.delivery.domain.store.service.StoreService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StoreManagementController {

    private static final Logger log = LoggerFactory.getLogger(StoreManagementController.class);

    private final StoreService ssvc;

    @GetMapping("/store-management/{preId}")
    public String storeManagement() {
        return "president/management/store-management";
    }

    @PostMapping("/addStore")
    public ModelAndView addStore(@ModelAttribute PreStoreDTO preDTO) {
        return ssvc.addStore(preDTO);
    }

    @PostMapping("/addMenu")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addMenu(@ModelAttribute PreStoreMenuDTO smDTO) {
        try {
            Map<String, Object> response = ssvc.addMenu(smDTO);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BusinessException e) {
            ErrorCode errorCode = e.getErrorCode();
            return ResponseEntity
                    .status(errorCode.getHttpStatus())
                    .body(ApiResponse.fail(errorCode, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Failed to add menu. preStoId={}", smDTO.getPreStoId(), e);
            return ResponseEntity
                    .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to add menu.", null));
        }
    }

    @PostMapping("/editStoreHours")
    public ModelAndView editStoreHours(@RequestParam("preStoId") Long preStoId,
                                       @RequestParam("new-operation-days") String operationDays,
                                       @RequestParam("new-opening-hours") String openingHours){
        return ssvc.editStoreHours(preStoId, operationDays, openingHours);
    }

    @PostMapping("/editStoreHolidayCycle")
    public ModelAndView editStoreHolidayCycle(@RequestParam("preStoId") Long preStoId,
                                              @RequestParam("preStoHolidayWeek") String preStoHolidayWeek,
                                              @RequestParam("preStoDayOff") String preStoDayOff){
        return ssvc.editStoreHolidayCycle(preStoId, preStoHolidayWeek, preStoDayOff);
    }

    @PostMapping("/storeBreakTime")
    public ModelAndView storeBreakTime(@ModelAttribute PreStoreDTO dto) {
        log.debug("Store break time update requested. preStoId={}", dto.getPreStoId());
        return ssvc.storeBreakTime(dto);
    }

}
