package com.icia.delivery.service.rider;

import com.icia.delivery.dao.member.OrderRepository;
import com.icia.delivery.dao.rider.DeliveryGroupRepository;
import com.icia.delivery.dto.member.OrderEntity;
import com.icia.delivery.dto.rider.DeliveryGroupDTO;
import com.icia.delivery.dto.rider.DeliveryGroupEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryGroupService {

    @Autowired
    private DeliveryGroupRepository deliveryGroupRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 배달 상태가 "배달중" 또는 "배달완료"인 묶음 배달 주문 내역 조회
     */
    public List<DeliveryGroupDTO> getProcessedGroupOrders() {
        List<DeliveryGroupEntity> entities =
                deliveryGroupRepository.findByDeliveryStatusIn(Arrays.asList("배달중", "배달완료"));
        return entities.stream()
                .map(DeliveryGroupDTO::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 배달 상태가 "배달중" 또는 "배달완료"인 한집배달 주문 내역 조회
     */
    public List<DeliveryGroupDTO> getProcessedSingleOrders() {
        List<DeliveryGroupEntity> entities =
                deliveryGroupRepository.findByDeliveryTypeAndDeliveryStatusIn("한집배달",
                        Arrays.asList("배달중", "배달완료"));
        return entities.stream()
                .map(DeliveryGroupDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptDeliveryGroup(Long deliveryId, Long riderNo) {
        DeliveryGroupEntity group = deliveryGroupRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("해당 배달 그룹을 찾을 수 없습니다. deliveryId=" + deliveryId));

        // 이미 다른 라이더가 수락한 경우 (필요하면 예외 처리)
        if (group.getRiderNo() != null && !group.getRiderNo().equals(riderNo)) {
            throw new RuntimeException("이미 다른 라이더가 이 배달 그룹을 수락했습니다.");
        }

        group.setRiderNo(riderNo);
        deliveryGroupRepository.save(group);
    }

    @Transactional
    public void completeDeliveryGroup(Long deliveryId, Long riderNo) {
        DeliveryGroupEntity group = deliveryGroupRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("해당 배달 그룹을 찾을 수 없습니다. deliveryId=" + deliveryId));

        // (필요하다면) 라이더 번호 검증 등 추가
        // 예: if (!group.getRiderNo().equals(riderNo)) { throw new RuntimeException("권한 없음"); }

        group.setDeliveryStatus("배달완료");
        deliveryGroupRepository.save(group);
    }
}
