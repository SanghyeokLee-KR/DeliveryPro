package com.icia.delivery.dto.rider;

import lombok.Data;

import java.util.List;

@Data
public class GroupRiderCallRequest {
    private List<Long> orderIds;
    private String callTime;  // "yyyy-MM-dd HH:mm:ss" 형식의 호출 시각
}
