package com.icia.delivery.domain.routing;

import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 카카오 키 없이 도는 데모 전용 좌표 변환 창구.
 *
 * <p>h2 프로파일에서만 뜬다. 운영 프로파일에 가짜 좌표를 내려주는 경로를 남기지 않기 위해서다.
 * 화면은 카카오 지오코더를 먼저 부르고, 그것이 없거나 실패했을 때만 여기로 내려온다.
 */
@RestController
@Profile("h2")
public class DemoGeocodeController {

    public record GeocodeRequest(List<String> addresses) {
    }

    /** 화면 좌표 규약을 그대로 따른다. x 가 경도, y 가 위도다. */
    public record PointResponse(double x, double y) {
    }

    @PostMapping("/rider/route/geocode/demo")
    public ResponseEntity<ApiResponse<List<PointResponse>>> geocode(@RequestBody GeocodeRequest request) {
        if (request == null || request.addresses() == null || request.addresses().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "주소가 없습니다.");
        }

        List<PointResponse> points = new ArrayList<>(request.addresses().size());
        for (String address : request.addresses()) {
            GeoPoint point = DemoAddressGeocoder.locate(address);
            points.add(point == null ? null : new PointResponse(point.lon(), point.lat()));
        }
        return ResponseEntity.ok(ApiResponse.success(points));
    }
}
