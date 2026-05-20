package com.icia.delivery.service.admin;

import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dao.member.OrderRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.member.OrderDTO;
import com.icia.delivery.dto.member.OrderEntity;
import com.icia.delivery.dto.president.PreStoreDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketingService {

    private final MemberRepository mrepo;
    private final OrderRepository orepo;
    private final StoreRepository srepo;

    public List<MemberDTO> getAllMembers() {
        List<MemberEntity> memberEntities = mrepo.findAll();
        return memberEntities.stream()
                .map(MemberDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        List<OrderEntity> orderDTOList = orepo.findAll();
        return orderDTOList.stream()
                .map(OrderDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Integer>> birthCategoryRatio() {
        List<OrderDTO> orderDTOs = orepo.findAll().stream()
                .map(OrderDTO::toDTO)
                .collect(Collectors.toList());

        List<MemberDTO> memberDTOs = mrepo.findAll().stream()
                .map(MemberDTO::toDTO)
                .collect(Collectors.toList());

        List<PreStoreDTO> preStoreDTOs = srepo.findAll().stream()
                .map(PreStoreDTO::toDTO)
                .collect(Collectors.toList());

        // 회원 정보 매핑 (memId -> MemberDTO)
        Map<Long, MemberDTO> memberMap = memberDTOs.stream()
                .collect(Collectors.toMap(MemberDTO::getMId, member -> member));

        // 가게 정보 매핑 (preStoId -> 가게 카테고리)
        Map<Long, String> storeMap = preStoreDTOs.stream()
                .collect(Collectors.toMap(PreStoreDTO::getPreStoId, PreStoreDTO::getPreStoCategory));

        // 결과 저장할 Map (나이대 -> {카테고리 -> 주문 건수})
        Map<String, Map<String, Integer>> ageCategoryCount = new HashMap<>();

        for (OrderDTO order : orderDTOs) {
            Long memId = order.getMemId();  // 주문한 회원 ID
            Long preStoId = order.getPrestoId(); // 주문한 가게 ID

            // 회원과 가게 정보가 존재하는 경우만 처리
            if (memberMap.containsKey(memId) && storeMap.containsKey(preStoId)) {
                MemberDTO member = memberMap.get(memId);
                String category = storeMap.get(preStoId);

                // 회원 나이대 계산 (ex: 1990년생 → "30대")
                String ageGroup = calculateAgeGroup(String.valueOf(member.getBirthday()));

                // 나이대별 카테고리 주문 수 저장
                ageCategoryCount.putIfAbsent(ageGroup, new HashMap<>());
                ageCategoryCount.get(ageGroup).merge(category, 1, Integer::sum);
            }
        }

        return ageCategoryCount;
    }

    // 🔹 생년월일로 나이대 계산 (ex: 1995년생 → "20대")
    private String calculateAgeGroup(String birthDate) {
        if (birthDate == null || birthDate.length() < 4) return "기타";

        int birthYear = Integer.parseInt(birthDate.substring(0, 4));
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - birthYear;

        if (age < 20) return "10대 이하";
        if (age < 30) return "20대";
        if (age < 40) return "30대";
        if (age < 50) return "40대";
        if (age < 60) return "50대";
        return "60대 이상";
    }

    public List<Map<String, Object>> topOrderMemList() {

        // 모든 주문 정보와 회원 정보 가져오기
        List<OrderDTO> orderDTOs = orepo.findAll().stream()
                .map(OrderDTO::toDTO)
                .collect(Collectors.toList());

        List<MemberDTO> memberDTOs = mrepo.findAll().stream()
                .map(MemberDTO::toDTO)
                .collect(Collectors.toList());

        // 회원별 주문 횟수 계산
        Map<Long, Long> orderCountMap = orderDTOs.stream()
                .collect(Collectors.groupingBy(OrderDTO::getMemId, Collectors.counting()));

        // 주문 횟수 기준으로 내림차순 정렬
        List<Map.Entry<Long, Long>> sortedOrderCounts = orderCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .collect(Collectors.toList());

        // 결과를 저장할 리스트 생성
        List<Map<String, Object>> topMembers = new ArrayList<>();

        // 회원 정보를 가져와서 회원 이름, 아이디, 주문 수 포함
        for (Map.Entry<Long, Long> entry : sortedOrderCounts) {
            Long memberId = entry.getKey();
            Long orderCount = entry.getValue();

            // 회원 정보 찾기
            MemberDTO member = memberDTOs.stream()
                    .filter(m -> m.getMId().equals(memberId))
                    .findFirst()
                    .orElse(null);

            if (member != null) {
                // 회원 정보를 Map에 저장
                Map<String, Object> memberData = new HashMap<>();
                memberData.put("id", memberId);
                memberData.put("name", member.getUsername());
                memberData.put("orderCount", orderCount);

                topMembers.add(memberData);
            }
        }

        return topMembers;
    }


    public Map<String, Long> rewardGradeData() {
        // 모든 회원을 MemberDTO로 변환
        List<MemberDTO> memberDTOs = mrepo.findAll().stream()
                .map(MemberDTO::toDTO)
                .collect(Collectors.toList());

        // 각 등급별로 count를 세기 위한 Map
        Map<String, Long> gradeCounts = memberDTOs.stream()
                .collect(Collectors.groupingBy(MemberDTO::getGrade, Collectors.counting()));

        return gradeCounts;  // JSON으로 자동 변환될 Map 형태로 반환
    }

}
