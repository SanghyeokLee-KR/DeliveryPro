package com.icia.delivery.domain.routing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("데모 대체 지오코더")
class DemoAddressGeocoderTest {

    /** 데모 시드에 든 매장과 회원 주소다. 이 주소들이 실제로 쓰인다. */
    private static final List<String> SEED_ADDRESSES = List.of(
            "인천광역시 미추홀구 미추홀대로 605",
            "인천광역시 미추홀구 인하로 67, 101동 1203호",
            "인천광역시 미추홀구 한나루로 331, 604호",
            "인천광역시 미추홀구 독배로 215, 201호",
            "인천광역시 미추홀구 매소홀로 488, 1801호");

    @Test
    @DisplayName("같은 주소는 언제나 같은 좌표를 낸다")
    void sameAddressGivesSamePoint() {
        for (String address : SEED_ADDRESSES) {
            GeoPoint first = DemoAddressGeocoder.locate(address);
            GeoPoint second = DemoAddressGeocoder.locate("  " + address + " ");
            assertNotNull(first);
            assertEquals(first, second, "앞뒤 공백만 다른 주소는 같은 점이어야 한다");
        }
    }

    @Test
    @DisplayName("주소마다 다른 좌표가 나온다")
    void differentAddressesGiveDifferentPoints() {
        Set<GeoPoint> points = new HashSet<>();
        for (String address : SEED_ADDRESSES) {
            points.add(DemoAddressGeocoder.locate(address));
        }
        assertEquals(SEED_ADDRESSES.size(), points.size(), "좌표가 겹치면 배달지가 한 점으로 뭉친다");
    }

    @Test
    @DisplayName("좌표는 서비스 구역 안이라 도로망에 붙는다")
    void pointsSnapToTheRoadNetwork() {
        NodeIndex index = RoadNetworkProvider.index();
        for (String address : SEED_ADDRESSES) {
            GeoPoint point = DemoAddressGeocoder.locate(address);
            assertTrue(point.lon() > DemoRoadNetwork.MIN_LON && point.lon() < DemoRoadNetwork.MAX_LON);
            assertTrue(point.lat() > DemoRoadNetwork.MIN_LAT && point.lat() < DemoRoadNetwork.MAX_LAT);
            assertTrue(index.nearest(point.lon(), point.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS) >= 0,
                    "붙지 못하면 경로가 도달 불가로 떨어진다. address=" + address);
        }
    }

    @Test
    @DisplayName("주소가 비면 좌표를 만들지 않는다")
    void blankAddressGivesNothing() {
        assertNull(DemoAddressGeocoder.locate(null));
        assertNull(DemoAddressGeocoder.locate("   "));
    }
}
