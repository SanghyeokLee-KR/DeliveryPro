package com.icia.delivery.domain.member.controller;

import com.icia.delivery.domain.member.dto.MemberDTO;
import com.icia.delivery.domain.member.service.MemberService;
import com.icia.delivery.domain.order.service.OrderService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class RestfulController {

    private final MemberService msvc;
    private final OrderService osvc;

    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserId(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("exists", msvc.isUserIdExists(userId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/userName")
    public ResponseEntity<ApiResponse<String>> getUserName(HttpSession session) {
        Long memId = (Long) session.getAttribute("mem_id");
        if (memId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Login is required.");
        }

        String userName = msvc.getUserNameById(memId);
        if (userName == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Member not found.");
        }

        return ResponseEntity.ok(ApiResponse.success(userName));
    }

    @PostMapping("/update-modal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateMemberModal(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        Long mId = (Long) session.getAttribute("mem_id");
        String field = payload != null ? payload.get("field") : null;
        String value = payload != null ? payload.get("value") : null;

        if (field == null || value == null || mId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Required parameter is missing.");
        }

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMId(mId);

        try {
            applyMemberField(memberDTO, field, value);
            msvc.updateMemberModal(mId, memberDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원정보가 수정되었습니다.");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BusinessException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update member.", e);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Map<String, String>>> delete(HttpSession session) {
        Long mId = (Long) session.getAttribute("mem_id");
        if (mId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Login is required.");
        }

        try {
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMId(mId);

            Map<String, String> response = new HashMap<>();
            response.put("message", msvc.delete(memberDTO));
            response.put("redirectUrl", "/index");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PostMapping("/updateBirthday")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateBirthday(
            @RequestParam("birthday") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
            HttpSession session) {

        Long mId = (Long) session.getAttribute("mem_id");
        if (mId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Login is required.");
        }

        try {
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMId(mId);
            memberDTO.setBirthday(birthDate);
            msvc.updateMemberModal(mId, memberDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "생년월일이 수정되었습니다.");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update birthday.", e);
        }
    }

    @PostMapping("/address/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAddress(@PathVariable("orderId") Long orderId) {
        Long memId = osvc.getMemIdByOrderId(orderId);
        String address = msvc.getAddressByMemId(memId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("address", String.valueOf(address));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private void applyMemberField(MemberDTO memberDTO, String field, String value) {
        switch (field) {
            case "nickname":
                memberDTO.setNickname(value);
                break;
            case "phone":
                memberDTO.setPhone(value);
                break;
            case "email":
                memberDTO.setEmail(value);
                break;
            case "birthday":
                try {
                    memberDTO.setBirthday(LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } catch (DateTimeParseException e) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid birthday format.", e);
                }
                break;
            case "gender":
                memberDTO.setGender(value);
                break;
            default:
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid field.");
        }
    }
}
