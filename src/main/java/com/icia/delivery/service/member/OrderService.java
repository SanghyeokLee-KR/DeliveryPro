package com.icia.delivery.service.member;

import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dao.member.OrderItemRepository;
import com.icia.delivery.dao.member.OrderRepository;
import com.icia.delivery.dao.member.RewardRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dao.rider.DeliveryGroupItemRepository;
import com.icia.delivery.dao.rider.DeliveryGroupRepository;
import com.icia.delivery.dto.common.NotificationDTO;
import com.icia.delivery.dto.member.*;
import com.icia.delivery.dto.president.PreStoreEntity;
import com.icia.delivery.dto.rider.DeliveryGroupEntity;
import com.icia.delivery.dto.rider.DeliveryGroupItemEntity;
import com.icia.delivery.service.common.NotificationService;
import com.icia.delivery.util.KakaoApiUtil;
import com.icia.delivery.util.KakaoGeocoderUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.icia.delivery.util.KakaoApiUtil.isSamePoint;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final DeliveryGroupRepository deliveryGroupRepository;
    private final DeliveryGroupItemRepository deliveryGroupItemRepository;
    private final NotificationService notificationService;
    private final HttpSession session;
    private final RewardRepository rewardRepository;

    @Transactional
    public ModelAndView createOrder(OrderDTO orderDTO) {
        ModelAndView mav = new ModelAndView();

        Long memId = (Long) session.getAttribute("mem_id");
        Long preStoId = (Long) session.getAttribute("preStoId");
        Long menuId = (Long) session.getAttribute("menuId");

        // 1. 주문 엔티티 생성
        OrderEntity orderEntity = OrderEntity.toEntity(orderDTO);
        orderEntity.setOrderStatus("주문접수");
        orderEntity.setMemId(memId);
        orderEntity.setPreStoId(preStoId);
        orderEntity.setMenuId(menuId);
        orderEntity.setOrderCreatedAt(LocalDateTime.now());
        orderEntity.setOrderTotalPrice(orderDTO.getOrderTotalPrice());
        orderEntity.setDeliveryType(orderDTO.getDeliveryType());
        orderEntity.setDeliveryFee(orderDTO.getDeliveryFee());
        orderEntity = orderRepository.save(orderEntity);
        session.setAttribute("orderId", orderDTO.getOrderId());

        // rewardRepository
        Optional<RewardEntity> entity = rewardRepository.findBymemId(memId);
        // 1. 주문 총합계와 배달비를 센트 단위로 저장
        double totalAmount = orderDTO.getOrderTotalPrice();  // 총합계

        if(entity.isPresent()){
            RewardEntity rewardEntity = entity.get();
            rewardEntity.setRewardAmount((long) (rewardEntity.getRewardAmount() + totalAmount));  // 기존 리워드 금액에 합산
            rewardRepository.save(rewardEntity);  // 업데이트된 리워드 엔티티 저장
        } else {
            // 리워드 엔티티가 존재하지 않으면 새로운 엔티티 생성
            RewardEntity rewardEntity = new RewardEntity();
            rewardEntity.setMemId(memId);
            rewardEntity.setRewardAmount((long) totalAmount);  // 새로운 리워드 금액 설정
            rewardRepository.save(rewardEntity);  // 새로운 리워드 엔티티 저장
        }

        // 2. 주문 항목 엔티티 생성 (동일 상품도 개별 저장)
        OrderEntity finalOrderEntity = orderEntity;
        List<OrderItemEntity> orderItems = orderDTO.getOrderItems().stream()
                .map(OrderItemEntity::toEntity)
                .peek(item -> {
                    item.setOrderId(finalOrderEntity.getOrderId()); // 주문 ID 매핑
                    item.setOrderDate(LocalDateTime.now());
                    item.setMenuId(menuId);
                })
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);

        // 3. 가게에 알림 생성 및 전송
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setSenderType("MEMBER");
        notificationDTO.setSenderId(memId);
        notificationDTO.setRecipientType("STORE");
        notificationDTO.setRecipientId(preStoId);
        notificationDTO.setCategory("주문");
        notificationService.createNotification(notificationDTO);

        mav.setViewName("redirect:/customer");
        return mav;
    }

    public List<OrderDTO> findOrderById(Long orderId) {
        Optional<OrderEntity> orderEntityOpt = orderRepository.findByOrderId(orderId);
        return orderEntityOpt
                .map(OrderDTO::toDTO)
                .map(List::of)
                .orElse(List.of());
    }

    public List<OrderDTO> orderList(Long preStoId) {
        List<OrderEntity> orderEntities = orderRepository.findByPreStoId(preStoId);
        return orderEntities.stream()
                .map(OrderDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getOrderSummariesByMemberId(Long memId) {
        List<Object[]> results = orderRepository.findOrderSummariesByMemId(memId);
        List<Map<String, Object>> summaries = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("orderId", row[0]);
            summary.put("orderCreatedAt", row[1]);
            summary.put("storeName", row[2]);
            summary.put("menuName", row[3]);
            summary.put("menuImageUrl", row[4]);
            summary.put("preStoId", row[5]);
            summaries.add(summary);
        }
        return summaries;
    }

    @Transactional
    public void acceptOrder(Long orderId, String action) {
        if ("reject".equalsIgnoreCase(action)) {
            rejectOrder(orderId);
            return;
        }
        Optional<OrderEntity> optionalOrder = orderRepository.findByOrderId(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("주문을 찾을 수 없습니다. orderId=" + orderId);
        }
        OrderEntity order = optionalOrder.get();
        // 주문 상태만 "픽업중"으로 업데이트 (그룹 생성 로직은 제거)
        order.setDeliveryStatus("픽업중");
        orderRepository.save(order);
    }

    @Transactional
    public void rejectOrder(Long orderId) {
        Optional<OrderEntity> optionalOrder = orderRepository.findByOrderId(orderId);
        Long memId = (Long) session.getAttribute("mem_id");
        Long preStoId = (Long) session.getAttribute("preStoId");

        optionalOrder.ifPresent(order -> {
            order.setOrderStatus("거절");
            orderRepository.save(order);

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setSenderType("STORE");
            notificationDTO.setSenderId(preStoId);
            notificationDTO.setRecipientType("MEMBER");
            notificationDTO.setRecipientId(memId);
            notificationDTO.setCategory("취소됨");
            notificationService.createNotification(notificationDTO);
        });
    }

    public List<Map<String, Object>> findOrders(Long orderId) {
        List<Object[]> results = orderRepository.findOrderWithMenuCount(orderId);
        List<Map<String, Object>> delivery = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("orderId", row[0]);
            summary.put("deliveryType", row[1]);
            summary.put("deliveryStatus", row[2]);
            summary.put("address", row[3]);
            summary.put("menuCount", row[4]);
            summary.put("deliveryMessage", row[5]);
            summary.put("preStoName", row[6]);
            summary.put("preStoAddress", row[7]);
            delivery.add(summary);
        }
        return delivery;
    }

    public List<Map<String, Object>> acceptOrders(Long orderId) {
        List<Object[]> results = orderRepository.findOrderWithStore(orderId);
        List<Map<String, Object>> accept = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> orders = new HashMap<>();
            orders.put("orderId", row[0]);
            orders.put("preStoAddress", row[1]);
            orders.put("deliveryFee", row[2]);
            orders.put("deliveryMessage", row[3]);
            orders.put("paymentMethod", row[4]);
            orders.put("totalPrice", row[5]);
            orders.put("address", row[6]);
            accept.add(orders);
            orderRepository.updateDeliveryStatusToInProgress(orderId);
        }
        return accept;
    }

    public List<OrderDTO> storeOrderList() {
        Long preStoId = (Long) session.getAttribute("pre_store_id");
        List<OrderDTO> dtoList = new ArrayList<>();
        List<OrderEntity> entity = orderRepository.findByPreStoId(preStoId);

        for (OrderEntity order : entity) {
            Long memId = order.getMemId();
            Optional<MemberEntity> memberOpt = memberRepository.findById(memId);
            if (memberOpt.isPresent()) {
                MemberEntity member = memberOpt.get();
                String userAddress = member.getAddress();
                OrderDTO dto = OrderDTO.toDTO(order);
                dto.setUserAddress(userAddress);
                dtoList.add(dto);
            } else {
                System.out.println("memId " + memId + "에 해당하는 회원을 찾을 수 없습니다.");
            }
        }
        return dtoList;
    }

    public List<OrderDTO> riderOrderList() {
        List<OrderDTO> dtoList = new ArrayList<>();
        // "배차중" 상태의 주문만 조회
        List<OrderEntity> entityList = orderRepository.findByDeliveryStatusTrimmed("배차중");
        System.out.println("조회된 주문 개수: " + entityList.size());
        entityList.forEach(o -> System.out.println("주문 deliveryStatus: [" + o.getDeliveryStatus() + "]"));

        for (OrderEntity order : entityList) {
            Long memId = order.getMemId();
            Long preStoId = order.getPreStoId();
            Optional<MemberEntity> memberOpt = memberRepository.findById(memId);
            Optional<PreStoreEntity> preStoOpt = storeRepository.findById(preStoId);

            if (memberOpt.isPresent() && preStoOpt.isPresent()) {
                MemberEntity member = memberOpt.get();
                PreStoreEntity preSto = preStoOpt.get();
                OrderDTO dto = OrderDTO.toDTO(order);
                dto.setUserAddress(member.getAddress());
                dto.setStoreAddress(preSto.getPreStoAddress());

                // "묶음배달" 또는 "한집배달" 모두 그룹 호출 방식으로 처리
                if ("묶음배달".equals(order.getDeliveryType()) || "한집배달".equals(order.getDeliveryType())) {
                    List<DeliveryGroupItemEntity> groupItems =
                            deliveryGroupItemRepository.findAllByOrderId(order.getOrderId());
                    if (!groupItems.isEmpty()) {
                        Long deliveryGroupId = groupItems.get(0).getDeliveryId();
                        Optional<DeliveryGroupEntity> dgOpt = deliveryGroupRepository.findById(deliveryGroupId);
                        if (dgOpt.isPresent()) {
                            DeliveryGroupEntity dg = dgOpt.get();
                            // 그룹의 라이더 번호가 미할당이고, 그룹 상태가 "배차중"일 때만 처리
                            if (dg.getRiderNo() == null && "배차중".equals(dg.getDeliveryStatus())) {
                                dto.setDeliveryId(deliveryGroupId);
                                dto.setCallTime(dg.getCallTime());
                                dto.setOrderSequence(groupItems.get(0).getOrderSequence());
                            }
                        }
                    }
                }
                dtoList.add(dto);
            }
        }
        return dtoList;
    }


    /**
     * 단일 주문에 대해 배달 호출(개별 호출)을 진행합니다.
     * 주문 상태를 "배달중"으로 변경하고, 새 DeliveryGroup 및 DeliveryGroupItem을 생성합니다.
     * 단, 이 메서드는 callTime을 별도로 입력하지 않고, 배달 호출 시 생성 시각이 callTime으로 기록되지 않는다면,
     * 추후 다른 방식(그룹 호출)을 사용해야 합니다.
     */
    @Transactional
    public String riderCall(Long orderId) {
        Optional<OrderEntity> opEnti = orderRepository.findByOrderId(orderId);
        if (opEnti.isEmpty()) {
            return "주문이 없습니다.";
        }
        OrderEntity entity = opEnti.get();

        // 주문 상태를 "배차중"으로 변경
        entity.setDeliveryStatus("배차중");
        orderRepository.save(entity);

        // 새 DeliveryGroup 생성 (한집배달도 그룹 호출과 동일하게 처리)
        DeliveryGroupEntity deliveryGroup = new DeliveryGroupEntity();
        deliveryGroup.setRiderNo(null); // 라이더 미할당
        deliveryGroup.setStoreId(entity.getPreStoId());
        // 만약 한집배달이면 "한집배달" 그대로 사용하거나, 그룹 호출과 동일하게 "묶음배달"로 통일할 수 있습니다.
        // 여기서는 후속 처리의 통일성을 위해 "묶음배달"로 강제 설정하는 예시입니다.
        deliveryGroup.setDeliveryType("묶음배달");
        deliveryGroup.setDeliveryStatus("배차중");
        deliveryGroup.setCustomerRequest(entity.getDeliveryMessage());
        deliveryGroup.setDeliveryFee(entity.getDeliveryFee());
        deliveryGroup.setCreatedAt(LocalDateTime.now());
        // 현재 시각을 callTime으로 기록 (한집배달에도 callTime 기록)
        deliveryGroup.setCallTime(LocalDateTime.now());

        DeliveryGroupEntity savedGroup = deliveryGroupRepository.save(deliveryGroup);

        // 새 DeliveryGroupItemEntity 생성 (주문과 그룹 연결)
        DeliveryGroupItemEntity groupItem = new DeliveryGroupItemEntity();
        groupItem.setDeliveryId(savedGroup.getDeliveryId());
        groupItem.setOrderId(orderId);

        // 매장 주소 설정
        Long preStoId = entity.getPreStoId();
        PreStoreEntity preStore = storeRepository.findById(preStoId)
                .orElseThrow(() -> new RuntimeException("가게 정보가 없습니다. preStoId=" + preStoId));
        groupItem.setStoreAddress(preStore.getPreStoAddress());

        // 고객(회원) 주소 정제 후 설정
        Long memId = entity.getMemId();
        MemberEntity member = memberRepository.findById(memId)
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다. memId=" + memId));
        String originalDestination = member.getAddress();
        String cleanedDestination = cleanAddress(originalDestination);
        groupItem.setDestinationAddress(cleanedDestination);

        deliveryGroupItemRepository.save(groupItem);

        return "배달중";
    }
    /**
     * 그룹 배달 호출 (묶음 호출) 메서드
     * - orderIds 리스트와 callTime(문자열, "yyyy-MM-dd HH:mm:ss")를 받아 처리합니다.
     */
    @Transactional
    public void groupRiderCall(List<Long> orderIds, String callTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime callDateTime = LocalDateTime.parse(callTime, formatter);

        // 그룹 호출 시, 주문 목록이 비어있지 않다고 가정하고, 첫 주문의 preStoId를 사용하여 새 DeliveryGroup 생성
        if (orderIds.isEmpty()) {
            return;
        }

        Long firstOrderId = orderIds.get(0); // getFirst() 대신 index 0 사용
        Optional<OrderEntity> firstOrderOpt = orderRepository.findByOrderId(firstOrderId);
        if (firstOrderOpt.isEmpty()) {
            throw new RuntimeException("첫 주문을 찾을 수 없습니다. orderId=" + firstOrderId);
        }
        OrderEntity firstOrder = firstOrderOpt.get();

        DeliveryGroupEntity deliveryGroup = new DeliveryGroupEntity();
        deliveryGroup.setRiderNo(null);
        deliveryGroup.setStoreId(firstOrder.getPreStoId());
        deliveryGroup.setDeliveryType("묶음배달");
        deliveryGroup.setDeliveryStatus("배차중");
        deliveryGroup.setCustomerRequest("묶음 배달 호출");
        // 필요에 따라 배달비를 합산하거나 0 처리
        deliveryGroup.setDeliveryFee(0);
        deliveryGroup.setCreatedAt(LocalDateTime.now());
        DeliveryGroupEntity savedGroup = deliveryGroupRepository.save(deliveryGroup);

        // 각 주문에 대해 주문 상태를 "배달중"으로 변경하고, 새 DeliveryGroupItemEntity 생성
        for (Long orderId : orderIds) {
            Optional<OrderEntity> opOrder = orderRepository.findByOrderId(orderId);
            if (opOrder.isPresent()) {
                OrderEntity order = opOrder.get();
                order.setDeliveryStatus("배차중");
                orderRepository.save(order);

                DeliveryGroupItemEntity groupItem = new DeliveryGroupItemEntity();
                groupItem.setDeliveryId(savedGroup.getDeliveryId());
                groupItem.setOrderId(orderId);

                Long preStoId = order.getPreStoId();
                PreStoreEntity preStore = storeRepository.findById(preStoId)
                        .orElseThrow(() -> new RuntimeException("가게 정보가 없습니다. preStoId=" + preStoId));
                groupItem.setStoreAddress(preStore.getPreStoAddress());

                Long memId = order.getMemId();
                MemberEntity member = memberRepository.findById(memId)
                        .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다. memId=" + memId));
                String originalDestination = member.getAddress();
                String cleanedDestination = cleanAddress(originalDestination);
                groupItem.setDestinationAddress(cleanedDestination);

                deliveryGroupItemRepository.save(groupItem);
            }
        }

        // 업데이트: 그룹의 callTime 및 업데이트 시간 설정 (콜 타임을 명시적으로 기록)
        savedGroup.setUpdatedAt(callDateTime);
        savedGroup.setCallTime(callDateTime);
        deliveryGroupRepository.save(savedGroup);

        System.out.println("[groupRiderCall] 새 그룹 생성 완료: deliveryId=" + savedGroup.getDeliveryId());

        // 2. 그룹 내 모든 DeliveryGroupItemEntity의 최적 경로 순서 산출 및 order_sequence 업데이트
        List<DeliveryGroupItemEntity> allGroupItems = deliveryGroupItemRepository.findAllByDeliveryId(savedGroup.getDeliveryId());
        if (!allGroupItems.isEmpty()) {
            List<String> destinationAddresses = allGroupItems.stream()
                    .map(item -> cleanAddress(item.getDestinationAddress()))
                    .collect(Collectors.toList());
            System.out.println("[groupRiderCall] 정제된 배송지 목록: " + destinationAddresses);

            // KakaoGeocoderUtil.geocodeAddresses는 콜백 방식이 일반적이지만,
            // 여기서는 동기식(또는 별도 처리)으로 가정합니다.
            List<KakaoApiUtil.Point> destinationPoints = KakaoGeocoderUtil.geocodeAddresses(destinationAddresses);
            System.out.println("[groupRiderCall] 배송지 좌표: " + destinationPoints);

            String storeAddressOriginal = allGroupItems.get(0).getStoreAddress();
            String storeAddress = cleanAddress(storeAddressOriginal);
            KakaoApiUtil.Point storePoint = KakaoGeocoderUtil.geocodeAddress(storeAddress);
            System.out.println("[groupRiderCall] 매장 주소: " + storeAddressOriginal + " → 정제 후: " + storeAddress);
            System.out.println("[groupRiderCall] 매장 좌표: " + storePoint);

            List<KakaoApiUtil.Point> optimizedOrder =
                    KakaoGeocoderUtil.RouteOptimizer.optimizeRouteOrder(storePoint, destinationPoints);
            System.out.println("[groupRiderCall] 최적 순서 좌표: " + optimizedOrder);

            // 최적의 배송 순서에 따라 각 그룹 아이템의 orderSequence 업데이트
            for (int seq = 1; seq <= optimizedOrder.size(); seq++) {
                KakaoApiUtil.Point p = optimizedOrder.get(seq - 1);
                boolean found = false;
                for (DeliveryGroupItemEntity item : allGroupItems) {
                    KakaoApiUtil.Point itemPoint = KakaoGeocoderUtil.geocodeAddress(item.getDestinationAddress());
                    System.out.println("[groupRiderCall] 비교: 최적 좌표 " + p + " vs. 배송지 좌표 " + itemPoint);
                    if (isSamePoint(p, itemPoint)) {
                        item.setOrderSequence(seq);
                        System.out.println("[groupRiderCall] orderId " + item.getOrderId() + "의 order_sequence 업데이트: " + seq);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("[groupRiderCall] 최적 좌표 " + p + "와 일치하는 배송지가 없음");
                }
            }
            deliveryGroupItemRepository.saveAll(allGroupItems);
            System.out.println("[groupRiderCall] 묶음배달 처리 완료, 최적 순서 부여됨");
        }
    }

    @Transactional
    public void acceptBatchOrders(Long preStoId, List<Long> orderIds) {
        System.out.println("\n[acceptBatchOrders] 시작 - preStoId=" + preStoId + ", orderIds=" + orderIds);

        // 항상 새로운 그룹 생성
        DeliveryGroupEntity group = new DeliveryGroupEntity();
        group.setRiderNo(null);
        group.setStoreId(preStoId);
        group.setDeliveryType("묶음배달");
        group.setDeliveryStatus("배차중");
        group.setCustomerRequest("가게에서 묶음 접수");
        group.setDeliveryFee(0);
        group.setCreatedAt(LocalDateTime.now());
        group = deliveryGroupRepository.save(group);
        System.out.println("[acceptBatchOrders] 새 묶음배달 그룹 생성: deliveryId=" + group.getDeliveryId());

        List<DeliveryGroupItemEntity> groupItems = new ArrayList<>();
        for (Long orderId : orderIds) {
            Optional<OrderEntity> opOrder = orderRepository.findById(orderId);
            if (opOrder.isEmpty()) {
                System.out.println("[acceptBatchOrders] 주문 없음: orderId=" + orderId);
                continue;
            }
            OrderEntity ord = opOrder.get();
            ord.setDeliveryStatus("배차중");
            orderRepository.save(ord);

            DeliveryGroupItemEntity item = new DeliveryGroupItemEntity();
            item.setDeliveryId(group.getDeliveryId());
            item.setOrderId(orderId);
            item.setOrderSequence(0);

            PreStoreEntity preStore = storeRepository.findById(preStoId)
                    .orElseThrow(() -> new RuntimeException("가게 정보 없음: preStoId=" + preStoId));
            item.setStoreAddress(preStore.getPreStoAddress());

            MemberEntity member = memberRepository.findById(ord.getMemId())
                    .orElseThrow(() -> new RuntimeException("회원 정보 없음: memId=" + ord.getMemId()));
            String originalDestination = member.getAddress();
            String cleanedDestination = cleanAddress(originalDestination);
            item.setDestinationAddress(cleanedDestination);
            System.out.println("[acceptBatchOrders] orderId=" + orderId
                    + ", 배송지 원본: " + originalDestination + ", 정제된 배송지: " + cleanedDestination);

            groupItems.add(item);
        }
        deliveryGroupItemRepository.saveAll(groupItems);
        System.out.println("[acceptBatchOrders] DeliveryGroupItem 저장 완료. 항목 수: " + groupItems.size());

        // 배송지 주소 목록과 좌표 산출
        List<String> destinationAddresses = groupItems.stream()
                .map(DeliveryGroupItemEntity::getDestinationAddress)
                .collect(Collectors.toList());
        System.out.println("[acceptBatchOrders] 배송지 목록: " + destinationAddresses);

        // KakaoGeocoderUtil 사용 (동기식 처리 가정)
        List<KakaoApiUtil.Point> destinationPoints = KakaoGeocoderUtil.geocodeAddresses(destinationAddresses);
        System.out.println("[acceptBatchOrders] 배송지 좌표: " + destinationPoints);

        PreStoreEntity preStore = storeRepository.findById(preStoId)
                .orElseThrow(() -> new RuntimeException("가게 정보 없음: preStoId=" + preStoId));
        String storeAddressOriginal = preStore.getPreStoAddress();
        String storeAddress = cleanAddress(storeAddressOriginal);
        KakaoApiUtil.Point storePoint = KakaoGeocoderUtil.geocodeAddress(storeAddress);
        System.out.println("[acceptBatchOrders] 매장 주소: " + storeAddressOriginal + " → 정제 후: " + storeAddress);
        System.out.println("[acceptBatchOrders] 매장 좌표: " + storePoint);

        List<KakaoApiUtil.Point> optimizedOrder =
                KakaoGeocoderUtil.RouteOptimizer.optimizeRouteOrder(storePoint, destinationPoints);
        System.out.println("[acceptBatchOrders] 최적 순서 좌표: " + optimizedOrder);

        // 최적의 배송 순서에 따라 각 그룹 아이템의 orderSequence 업데이트
        for (int seq = 1; seq <= optimizedOrder.size(); seq++) {
            KakaoApiUtil.Point p = optimizedOrder.get(seq - 1);
            boolean found = false;
            for (DeliveryGroupItemEntity item : groupItems) {
                KakaoApiUtil.Point itemPoint = KakaoGeocoderUtil.geocodeAddress(item.getDestinationAddress());
                System.out.println("[acceptBatchOrders] 비교: 최적 좌표 " + p + " vs. 배송지 좌표 " + itemPoint);
                if (isSamePoint(p, itemPoint)) {
                    item.setOrderSequence(seq);
                    System.out.println("[acceptBatchOrders] orderId " + item.getOrderId() + "의 order_sequence 업데이트: " + seq);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("[acceptBatchOrders] 최적 좌표 " + p + "와 일치하는 배송지가 없음");
            }
        }
        deliveryGroupItemRepository.saveAll(groupItems);
        System.out.println("[acceptBatchOrders] 묶음배달 처리 완료, 최적 순서 부여됨");
    }

    @Transactional
    public Long getMemIdByOrderId(Long orderId) {
        return orderRepository.findMemIdByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("🚨 해당 주문 ID의 회원 정보가 없습니다: " + orderId));
    }

    // Private 메서드: 주소 정제
    private String cleanAddress(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }
        String[] parts = address.trim().split("\\s+");
        if (parts.length > 1 && parts[0].matches("\\d+")) {
            return String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        }
        return address;
    }


    @Transactional
    public void completeOrder(Long orderId, Long riderNo) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다. orderId=" + orderId));

        // (필요하다면) 해당 주문이 현재 라이더가 수행할 수 있는 주문인지 검증
        // 예: order.getDeliveryStatus()가 "배달중"인지 등

        order.setDeliveryStatus("배달완료");
        orderRepository.save(order);
    }
}

