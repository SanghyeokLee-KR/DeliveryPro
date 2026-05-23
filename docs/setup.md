# DeliveryPro Setup

이 문서는 리팩토링 전 P0 안정화를 위한 로컬 실행 환경 설정 가이드입니다.

## Required Runtime

- JDK 21
- Gradle Wrapper: `./gradlew` 또는 `gradlew.bat`
- Oracle DB XE 또는 호환 가능한 Oracle 인스턴스

현재 프로젝트의 `build.gradle`은 Java 21 toolchain을 사용합니다. 로컬에서 `JAVA_HOME`이 JDK 21 설치 경로를 가리켜야 합니다.

## Windows JAVA_HOME Example

PowerShell에서 현재 터미널에만 적용하려면 다음처럼 설정합니다.

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java -version
```

영구 설정은 Windows 환경 변수에서 `JAVA_HOME`을 JDK 21 경로로 등록하고, `Path`에 `%JAVA_HOME%\bin`을 추가합니다.

## Required Environment Variables

애플리케이션 실행 전에 아래 환경 변수를 설정해야 합니다.

```powershell
$env:DB_URL="jdbc:oracle:thin:@<host>:<port>/<service_name>"
$env:DB_USERNAME="your_oracle_username"
$env:DB_PASSWORD="your_oracle_password"

$env:MAIL_USERNAME="your_gmail_address"
$env:MAIL_PASSWORD="your_gmail_app_password"

$env:NAVER_CLIENT_ID="your_naver_client_id"
$env:NAVER_CLIENT_SECRET="your_naver_client_secret"

$env:GOOGLE_CLIENT_ID="your_google_client_id"
$env:GOOGLE_CLIENT_SECRET="your_google_client_secret"

$env:KAKAO_CLIENT_ID="your_kakao_client_id"
$env:KAKAO_CLIENT_SECRET="your_kakao_client_secret"
$env:KAKAO_API_KEY="your_kakao_rest_api_key"
$env:KAKAO_JAVASCRIPT_KEY="your_kakao_javascript_key"

```

## Local Run

Oracle DB가 실행 중이고 환경 변수가 준비되면 다음 명령으로 실행합니다.

```powershell
.\gradlew.bat bootRun
```

기본 포트는 `9090`입니다.

```text
http://localhost:9090
```

## Build Verification

코드 변경 없이 빌드 가능 여부만 확인하려면 다음 명령을 사용합니다.

```powershell
.\gradlew.bat compileJava
```

테스트까지 확인하려면 다음 명령을 사용합니다.

```powershell
.\gradlew.bat test
```

현재 저장소의 테스트는 기본 Spring Context 로딩 테스트 1개뿐이므로, 리팩토링 전에는 핵심 도메인 테스트를 추가하는 것을 권장합니다.

## Secret Handling

민감 정보는 Git에 커밋하지 않습니다.

- DB 계정: `DB_USERNAME`, `DB_PASSWORD`
- Mail 계정: `MAIL_USERNAME`, `MAIL_PASSWORD`
- OAuth Secret: `NAVER_CLIENT_SECRET`, `GOOGLE_CLIENT_SECRET`, `KAKAO_CLIENT_SECRET`
- Kakao REST API Key: `KAKAO_API_KEY`
- Kakao JavaScript Key: `KAKAO_JAVASCRIPT_KEY`

이미 노출된 API 키는 서비스 제공자 콘솔에서 폐기하고 새 키로 교체하는 것이 안전합니다.
