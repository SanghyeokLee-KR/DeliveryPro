임시입니다. 포폴 준비 할때 한거 수정 예정

# 🚚 DeliveryPro

Spring Boot 기반 음식 배달 플랫폼 프로젝트입니다.
사용자 주문, 라이더 배달, 소셜 로그인, 메일 전송, 지도 기능 등을 포함한 웹 애플리케이션입니다.

---

## 📌 Overview

DeliveryPro는 음식 주문부터 배달까지의 전체 흐름을 구현한 프로젝트입니다.
회원, 주문, 라이더, 인증, 외부 API 연동 기능을 포함하고 있습니다.

---

## 🛠 Tech Stack

### Backend

* Java 21
* Spring Boot 3.4
* Spring Data JPA
* Spring Security
* Spring Validation

### Frontend

* Thymeleaf

### Database

* Oracle DB

### Others

* Gradle
* WebSocket
* OAuth2 Client (Google / Naver / Kakao)
* Java Mail Sender

---

## ✨ Features

### 👤 User

* 회원가입 / 로그인
* 소셜 로그인 (Google, Naver, Kakao)

### 🛒 Order

* 음식 주문 생성
* 주문 내역 조회
* 주문 상태 관리

### 🏍 Rider

* 배달 상태 관리
* 라이더 관련 기능

### 📍 Map

* 지도 기반 위치 기능

### 📧 Mail

* 이메일 인증 및 발송

---

## 📂 Project Structure

```
src/main/java/com/icia/delivery
 ┣ controller
 ┃ ┣ member
 ┃ ┣ rider
 ┃ ┣ map
 ┃ ┗ admin
 ┣ service
 ┣ repository
 ┣ entity
 ┣ dto
 ┗ config
```

---

## ⚙️ Configuration

이 프로젝트는 보안을 위해 민감 정보를 외부 설정으로 분리합니다.

### Required Environment Variables

* DB_USERNAME
* DB_PASSWORD
* MAIL_USERNAME
* MAIL_PASSWORD
* NAVER_CLIENT_ID / NAVER_CLIENT_SECRET
* GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET
* KAKAO_CLIENT_ID / KAKAO_CLIENT_SECRET
* KAKAO_API_KEY
* IPIFY_API_URL

---

## 🚀 Run

1. JDK 21 설치
2. Oracle DB 실행
3. 환경 변수 설정 또는 `application-local.properties` 생성
4. 실행

```
./gradlew bootRun
```

---

## 🧪 Troubleshooting

### 1. Controller Bean 충돌

* 동일한 이름의 Controller 클래스가 존재할 경우 충돌 발생
* 해결: 클래스명 변경 (예: `MapController` → `RiderMapController`)

### 2. Gradle / Spring Boot 버전 충돌

* Spring Boot 3.4 사용 시 Gradle 8.14 이상 필요

### 3. Lombok @Builder 경고

* 기본값 유지 시 `@Builder.Default` 사용 필요

---

## 🔒 Security Notice

비밀번호, API 키, OAuth Secret 등 민감 정보는
GitHub에 포함되어 있지 않으며, 환경 변수로 관리됩니다.

---

## 📌 Future Improvements

* REST API 구조로 리팩토링
* JWT 기반 인증 적용
* Docker 및 배포 환경 구성
* 프론트엔드 분리 (React/Vue)

---

## 👨‍💻 Author

* Sanghyeok Lee
