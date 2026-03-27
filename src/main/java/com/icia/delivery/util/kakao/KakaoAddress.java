// src/main/java/com/icia/delivery/util/kakao/KakaoAddress.java

package com.icia.delivery.util.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Kakao의 주소 검색 결과 JSON 전체를 매핑
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddress {
    private List<Document> documents; // 검색된 주소 문서 목록
}
