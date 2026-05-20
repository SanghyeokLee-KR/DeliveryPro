package com.icia.delivery.service.admin;

import com.icia.delivery.dao.common.BoardRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dao.member.OrderRepository;
import com.icia.delivery.dao.president.PreMemRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dao.rider.RiderRepository;
import com.icia.delivery.dto.common.BoardEntity;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.member.OrderEntity;
import com.icia.delivery.dto.president.PreMemberEntity;
import com.icia.delivery.dto.president.PreStoreEntity;
import com.icia.delivery.dto.rider.RiderEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository mrepo;
    private final RiderRepository rrepo;
    private final PreMemRepository pmrepo;
    private final StoreRepository srepo;
    private final OrderRepository orepo;
    private final BoardRepository brepo;

    @Transactional
    public void dinf(Model model) {
        List<MemberEntity> memberEntities = mrepo.findAll();
        List<RiderEntity> riderEntities = rrepo.findAll();
        List<PreMemberEntity> preMemberEntities = pmrepo.findAll();
        List<PreStoreEntity> preStoreEntities = srepo.findAll();
        List<OrderEntity> orderEntities = orepo.findAll();
        List<BoardEntity> boardEntities = brepo.findAll();

        int totalMember = memberEntities.size();
        int totalRider = riderEntities.size();
        int totalPreMem = preMemberEntities.size();
        int totalPreSto = preStoreEntities.size();
        int totalBoard = boardEntities.size();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();  // 오늘 00:00
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59, 999999);  // 오늘 23:59:59.999999

        // 오늘 회원가입한 사람만 필터링
        int memCount = (int) memberEntities.stream()
                .filter(member -> !member.getRegisterDate().isBefore(startOfDay) && !member.getRegisterDate().isAfter(endOfDay))
                .count();

        // 오늘 라이더 가입한 사람만 필터링
        int riderCount = (int) riderEntities.stream()
                .filter(rider -> !rider.getRiderCreatedAt().isBefore(startOfDay) && !rider.getRiderCreatedAt().isAfter(endOfDay))
                .count();

        // 오늘 가입한 사장님만 필터링
        int preMemCount = (int) preMemberEntities.stream()
                .filter(preMember -> !preMember.getPreMemCreatedAt().isBefore(startOfDay) && !preMember.getPreMemCreatedAt().isAfter(endOfDay))
                .count();

        // 오늘 추가한 가게만 필터링
        int preStoCount = (int) preStoreEntities.stream()
                .filter(preStore -> !preStore.getPreStoCreatedAt().isBefore(startOfDay) && !preStore.getPreStoCreatedAt().isAfter(endOfDay))
                .count();

        // 오늘 가장 많이 주문된 카테고리
        // 오늘의 주문만 필터링
        // 1. 주문된 가게 ID 리스트를 가져옵니다.
        List<Long> preStoIds = orderEntities.stream()
                .filter(order -> !order.getOrderCreatedAt().isBefore(startOfDay) && !order.getOrderCreatedAt().isAfter(endOfDay))
                .map(OrderEntity::getPreStoId)  // pre_sto_id를 Long으로 추출
                .collect(Collectors.toList());

        // 2. preStoIds에 해당하는 PreStoreEntity를 가져옵니다.
        List<PreStoreEntity> preStoreEntitiList = srepo.findBypreStoIdCategoryList(preStoIds);

        // 3. 각 가게 ID에 대한 카테고리를 찾아서 카운팅
        Map<Long, String> storeCategoryMap = preStoreEntitiList.stream()
                .collect(Collectors.toMap(PreStoreEntity::getPreStoId, PreStoreEntity::getPreStoCategory));

        // 4. 가게 ID별로 주문 수를 세기
        Map<Long, Long> storeOrderCountMap = preStoIds.stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        // 5. 가게 ID별 주문 수와 카테고리 정보를 기반으로, 카테고리별 주문 수를 카운팅
        Map<String, Long> categoryCountMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry : storeOrderCountMap.entrySet()) {
            Long storeId = entry.getKey();
            Long orderCount = entry.getValue();

            // 해당 가게의 카테고리를 가져옵니다.
            String category = storeCategoryMap.get(storeId);

            // 카테고리별 주문 수를 누적합니다.
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0L) + orderCount);
        }

        // 6. 가장 많이 주문된 카테고리를 찾습니다.
        String mostOrderedCategory = "";
        Long mostOrderedCategoryCount = 0L;
        Optional<Map.Entry<String, Long>> maxCategory = categoryCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue());  // 가장 많이 주문된 카테고리 찾기

        if (maxCategory.isPresent()) {
            mostOrderedCategory = maxCategory.get().getKey();
            mostOrderedCategoryCount = maxCategory.get().getValue();
        }

        // 미답변 문의 갯수 확인
        int unanswered = brepo.findByUnansweredCount();

        // statistics Map 생성 후, 데이터를 추가
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalMember", totalMember);
        statistics.put("todayMember", memCount);
        statistics.put("totalRider", totalRider);
        statistics.put("riderCount", riderCount);
        statistics.put("totalPreMem", totalPreMem);
        statistics.put("totalPreSto", totalPreSto);
        statistics.put("totalBoard", totalBoard);
        statistics.put("unanswered", unanswered);
        statistics.put("preMemCount", preMemCount);
        statistics.put("preStoCount", preStoCount);

        // 가장 많이 주문된 카테고리와 그 주문 횟수 추가
        statistics.put("mostOrderedCategory", mostOrderedCategory);
        statistics.put("mostOrderedCategoryCount", mostOrderedCategoryCount);

        // model에 Map 추가
        model.addAttribute("dash", statistics);
    }



    public Map<String, Long> dashboardChart() {
        List<OrderEntity> entities = orepo.findAll();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();  // 오늘 00:00
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59, 999999);  // 오늘 23:59:59.999999

        // 오늘의 주문만 필터링
        List<Long> preStoIds = entities.stream()
                .filter(order -> !order.getOrderCreatedAt().isBefore(startOfDay) && !order.getOrderCreatedAt().isAfter(endOfDay))
                .map(OrderEntity::getPreStoId)  // pre_sto_id를 Long으로 추출
                .collect(Collectors.toList());

        // 해당하는 preStoId를 가진 가게들의 카테고리 정보를 가져옴
        List<PreStoreEntity> entityList = srepo.findBypreStoIdCategoryList(preStoIds);

        // preStoIds의 순서대로 카테고리를 가져와 storeCategory에 추가
        List<String> storeCategory = new ArrayList<>();
        for (Long preStoId : preStoIds) {
            entityList.stream()
                    .filter(entity -> entity.getPreStoId().equals(preStoId))
                    .findFirst()
                    .ifPresent(entity -> storeCategory.add(entity.getPreStoCategory()));
        }

        // 카테고리별 주문 수를 집계
        Map<String, Long> categoryCountMap = storeCategory.stream()
                .collect(Collectors.groupingBy(category -> category, Collectors.counting()));

        return categoryCountMap;  // 카테고리별 주문 수를 Map으로 반환
    }


}
