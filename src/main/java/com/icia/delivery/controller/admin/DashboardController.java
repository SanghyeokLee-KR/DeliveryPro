package com.icia.delivery.controller.admin;


import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.dto.member.OrderDTO;
import com.icia.delivery.service.admin.AdminService;
import com.icia.delivery.service.admin.DashboardService;
import com.icia.delivery.service.admin.MarketingService;
import com.icia.delivery.service.admin.SalesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("dashboardController") // 고유한 빈 이름 지정
@RequestMapping("/admin/sss")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SalesService salesService;
    private final MarketingService marketingService;
    private final AdminService adminService;

    private final HttpSession session;

    // statistics
    @GetMapping("/statistics")
    public String showEditForm(Model model) {

        dashboardService.dinf(model);

        model.addAttribute("content", "statistics"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    // sales
    @GetMapping("/sales")
    public String showEditSales(Model model) {

        salesService.riso(model);

        model.addAttribute("content", "sales"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    @GetMapping("/marketing")
    public String showEditMarketing(Model model) {
        // 모든 회원 리스트와 주문 리스트 조회
        List<MemberDTO> members = marketingService.getAllMembers();
        List<OrderDTO> orders = marketingService.getAllOrders();

        // 남/녀 비율을 카운트할 변수
        int maleCount = 0;
        int femaleCount = 0;

        // 로그인 타입
        int localLogin = 0;
        int kakaoLogin = 0;
        int naverLogin = 0;
        int googleLogin = 0;

        // 로그인 총 수
        int totalLogin = 0;

        // 회원 리스트에서 성별을 확인하고 카운트
        for (MemberDTO member : members) {
            if ("남성".equals(member.getGender())) {
                maleCount++;
            } else if ("여성".equals(member.getGender())) {
                femaleCount++;
            }

            // 로그인 타입 카운트
            if ("LOCAL".equals(member.getLoginType())) {
                localLogin++;
            } else if ("네이버".equals(member.getLoginType())) {
                naverLogin++;
            } else if ("카카오".equals(member.getLoginType())) {
                kakaoLogin++;
            } else if ("구글".equals(member.getLoginType())) {
                googleLogin++;
            }
        }

        // 로그인 총 수 계산
        totalLogin = localLogin + naverLogin + kakaoLogin + googleLogin;

        // 로그인 타입 비율 계산
        double localLoginRatio = (totalLogin > 0) ? (double) localLogin / totalLogin * 100 : 0;
        double naverLoginRatio = (totalLogin > 0) ? (double) naverLogin / totalLogin * 100 : 0;
        double kakaoLoginRatio = (totalLogin > 0) ? (double) kakaoLogin / totalLogin * 100 : 0;
        double googleLoginRatio = (totalLogin > 0) ? (double) googleLogin / totalLogin * 100 : 0;

        // 성별 비율 계산 (회원 수 기준)
        int totalMembers = maleCount + femaleCount;
        double maleRatio = (totalMembers > 0) ? (double) maleCount / totalMembers * 100 : 0;
        double femaleRatio = (totalMembers > 0) ? (double) femaleCount / totalMembers * 100 : 0;

        // 주문에서 성별 카운트
        int orderMaleCount = 0;
        int orderFemaleCount = 0;

        Map<Long, String> memberGenderMap = new HashMap<>();
        for (MemberDTO member : members) {
            memberGenderMap.put(member.getMId(), member.getGender()); // memId -> gender 맵핑
        }

        for (OrderDTO order : orders) {
            Long memId = order.getMemId();
            String gender = memberGenderMap.get(memId); // 해당 memId의 성별을 가져옴
            if ("남성".equals(gender)) {
                orderMaleCount++;
            } else if ("여성".equals(gender)) {
                orderFemaleCount++;
            }
        }

        // 주문에서 성별 비율 계산
        int totalOrders = orderMaleCount + orderFemaleCount;
        double orderMaleRatio = (totalOrders > 0) ? (double) orderMaleCount / totalOrders * 100 : 0;
        double orderFemaleRatio = (totalOrders > 0) ? (double) orderFemaleCount / totalOrders * 100 : 0;

        // 세션에 저장
        session.setAttribute("maleRatio", maleRatio);
        session.setAttribute("femaleRatio", femaleRatio);
        session.setAttribute("orderMaleRatio", orderMaleRatio);
        session.setAttribute("orderFemaleRatio", orderFemaleRatio);

        session.setAttribute("localLoginRatio", localLoginRatio);
        session.setAttribute("naverLoginRatio", naverLoginRatio);
        session.setAttribute("kakaoLoginRatio", kakaoLoginRatio);
        session.setAttribute("googleLoginRatio", googleLoginRatio);

        // 모델에 데이터 추가
        model.addAttribute("maleCount", maleCount);
        model.addAttribute("femaleCount", femaleCount);

        model.addAttribute("maleRatio", maleRatio);
        model.addAttribute("femaleRatio", femaleRatio);

        model.addAttribute("localLoginRatio", localLoginRatio);
        model.addAttribute("naverLoginRatio", naverLoginRatio);
        model.addAttribute("kakaoLoginRatio", kakaoLoginRatio);
        model.addAttribute("googleLoginRatio", googleLoginRatio);

        model.addAttribute("orderMaleRatio", orderMaleRatio);
        model.addAttribute("orderFemaleRatio", orderFemaleRatio);

        model.addAttribute("content", "marketing");

        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    // 관리자 내 정보 이동
    @GetMapping("/admin/{adminid}")
    public ModelAndView adminInfo(@PathVariable("adminid") Long adminid, Model model) {

        model.addAttribute("content", "admin-info");
        return adminService.adminView(adminid);
    }


}
