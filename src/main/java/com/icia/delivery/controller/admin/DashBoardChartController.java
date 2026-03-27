package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.service.admin.DashboardService;
import com.icia.delivery.service.admin.MarketingService;
import com.icia.delivery.service.admin.SalesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashBoardChartController {

    private final DashboardService dashboardService;
    private final SalesService salesService;
    private final MarketingService marketingService;

    private final HttpSession session;

    // dashboardChart
    @PostMapping("/dashboardChart")
    public Map<String, Long> dashboardChart() {
        // Service에서 카테고리별 주문 수를 집계한 Map을 받아옴
        Map<String, Long> categoryCountMap = dashboardService.dashboardChart();
        return categoryCountMap;  // Map을 그대로 반환
    }

    // salesChart
    @PostMapping("/salesChart")
    public Map<String, Long> salesChart() {
        // Service에서 지난 7일 동안의 매출 금액을 집계한 Map을 받아옴
        Map<String, Long> salesData = salesService.getSalesForLastWeek();
        return salesData;  // Map을 JSON 형식으로 반환
    }

    // topSellingStore
    @PostMapping("/topSellingStore")
    public Map<Double, PreStoreDTO> topSellingStore() {
        // Service에서 매장별 매출 집계를 Map 형식으로 받아옴
        Map<Double, PreStoreDTO> topStore = salesService.topSellingStore();
        return topStore;
    }

    @PostMapping("/statisticsDateSel")
    public String statisticsDateSel(@RequestParam("date") String date){
        System.out.println("date : " + date);
        return null;
    }

    // genderRatio_1
    @PostMapping("/genderRatio")
    public Map<String, Double> genderRatio(HttpSession session){
        // 세션에서 개별 값 가져오기
        Double maleRatio = (Double) session.getAttribute("maleRatio");
        Double femaleRatio = (Double) session.getAttribute("femaleRatio");
        Double orderMaleRatio = (Double) session.getAttribute("orderMaleRatio");
        Double orderFemaleRatio = (Double) session.getAttribute("orderFemaleRatio");
        Double localLoginRatio = (Double) session.getAttribute("localLoginRatio");
        Double naverLoginRatio = (Double) session.getAttribute("naverLoginRatio");
        Double kakaoLoginRatio = (Double) session.getAttribute("kakaoLoginRatio");
        Double googleLoginRatio = (Double) session.getAttribute("googleLoginRatio");

        // 반환할 Map에 담기
        Map<String, Double> genderRatio = new HashMap<>();
        genderRatio.put("maleRatio", maleRatio);
        genderRatio.put("femaleRatio", femaleRatio);
        genderRatio.put("orderMaleRatio", orderMaleRatio);
        genderRatio.put("orderFemaleRatio", orderFemaleRatio);
        genderRatio.put("localLogin", localLoginRatio);
        genderRatio.put("naverLogin", naverLoginRatio);
        genderRatio.put("kakaoLogin", kakaoLoginRatio);
        genderRatio.put("googleLogin", googleLoginRatio);

        return genderRatio;
    }

    @PostMapping("/birthCategoryRatio")
    public Map<String, Map<String, Integer>> birthCategoryRatio(){
        // Service에서 매장별 매출 집계를 Map 형식으로 받아옴
        Map<String, Map<String, Integer>> sss = marketingService.birthCategoryRatio();
        return sss;
    }

    @PostMapping("/topOrderMemList")
    public List<Map<String, Object>> topOrderMemList() {
        List<Map<String, Object>> topOM = marketingService.topOrderMemList();
        return topOM;
    }

    @PostMapping("/rewardGradeData")
    public Map<String, Long> rewardGradeData() {
        Map<String, Long> reward = marketingService.rewardGradeData();
        return reward;
    }



}
