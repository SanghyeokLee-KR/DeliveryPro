// src/main/java/com/icia/delivery/util/KakaoApiUtil.java

package com.icia.delivery.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icia.delivery.util.kakao.KakaoDirections;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class KakaoApiUtil {

    private final String REST_API_KEY;

    public KakaoApiUtil(@Value("${kakao.api.key}") String restApiKey) {
        this.REST_API_KEY = restApiKey;
    }

    @Data
    public static class Point {
        private Double x;  // 경도
        private Double y;  // 위도

        public Point() {}
        public Point(Double x, Double y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "Point{" + "x=" + x + ", y=" + y + '}';
        }
    }

    @Data
    public static class RoadSegment {
        private List<Point> coords; // 해당 구간의 좌표 배열
        private double distance;    // m
        private long duration;      // 초
    }

    @Data
    public static class Summary {
        private double distance; // 총 m
        private long duration;   // 총 초
    }

    @Data
    public static class RouteResult {
        private List<RoadSegment> segments; // 구간 목록
        private Summary summary;            // 전체 요약
    }

    //-----------------------------
    // 단일(from→to) 구간
    //-----------------------------
    public RouteResult getRouteResult(Double fromX, Double fromY, Double toX, Double toY)
            throws IOException, InterruptedException {

        RouteResult result = new RouteResult();
        result.setSegments(new ArrayList<>());
        Summary sum = new Summary();
        result.setSummary(sum);

        if(fromX == null || fromY == null || toX == null || toY == null) {
            sum.setDistance(0);
            sum.setDuration(0);
            return result;
        }

        HttpClient client = HttpClient.newHttpClient();
        String url = "https://apis-navi.kakaomobility.com/v1/directions?"
                + "origin=" + fromX + "," + fromY
                + "&destination=" + toX + "," + toY
                + "&priority=RECOMMEND";

        HttpRequest req = HttpRequest.newBuilder()
                .header("Authorization","KakaoAK " + REST_API_KEY)
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Kakao Directions API Response: " + resp.body());

        KakaoDirections kd = new ObjectMapper().readValue(resp.body(), KakaoDirections.class);
        if(kd.getRoutes() == null || kd.getRoutes().isEmpty()) return result;
        KakaoDirections.Route route = kd.getRoutes().get(0);
        if(route.getSections() == null || route.getSections().isEmpty()) return result;

        // summary 처리
        if(route.getSummary() != null) {
            sum.setDistance(route.getSummary().getDistance());
            sum.setDuration(route.getSummary().getDuration());
            System.out.println("Parsed summary: distance=" + sum.getDistance() + ", duration=" + sum.getDuration());
        } else {
            double totalDist = 0;
            long totalTime = 0;
            for(KakaoDirections.Section sec : route.getSections()){
                if(sec.getRoads() == null) continue;
                for(KakaoDirections.Road r : sec.getRoads()){
                    totalDist += r.getDistance();
                    totalTime += r.getDuration();
                }
            }
            sum.setDistance(totalDist);
            sum.setDuration(totalTime);
        }

        // 각 섹션의 도로 정보를 RoadSegment로 변환
        for(KakaoDirections.Section sec : route.getSections()){
            if(sec.getRoads() == null) continue;
            for(KakaoDirections.Road r : sec.getRoads()){
                RoadSegment seg = new RoadSegment();
                seg.setCoords(new ArrayList<>());
                seg.setDistance(r.getDistance());
                seg.setDuration(r.getDuration());
                List<Double> verts = r.getVertexes();
                for(int i = 0; i < verts.size(); i += 2){
                    if(i + 1 < verts.size()){
                        seg.getCoords().add(new Point(verts.get(i), verts.get(i+1)));
                    }
                }
                result.getSegments().add(seg);
            }
        }
        return result;
    }

    //-----------------------------
    // 경유지 포함 다중 구간
    //-----------------------------
    public RouteResult getRouteResultWithWaypoints(Double fromX, Double fromY, Double toX, Double toY, List<Point> waypoints)
            throws IOException, InterruptedException {
        RouteResult result = new RouteResult();
        result.setSegments(new ArrayList<>());
        Summary sum = new Summary();
        result.setSummary(sum);

        if(fromX == null || fromY == null || toX == null || toY == null) {
            sum.setDistance(0);
            sum.setDuration(0);
            return result;
        }

        StringBuilder wp = new StringBuilder();
        for(int i = 0; i < waypoints.size(); i++){
            if(i > 0) wp.append("|");
            wp.append(waypoints.get(i).getX()).append(",").append(waypoints.get(i).getY());
        }

        HttpClient client = HttpClient.newHttpClient();
        StringBuilder url = new StringBuilder("https://apis-navi.kakaomobility.com/v1/directions");
        url.append("?origin=").append(fromX).append(",").append(fromY);
        url.append("&destination=").append(toX).append(",").append(toY);
        url.append("&priority=RECOMMEND");
        if(wp.length() > 0){
            url.append("&waypoints=").append(wp.toString());
        }

        HttpRequest req = HttpRequest.newBuilder()
                .header("Authorization","KakaoAK " + REST_API_KEY)
                .uri(URI.create(url.toString()))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Kakao Directions API Response with Waypoints: " + resp.body());

        KakaoDirections kd = new ObjectMapper().readValue(resp.body(), KakaoDirections.class);
        if(kd.getRoutes() == null || kd.getRoutes().isEmpty()) return result;
        KakaoDirections.Route route = kd.getRoutes().get(0);
        if(route.getSections() == null || route.getSections().isEmpty()) return result;

        if(route.getSummary() != null) {
            sum.setDistance(route.getSummary().getDistance());
            sum.setDuration(route.getSummary().getDuration());
            System.out.println("Parsed summary (with waypoints): distance=" + sum.getDistance() + ", duration=" + sum.getDuration());
        } else {
            double totalDist = 0;
            long totalTime = 0;
            for(KakaoDirections.Section sec : route.getSections()){
                if(sec.getRoads() == null) continue;
                for(KakaoDirections.Road r : sec.getRoads()){
                    totalDist += r.getDistance();
                    totalTime += r.getDuration();
                }
            }
            sum.setDistance(totalDist);
            sum.setDuration(totalTime);
        }

        for(KakaoDirections.Section sec : route.getSections()){
            if(sec.getRoads() == null) continue;
            for(KakaoDirections.Road r : sec.getRoads()){
                RoadSegment seg = new RoadSegment();
                seg.setCoords(new ArrayList<>());
                seg.setDistance(r.getDistance());
                seg.setDuration(r.getDuration());
                List<Double> verts = r.getVertexes();
                for(int i = 0; i < verts.size(); i += 2){
                    if(i + 1 < verts.size()){
                        seg.getCoords().add(new Point(verts.get(i), verts.get(i+1)));
                    }
                }
                result.getSegments().add(seg);
            }
        }
        return result;
    }

    // 여기에 isSamePoint 메서드를 추가합니다.
    public static boolean isSamePoint(Point p1, Point p2) {
        double threshold = 0.0001;
        return Math.abs(p1.getX() - p2.getX()) < threshold &&
                Math.abs(p1.getY() - p2.getY()) < threshold;
    }

    /**
     * RouteOptimizer 내부 클래스.
     * Nearest Neighbor 방식으로 시작점과 여러 목적지 좌표의 최적 방문 순서를 계산합니다.
     */
    public static class RouteOptimizer {
        /**
         * 시작점과 목적지 리스트를 받아서 최적 방문 순서를 산출합니다.
         *
         * @param start 시작점 좌표 (예: 매장 좌표).
         * @param destinations 목적지 좌표들의 리스트.
         * @return 최적 방문 순서로 정렬된 좌표 리스트.
         */
        public static List<Point> optimizeRouteOrder(Point start, List<Point> destinations) {
            System.out.println("[RouteOptimizer] Optimizing route order.");
            List<Point> remaining = new ArrayList<>(destinations);
            List<Point> ordered = new ArrayList<>();
            Point current = start;
            while (!remaining.isEmpty()) {
                int nearestIndex = 0;
                double nearestDist = Double.MAX_VALUE;
                for (int i = 0; i < remaining.size(); i++) {
                    Point p = remaining.get(i);
                    double dist = Math.hypot(current.getX() - p.getX(), current.getY() - p.getY());
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearestIndex = i;
                    }
                }
                Point nextPoint = remaining.get(nearestIndex);
                System.out.println("[RouteOptimizer] Selected next point: " + nextPoint + " with distance: " + nearestDist);
                ordered.add(nextPoint);
                current = nextPoint;
                remaining.remove(nearestIndex);
            }
            System.out.println("[RouteOptimizer] Optimized order: " + ordered);
            return ordered;
        }
    }
}
