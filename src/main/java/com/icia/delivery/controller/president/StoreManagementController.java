package com.icia.delivery.controller.president;

import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.dto.president.PreStoreMenuDTO;
import com.icia.delivery.service.president.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final StoreService ssvc;

    @GetMapping("/store-management/{preId}")
    public String storeManagement() {
        return "president/management/store-management";
    }

    @PostMapping("/addStore")
    public ModelAndView addStore(@ModelAttribute PreStoreDTO preDTO) {
        // System.out.println("스토어 추가 : " + preDTO);
        return ssvc.addStore(preDTO);
    }

    @PostMapping("/addMenu")
    public ResponseEntity<Map<String, Object>> addMenu(@ModelAttribute PreStoreMenuDTO smDTO) {
        Map<String, Object> response = ssvc.addMenu(smDTO);

        if ("success".equals(response.get("status"))) {
            // 성공 시, 200 OK 응답과 함께 결과 반환
            return ResponseEntity.ok(response);
        } else {
            // 실패 시, 500 Internal Server Error 응답과 함께 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/editStoreHours")
    public ModelAndView editStoreHours(@RequestParam("preStoId") Long preStoId,
                                       @RequestParam("new-operation-days") String operationDays,
                                       @RequestParam("new-opening-hours") String openingHours){
        // System.out.println("PK : " + preStoId + ", 영업일 : " + operationDays + ", 영업시간 : " + openingHours);
        return ssvc.editStoreHours(preStoId, operationDays, openingHours);
    }

    @PostMapping("/editStoreHolidayCycle")
    public ModelAndView editStoreHolidayCycle(@RequestParam("preStoId") Long preStoId,
                                              @RequestParam("preStoHolidayWeek") String preStoHolidayWeek,
                                              @RequestParam("preStoDayOff") String preStoDayOff){
        // System.out.println("PK : " + preStoId + ", 영업일 : " + preStoHolidayWeek + ", 영업시간 : " + preStoDayOff);
        return ssvc.editStoreHolidayCycle(preStoId, preStoHolidayWeek, preStoDayOff);
    }

    @PostMapping("/storeBreakTime")
    public ModelAndView storeBreakTime(@ModelAttribute PreStoreDTO dto) {
        System.out.println("타이모 스토뿌 : "  + dto);
        return ssvc.storeBreakTime(dto);
    }

}
