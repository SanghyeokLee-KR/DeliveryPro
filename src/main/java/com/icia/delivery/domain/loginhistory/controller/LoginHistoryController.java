package com.icia.delivery.domain.loginhistory.controller;

import com.icia.delivery.domain.loginhistory.dto.LoginHistoryDTO;
import com.icia.delivery.domain.loginhistory.service.LoginHistoryService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 로그인 내역 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/login-history")
public class LoginHistoryController {

    @Autowired
    private LoginHistoryService loginHistoryService;

    /**
     * 회원번호를 기반으로 모든 로그인 내역 조회
     *
     * @param session 회원 세션에서 회원번호(mId) 가져오기
     * @return 회원의 로그인 내역 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LoginHistoryDTO>>> getAllLoginHistories(HttpSession session) {
        Long mId = (Long) session.getAttribute("mem_id"); // 세션에서 회원번호 가져오기
        if (mId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        List<LoginHistoryDTO> loginHistories = loginHistoryService.getAllLoginHistories(mId); // 회원번호를 기준으로 로그인 내역 조회
        return ResponseEntity.ok(ApiResponse.success(loginHistories)); // 조회된 로그인 내역 반환
    }


    /**
     * 새로운 로그인 내역 저장
     *
     * @param dto 저장할 로그인 내역 정보
     * @return 저장된 로그인 내역 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LoginHistoryDTO>> saveLoginHistory(@RequestBody LoginHistoryDTO dto) {
        LoginHistoryDTO savedHistory = loginHistoryService.saveLoginHistory(dto); // 새로운 로그인 내역 저장
        return ResponseEntity.ok(ApiResponse.success(savedHistory));
    }
}
