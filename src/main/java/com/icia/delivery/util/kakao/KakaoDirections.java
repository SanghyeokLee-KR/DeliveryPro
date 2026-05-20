// src/main/java/com/icia/delivery/util/kakao/KakaoDirections.java
package com.icia.delivery.util.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoDirections {
    private String trans_id;          // 트랜잭션 ID (옵션)
    private List<Route> routes;       // 여러 경로

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        private int result_code;          // 결과 코드 (0: 성공)
        private String result_msg;        // 결과 메시지
        private Summary summary;          // 경로 요약 정보
        private List<Section> sections;   // 세부 구간들
        private List<Guide> guides;       // 길안내 정보 (옵션)
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary {
        private Origin origin;            // 출발지 정보
        private Destination destination;  // 도착지 정보
        private List<?> waypoints;        // 경유지 정보 (필요 시)
        private String priority;          // 우선순위 ("RECOMMEND" 등)
        private Bound bound;              // 전체 경로 경계
        private Fare fare;                // 요금 정보
        private double distance;          // 총 거리 (미터)
        private long duration;            // 총 소요시간 (초)
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Origin {
        private String name;
        private double x; // 경도
        private double y; // 위도
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Destination {
        private String name;
        private double x;
        private double y;
        private String correction_result;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bound {
        private double min_x;
        private double min_y;
        private double max_x;
        private double max_y;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fare {
        private int taxi;
        private int toll;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Section {
        private List<Road> roads;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Road {
        private String name;              // 도로 이름 (있을 경우)
        private double distance;          // 도로 구간 거리 (미터)
        private long duration;            // 도로 구간 소요시간 (초)
        private double traffic_speed;     // 평균 속도 (옵션)
        private int traffic_state;        // 교통 상황 (옵션)
        private List<Double> vertexes;    // 해당 도로의 좌표 배열
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Guide {
        private String name;          // 안내지점 이름 ("출발지", "목적지" 등)
        private double x;             // 안내지점 경도
        private double y;             // 안내지점 위도
        private double distance;      // 해당 안내지점까지의 거리
        private long duration;        // 해당 안내지점까지의 소요시간 (초)
        private int type;             // 안내 유형 (숫자 코드)
        private String guidance;      // 안내 메시지 (예: "우회전")
        private int road_index;       // 도로 인덱스 (옵션)
    }
}
