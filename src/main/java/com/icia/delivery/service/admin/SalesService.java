package com.icia.delivery.service.admin;

import com.icia.delivery.dao.member.OrderRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dto.member.OrderEntity;
import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.dto.president.PreStoreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final OrderRepository orepo;
    private final StoreRepository srepo;

    public void riso(Model model) {

        List<OrderEntity> orderEntities = orepo.findAll();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();  // 오늘 00:00
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59, 999999);  // 오늘 23:59:59.999999

        // 이번 주의 시작일 (이번 주 월요일)
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();

        // 이번 달의 시작일 (이번 달 1일)
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();

        // 오늘 주문한 금액 합산
        int totalOrderAmount = (int) orderEntities.stream()
                .filter(order -> !order.getOrderCreatedAt().isBefore(startOfDay) && !order.getOrderCreatedAt().isAfter(endOfDay))
                .mapToDouble(OrderEntity::getOrderTotalPrice)
                .sum();

        // 이번 주 주문한 금액 합산
        int totalOrderAmountThisWeek = (int) orderEntities.stream()
                .filter(order -> !order.getOrderCreatedAt().isBefore(startOfWeek) && !order.getOrderCreatedAt().isAfter(endOfDay))
                .mapToDouble(OrderEntity::getOrderTotalPrice)
                .sum();

        // 이번 달 주문한 금액 합산
        int totalOrderAmountThisMonth = (int) orderEntities.stream()
                .filter(order -> !order.getOrderCreatedAt().isBefore(startOfMonth) && !order.getOrderCreatedAt().isAfter(endOfDay))
                .mapToDouble(OrderEntity::getOrderTotalPrice)
                .sum();

        // 모든 주문 금액 합산
        int totalOrderAmountAllTime = (int) orderEntities.stream()
                .mapToDouble(OrderEntity::getOrderTotalPrice)
                .sum();

        // statistics Map 생성 후, 원본 숫자 값 추가
        Map<String, Object> sales = new HashMap<>();
        sales.put("totalOrderAmount", totalOrderAmount);
        sales.put("totalOrderAmountThisWeek", totalOrderAmountThisWeek);
        sales.put("totalOrderAmountThisMonth", totalOrderAmountThisMonth);
        sales.put("totalOrderAmountAllTime", totalOrderAmountAllTime);

        // model에 Map 추가
        model.addAttribute("sales", sales);
    }


    public Map<String, Long> getSalesForLastWeek() {
        // 현재 날짜와 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // 지난 7일의 시작 날짜 계산 (오늘 포함)
        LocalDateTime startOfWeek = now.minusDays(6).toLocalDate().atStartOfDay();

        // 7일 간의 주문 필터링
        List<OrderEntity> orders = orepo.findAll();

        // TreeMap을 사용하여 날짜 순으로 정렬
        Map<String, Long> salesData = new TreeMap<>();

        // 일별로 매출을 합산
        for (int i = 0; i < 7; i++) {
            LocalDateTime currentDay = startOfWeek.plusDays(i); // 현재 날짜
            LocalDateTime nextDay = currentDay.plusDays(1); // 다음 날

            // 해당 날짜의 매출액 합산
            long dailySales = orders.stream()
                    .filter(order -> !order.getOrderCreatedAt().isBefore(currentDay) && order.getOrderCreatedAt().isBefore(nextDay))
                    .mapToLong(order -> (long) order.getOrderTotalPrice())  // double을 long으로 변환
                    .sum();

            // Map에 날짜와 매출액 저장 (yyyy-MM-dd 형태로 저장)
            salesData.put(currentDay.toLocalDate().toString(), dailySales);
        }

        // 매출 데이터 반환
        return salesData;
    }

    public Map<Double, PreStoreDTO> topSellingStore() {

        // 1. 주문 데이터 가져오기
        List<OrderEntity> orderEntityList = orepo.findAll();

        // 2. 매장별 총 매출 계산
        Map<Long, Double> storeSales = new HashMap<>();

        for (OrderEntity order : orderEntityList) {
            Long storeId = order.getPreStoId();  // 매장 아이디 (예: 1, 2, 3)
            Double orderAmount = order.getOrderTotalPrice();  // 매출 금액 (예: 3000, 4000)

            // 매장별 총 매출을 누적
            storeSales.put(storeId, storeSales.getOrDefault(storeId, 0.0) + orderAmount);
        }

        // 3. 매장별 매출 내림차순 정렬
        List<Map.Entry<Long, Double>> sortedStoreSales = storeSales.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // 내림차순 정렬
                .collect(Collectors.toList());

        // 4. PreStoEntity를 통해 매장 정보 가져오기 및 PreStoDTO 생성
        Map<Double, PreStoreDTO> topStores = new HashMap<>();

        for (Map.Entry<Long, Double> entry : sortedStoreSales) {
            Long storeId = entry.getKey();
            Double totalAmount = entry.getValue();

            // 매장 정보를 PreStoEntity에서 가져오기
            PreStoreEntity storeEntity = srepo.findById(storeId)
                    .orElseThrow(() -> new RuntimeException("Store not found"));

            // PreStoDTO로 매장 정보 및 총 매출을 설정
            PreStoreDTO storeDTO = new PreStoreDTO();
            storeDTO.setPreStoId(storeEntity.getPreStoId());
            storeDTO.setPreStoName(storeEntity.getPreStoName());
            storeDTO.setPreStoPhoto(storeEntity.getPreStoPhoto());
            storeDTO.setPreStoPreMemId(storeEntity.getPreStoPreMemId());
            // storeDTO.setTotalSales(totalSales); // 매출 추가

            // 매장 ID를 키로 하여 Map에 저장
            topStores.put(totalAmount, storeDTO);
        }

        // 5. 결과를 Map 형태로 반환
        return topStores;
    }

}
