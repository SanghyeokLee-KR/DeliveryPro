package com.icia.delivery.controller.president;

import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.dto.president.PreStoreMenuDTO;
import com.icia.delivery.service.president.StoreService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store") // 공통 경로 설정
public class PreRustfulController {

    private final StoreService ssvc;

    @PostMapping("/storeListBox")
    public List<PreStoreDTO> storeListBox(@RequestParam("pathValue") Long pathValue) {
        // pathValue를 사용하여 service에서 데이터를 가져옵니다.
        return ssvc.storeList(pathValue);
    }

    @PostMapping("/getStoreDetails")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStoreDetails(@RequestParam("storeId") Long storeId) {
        Map<String, Object> response = ssvc.getStoreDetails(storeId);
        return ResponseEntity.ok(response);
    }


    // getMenuList
    @PostMapping("/getMenuList")
    public List<PreStoreMenuDTO> getMenuList(@RequestParam("preStoId") Long preStoId) {
        // System.out.println("메뉴 리스트 매장 PK : " + preStoId);
        return ssvc.getStoreMenuList(preStoId);
    }

    // getSellStatusValue
    @PostMapping("/updateMenuStatus")
    public String getSellStatusValue(@RequestParam("menuId") Long menuId, @RequestParam("newStatus") String newStatus) {
        // System.out.println("메뉴 아이디 : " + menuId);
        // System.out.println("메뉴 status 값 : " + newStatus);
        return ssvc.updateMenuStatus(menuId, newStatus);
    }

    // menuModify
    @PostMapping("/menuModify")
    public String menuModify(@ModelAttribute PreStoreMenuDTO menuDTO) {
        System.out.println("메뉴 수정 데이터 : " + menuDTO);
        return ssvc.menuModify(menuDTO);
    }

    // menuDelete
    @PostMapping("/menuDelete")
    public String menuDelete(@RequestParam("menuId") Long menuId) {
        // System.out.println("삭제할 메뉴 아이디 : " + menuId);
        return ssvc.menuDelete(menuId);
    }

    // storeCount
    @PostMapping("/storeCount")
    public int storeCount(@RequestParam("pathValue") Long pathValue) {
        return ssvc.storeCount(pathValue);
    }

    // searchMenuList
    @PostMapping("/searchMenuList")
    public List<PreStoreMenuDTO> searchMenuList(@RequestParam("keyword") String keyword,
                                                @RequestParam("category") String category,
                                                @RequestParam("preStoId") Long preStoId) {

        List<PreStoreMenuDTO> storeMenuDTO = new ArrayList<>();

        // 검색 및 필터링 조건에 따라 데이터 조회
        // ~ 에 검색 데이터가 없지 않거나 그리고 검색 데이터가 공백이 아니라면
        if ((keyword != null && !keyword.trim().isEmpty()) ||
                (category != null && !category.trim().isEmpty())) {
            assert keyword != null;
            storeMenuDTO = ssvc.searchMenuList(keyword, category, preStoId);
        } else {
            System.out.println("오류가 검출 되었습니다.");
        }

        // return storeMenuDTO;
        return storeMenuDTO;
    }

    @PostMapping("/updateStoreDetails")
    public ResponseEntity<Map<String, Object>> updateStoreDetails(@RequestBody Map<String, String> payload,
                                                                  HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String field = payload.get("field");
            String value = payload.get("value");

            Long preStoreId = (Long) session.getAttribute("pre_store_id");

            if (field == null || value == null || preStoreId == null) {
                response.put("success", false);
                response.put("message", "필수 파라미터가 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            PreStoreDTO storeDTO = new PreStoreDTO();
            storeDTO.setPreStoId(preStoreId);

            // field에 따라 적절한 setter 호출
            switch (field) {
                case "storeName":
                    storeDTO.setPreStoName(value);
                    break;
                case "category":
                    storeDTO.setPreStoCategory(value);
                    break;
                case "address":
                    storeDTO.setPreStoAddress(value);
                    break;
                case "phone":
                    storeDTO.setPreStoPhone(value);
                    break;
                case "intro":
                    storeDTO.setPreStoIntro(value);
                    break;
                case "minOrder":
                    try {
                        // minOrder는 Integer 타입이므로 값을 숫자로 변환
                        int minOrderValue = Integer.parseInt(value);
                        storeDTO.setPreStoMinOrderAmount(minOrderValue);
                    } catch (NumberFormatException e) {
                        // value가 숫자가 아닐 경우 예외 처리
                        response.put("success", false);
                        response.put("message", "유효한 최소 주문 금액을 입력해주세요.");
                        return ResponseEntity.badRequest().body(response);
                    }
                    break;
                case "deliFee":
                    try {
                        // deliFee도 Integer 타입으로 변환
                        int deliFeeValue = Integer.parseInt(value);
                        storeDTO.setPreStoDeliveryFee(deliFeeValue);
                    } catch (NumberFormatException e) {
                        // value가 숫자가 아닐 경우 예외 처리
                        response.put("success", false);
                        response.put("message", "유효한 배달 요금을 입력해주세요.");
                        return ResponseEntity.badRequest().body(response);
                    }
                    break;
                default:
                    response.put("success", false);
                    response.put("message", "유효하지 않은 필드입니다.");
                    return ResponseEntity.badRequest().body(response);
            }

            // 서비스 메서드 호출하여 회원 정보 업데이트
            ssvc.updateStoreDetails(preStoreId, storeDTO);


            response.put("success", true);
            response.put("message", "매장 정보가 수정되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/storeSalesData")
    public Map<String, Double>  storeSalesData(){
        return ssvc.storeSalesData();
    }


    @PostMapping("/storeMemBirthSalesData")
    public ResponseEntity<Map<String, Object>> storeMemBirthSalesData() {
        Map<String, Object> response = ssvc.storeMemBirthSalesData();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/storeMenuRank")
    public ResponseEntity<Map<String, Object>> storeMenuRank(){
        Map<String, Object> response = ssvc.storeMenuRank();
        return ResponseEntity.ok(response);
    }

}
