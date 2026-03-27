package com.icia.delivery.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    String searchCategory;
    String searchKeyword;
}
