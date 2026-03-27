package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.service.member.MemberService;
import com.icia.delivery.service.member.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member") // 공통 경로 설정
public class RestfulController {

    private final MemberService msvc;
    private final OrderService osvc;

    /**
     * 아이디 중복 확인 엔드포인트
     * GET /api/member/check-id?userId=desiredUserId
     *
     * @param userId 확인할 아이디
     * @return 아이디 존재 여부를 담은 JSON
     */
    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Object>> checkUserId(@RequestParam String userId) {
        boolean exists = msvc.isUserIdExists(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인한 사용자의 이름을 반환하는 엔드포인트
     * POST /api/member/userName
     *
     * @param session 현재 세션
     * @return 사용자 이름 또는 "로그인 필요"
     */
    @PostMapping("/userName")
    public ResponseEntity<String> getUserName(HttpSession session) {
        // 세션에서 mem_id를 Long 타입으로 가져오기
        Long memId = (Long) session.getAttribute("mem_id");  // Long 타입으로 캐스팅
        System.out.println("memID : " + memId);

        if (memId != null) {
            // mem_id를 통해 사용자 이름을 조회
            String userName = msvc.getUserNameById(memId);  // Long 타입을 전달
            if (userName != null) {
                return ResponseEntity.ok(userName);  // 사용자 이름 반환
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } else {
            // mem_id가 없으면, 로그인되지 않은 상태로 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
    }

    /**
     * 회원 정보를 업데이트하는 엔드포인트
     * POST /api/member/update-modal
     *
     * @param payload 요청 본문에 포함된 필드 및 값
     * @param session 현재 세션
     * @return 업데이트 성공 여부 및 메시지를 담은 JSON
     */
    @PostMapping("/update-modal")
    public ResponseEntity<Map<String, Object>> updateMemberModal(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String field = payload.get("field");
            String value = payload.get("value");
            // 세션에서 mem_id를 가져오기
            Long mId = (Long) session.getAttribute("mem_id");

            if (field == null || value == null || mId == null) {
                response.put("success", false);
                response.put("message", "필수 파라미터가 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // MemberDTO 생성 및 설정
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMId(mId); // mId 설정

            // field에 따라 적절한 setter 호출
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
                    // Parse the birthday string to LocalDate
                    LocalDate birthDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    memberDTO.setBirthday(birthDate);
                    break;
                case "gender":
                    memberDTO.setGender(value);
                    break;
                default:
                    response.put("success", false);
                    response.put("message", "유효하지 않은 필드입니다.");
                    return ResponseEntity.badRequest().body(response);
            }

            // 서비스 메서드 호출하여 회원 정보 업데이트
            msvc.updateMemberModal(mId, memberDTO);

            response.put("success", true);
            response.put("message", "회원정보가 수정되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 회원 탈퇴를 처리하는 엔드포인트
     * POST /api/member/delete
     *
     * @param session 현재 세션
     * @return 탈퇴 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/delete")
    public ResponseEntity<String> delete(HttpSession session) {
        // 세션에서 mem_id를 가져오기
        Long mId = (Long) session.getAttribute("mem_id");

        if (mId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // MemberDTO 생성 및 mId 설정
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMId(mId);

            String result = msvc.delete(memberDTO);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * 회원의 생년월일을 업데이트하는 엔드포인트
     * POST /api/member/updateBirthday
     *
     * @param birthDate 업데이트할 생년월일 (LocalDate 형식: yyyy-MM-dd)
     * @param session   현재 세션
     * @return 업데이트 성공 여부 및 메시지를 담은 JSON
     */
    @PostMapping("/updateBirthday")
    public ResponseEntity<Map<String, Object>> updateBirthday(
            @RequestParam("birthday") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long mId = (Long) session.getAttribute("mem_id");

            if (mId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMId(mId);
            memberDTO.setBirthday(birthDate);

            // 서비스 메서드 호출하여 생년월일 업데이트
            msvc.updateMemberModal(mId, memberDTO);

            response.put("success", true);
            response.put("message", "생년월일이 수정되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @PostMapping("/address/{orderId}")
    public ResponseEntity<Map<String, String>> getAddress(@PathVariable("orderId") Long orderId) {

        System.out.println("🔍 요청된 주문 ID: " + orderId);

        // ✅ 주문 ID로 해당 주문의 `memId` 조회
        Long memId = osvc.getMemIdByOrderId(orderId);
        System.out.println("해당 주문의 사용자 ID: " + memId);

        // ✅ `memId`로 해당 사용자의 주소 조회
        String address = msvc.getAddressByMemId(memId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("address", String.valueOf(address));

        return ResponseEntity.ok(response);
    }
}

