/**
 * toRad: 도(degree)를 라디안(radian)으로 변환합니다.
 * @param {number} deg - 각도 (degree)
 * @return {number} 각도 (radian)
 */
function toRad(deg) {
    return deg * Math.PI / 180;
}

/**
 * haversineDistance: 두 좌표 간의 거리를 하버사인 공식을 이용하여 계산합니다.
 * @param {Object} coord1 - 첫 번째 좌표 { lat: 위도, lng: 경도 }
 * @param {Object} coord2 - 두 번째 좌표 { lat: 위도, lng: 경도 }
 * @return {number} 두 좌표 사이의 거리 (미터)
 */
function haversineDistance(coord1, coord2) {
    const R = 6371000; // 지구의 반지름 (미터)

    // 두 좌표 간의 위도/경도 차이를 라디안으로 변환
    const dLat = toRad(coord2.lat - coord1.lat);
    const dLng = toRad(coord2.lng - coord1.lng);

    // 좌표를 라디안으로 변환
    const lat1 = toRad(coord1.lat);
    const lat2 = toRad(coord2.lat);

    // 하버사인 공식
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1) * Math.cos(lat2) *
        Math.sin(dLng / 2) * Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = R * c;

    return distance;
}

// 사용 예시:
const coordA = { lat: 37.4655868339819, lng: 126.643508202679 };
const coordB = { lat: 37.4388938204128, lng: 126.675113024566 };

console.log("두 좌표 사이의 거리 (미터):", haversineDistance(coordA, coordB));

/**
 * computeTravelTime: 두 좌표 사이의 이동시간(분)을 계산합니다.
 * @param {kakao.maps.LatLng} fromLatLng - 시작 위치 (라이더 위치)
 * @param {kakao.maps.LatLng} toLatLng - 도착 위치 (매장 위치)
 * @return {number} 예상 이동시간(분)
 */
function computeTravelTime(fromLatLng, toLatLng) {
    // kakao.maps.LatLng 객체에서 위도/경도 값을 추출하여 객체 생성
    const from = { lat: fromLatLng.getLat(), lng: fromLatLng.getLng() };
    const to = { lat: toLatLng.getLat(), lng: toLatLng.getLng() };

    // 하버사인 공식으로 두 좌표 사이의 거리를 미터 단위로 계산
    var distance = haversineDistance(from, to);

    // 예: 평균 이동 속도 40km/h ≒ 11.11 m/s (필요에 따라 수정)
    var avgSpeed = 11.11;
    var timeInSeconds = distance / avgSpeed;

    // 이동 시간을 분 단위로 올림하여 반환
    return Math.ceil(timeInSeconds / 60);
}
