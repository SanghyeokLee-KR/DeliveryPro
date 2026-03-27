// src/main/java/com/icia/delivery/util/kakao/Document.java

package com.icia.delivery.util.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Kakao API의 주소/키워드 검색 결과 문서 (장소 1건)
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    private Double x; // 장소의 X 좌표 (경도)
    private Double y; // 장소의 Y 좌표 (위도)

    @JsonProperty("place_name")
    private String placeName; // 장소 이름 (키워드 검색 시 사용)

    private String phone; // 장소 전화번호 (키워드 검색 시)
}
