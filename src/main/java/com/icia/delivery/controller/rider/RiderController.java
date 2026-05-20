package com.icia.delivery.controller.rider;

import com.icia.delivery.dto.rider.RiderAccountDTO;
import com.icia.delivery.dto.rider.RiderDTO;
import com.icia.delivery.service.rider.RiderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class RiderController {

    private final RiderService rsvc;
    private final HttpSession session;

    // 메인 및 관리 페이지 매핑
    @GetMapping("/rider")
    public String rider() {
        return "rider/rider-main";
    }

    @GetMapping("/r_JoinForm")
    public String r_JoinForm() {
        return "rider/management/rJoin";
    }

    @GetMapping("/rLoginForm")
    public String rLoginForm() {
        return "rider/management/rLogin";
    }

    @GetMapping("/riderInfoPage/{rId}")
    public ModelAndView mView(@PathVariable("rId") Long rNo) {
        // DB에서는 라이더의 PK가 'No'로 되어 있기 때문에 Long rNo 사용
        return rsvc.rView(rNo);
    }

    @GetMapping("/riderProfile")
    public String riderProfile(){
        return "rider/management/riderInfoPage-myProfile";
    }

    @GetMapping("/riderAdjustment")
    public String riderAdjustment(){
        return "rider/management/riderInfoPage-adjustment";
    }

    @GetMapping("/riderAccount")
    public String riderAccount(){
        return "rider/management/riderInfoPage-account";
    }

    /**
     * [통합] /riderStartOfWork/{rId} 매핑
     * 라이더가 배달 업무를 시작할 때 호출됩니다.
     * 동적 HTML 내용(묶음 배달, 한집 배달, 진행중, 완료 배달 관련 컨텐츠)을 Model에 담아 전달합니다.
     */
    @GetMapping("/riderStartOfWork/{rId}")
    public String riderStartOfWork(@PathVariable("rId") Long rNo, Model model) {
        // 배달 관련 동적 HTML 컨텐츠 예시 (실제 로직에 따라 변경)
        String groupDeliveryHtml = "";
        String singleDeliveryHtml = "";
        String inProgressHtml = "";
        String completedHtml = "";

        model.addAttribute("groupDeliveryHtml", groupDeliveryHtml);
        model.addAttribute("singleDeliveryHtml", singleDeliveryHtml);
        model.addAttribute("inProgressHtml", inProgressHtml);
        model.addAttribute("completedHtml", completedHtml);

        // rider-work.html 뷰로 이동
        return "rider/delivery/rider-work";
    }

    // 라이더 회원가입 처리
    @PostMapping("/riderRegister")
    public ModelAndView registerMember(@ModelAttribute RiderDTO riderDTO) {
        System.out.println("라이더 정보 : " + riderDTO);
        return rsvc.riderRegister(riderDTO);
    }

    // 라이더 로그인 처리
    @PostMapping("/rLogin")
    public ModelAndView rLogin(@ModelAttribute RiderDTO riderDTO) {
        System.out.println("\n로그인 메소드\n[1] html → controller : " + riderDTO);
        return rsvc.rLogin(riderDTO);
    }

    // 라이더 로그아웃 처리
    @GetMapping("/rLogout")
    public String rLogout(HttpSession session) {
        if (session != null) {
            session.removeAttribute("rider_no");
            session.removeAttribute("rider_id");
            session.removeAttribute("rider_name");
        }
        return "rider/rider-main";
    }


    // 라이더 계좌 등록 처리
    @PostMapping("/addRiderAccount")
    public ModelAndView addRiderAccount(@ModelAttribute RiderAccountDTO accDTO) {
        System.out.println("계좌번호 원본 : " + accDTO.getRiderAccountNumber());

        // 계좌번호 포맷 처리
        String formattedAccountNumber = formatAccountNumber(accDTO.getRiderAccountNumber(), accDTO.getRiderBankName());
        accDTO.setRiderAccountNumber(formattedAccountNumber);
        System.out.println("수정된 계좌번호 : " + accDTO.getRiderAccountNumber());

        return rsvc.addRiderAccount(accDTO);
    }

    // 계좌번호를 은행에 맞게 포맷하는 메서드
    private String formatAccountNumber(String accountNumber, String bankName) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return accountNumber;
        }
        // '-' 기호 제거
        accountNumber = accountNumber.replace("-", "");

        if ("카카오뱅크".equals(bankName)) {
            return accountNumber.substring(0, 4) + "-" + accountNumber.substring(4, 8) + "-" + accountNumber.substring(8);
        } else if ("국민은행".equals(bankName)) {
            return accountNumber.substring(0, 6) + "-" + accountNumber.substring(6, 8) + "-" + accountNumber.substring(8);
        } else if ("하나은행".equals(bankName)) {
            return accountNumber.substring(0, 3) + "-" + accountNumber.substring(3, 8) + "-" + accountNumber.substring(8,10) + "-" + accountNumber.substring(10);
        }
        // 그 외 은행은 원본 그대로 반환
        return accountNumber;
    }
}
