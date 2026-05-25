package com.icia.delivery.domain.store.controller;

import com.icia.delivery.domain.store.dto.PreStoreDTO;
import com.icia.delivery.domain.storemenu.dto.PreStoreMenuDTO;
import com.icia.delivery.domain.store.service.StoreService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store") // 공통 경로 설정
public class PreRustfulController {

    private static final Logger log = LoggerFactory.getLogger(PreRustfulController.class);

    private final StoreService ssvc;

    @PostMapping("/storeListBox")
    public ResponseEntity<ApiResponse<List<PreStoreDTO>>> storeListBox(@RequestParam("pathValue") Long pathValue) {
        // pathValue를 사용하여 service에서 데이터를 가져옵니다.
        return ResponseEntity.ok(ApiResponse.success(ssvc.storeList(pathValue)));
    }

    @PostMapping("/getStoreDetails")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStoreDetails(@RequestParam("storeId") Long storeId) {
        Map<String, Object> response = ssvc.getStoreDetails(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // getMenuList
    @PostMapping("/getMenuList")
    public ResponseEntity<ApiResponse<List<PreStoreMenuDTO>>> getMenuList(@RequestParam("preStoId") Long preStoId) {
        return ResponseEntity.ok(ApiResponse.success(ssvc.getStoreMenuList(preStoId)));
    }

    // getSellStatusValue
    @PostMapping("/updateMenuStatus")
    public ResponseEntity<ApiResponse<String>> getSellStatusValue(@RequestParam("menuId") Long menuId, @RequestParam("newStatus") String newStatus) {
        return ResponseEntity.ok(ApiResponse.success(ssvc.updateMenuStatus(menuId, newStatus)));
    }

    // menuModify
    @PostMapping("/menuModify")
    public ResponseEntity<ApiResponse<String>> menuModify(@ModelAttribute PreStoreMenuDTO menuDTO) {
        log.debug("Menu modify request. menuId={}", menuDTO.getMenuId());
        return ResponseEntity.ok(ApiResponse.success(ssvc.menuModify(menuDTO)));
    }

    // menuDelete
    @PostMapping("/menuDelete")
    public ResponseEntity<ApiResponse<String>> menuDelete(@RequestParam("menuId") Long menuId) {
        return ResponseEntity.ok(ApiResponse.success(ssvc.menuDelete(menuId)));
    }

    // storeCount
    @PostMapping("/storeCount")
    public ResponseEntity<ApiResponse<Integer>> storeCount(@RequestParam("pathValue") Long pathValue) {
        return ResponseEntity.ok(ApiResponse.success(ssvc.storeCount(pathValue)));
    }

    // searchMenuList
    @PostMapping("/searchMenuList")
    public ResponseEntity<ApiResponse<List<PreStoreMenuDTO>>> searchMenuList(@RequestParam("keyword") String keyword,
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
            log.warn("Search menu request has no keyword or category. preStoId={}", preStoId);
        }

        // return storeMenuDTO;
        return ResponseEntity.ok(ApiResponse.success(storeMenuDTO));
    }

    @PostMapping("/updateStoreDetails")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateStoreDetails(@RequestBody Map<String, String> payload,
                                                                               HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String field = payload.get("field");
            String value = payload.get("value");

            Long preStoreId = (Long) session.getAttribute("pre_store_id");

            if (field == null || value == null || preStoreId == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Required parameter is missing.");
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
                        throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid minimum order amount.", e);
                    }
                    break;
                case "deliFee":
                    try {
                        // deliFee도 Integer 타입으로 변환
                        int deliFeeValue = Integer.parseInt(value);
                        storeDTO.setPreStoDeliveryFee(deliFeeValue);
                    } catch (NumberFormatException e) {
                        // value가 숫자가 아닐 경우 예외 처리
                        throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid delivery fee.", e);
                    }
                    break;
                default:
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid field.");
            }

            // 서비스 메서드 호출하여 회원 정보 업데이트
            ssvc.updateStoreDetails(preStoreId, storeDTO);


            response.put("success", true);
            response.put("message", "매장 정보가 수정되었습니다.");
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            if (e instanceof BusinessException businessException) {
                throw businessException;
            }
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update store details.", e);
        }
    }

    @PostMapping("/storeSalesData")
    public ResponseEntity<ApiResponse<Map<String, Double>>>  storeSalesData(){
        return ResponseEntity.ok(ApiResponse.success(ssvc.storeSalesData()));
    }


    @PostMapping("/storeMemBirthSalesData")
    public ResponseEntity<ApiResponse<Map<String, Object>>> storeMemBirthSalesData() {
        Map<String, Object> response = ssvc.storeMemBirthSalesData();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/storeMenuRank")
    public ResponseEntity<ApiResponse<Map<String, Object>>> storeMenuRank(){
        Map<String, Object> response = ssvc.storeMenuRank();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
