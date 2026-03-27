package com.icia.delivery.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.member.UserProfile;
import com.icia.delivery.service.IpService;
import com.icia.delivery.util.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final IpService ipService;
    private final LoginHistoryService loginHistoryService;
    private final HttpServletRequest request;
    private final HttpSession session; // 세션 객체 주입

    @Getter
    @Value("${naver.client.id}")
    private String clientId; // 네이버 API 클라이언트 ID

    @Value("${naver.client.secret}")
    private String clientSecret; // 네이버 API 클라이언트 시크릿

    @Getter
    @Value("${naver.redirect.uri}")
    private String redirectUri; // 네이버 로그인 콜백 URI

    @Getter
    @Value("${google.client.id}")
    private String gClientId; // 구글 API 클라이언트 ID

    @Value("${google.client.secret}")
    private String gClientSecret; // 구글 API 클라이언트 시크릿

    @Getter
    @Value("${google.redirect.uri}")
    private String gRedirectUri; // 구글 로그인 콜백 URI

    @Getter
    @Value("${kakao.client.id}")
    private String kClientId; // 카카오 API 클라이언트 ID

    @Value("${kakao.client.secret}")
    private String kClientSecret; // 카카오 API 클라이언트 시크릿

    @Getter
    @Value("${kakao.redirect.uri}")
    private String kRedirectUri; // 카카오 로그인 콜백 URI

    /**
     * 네이버 API를 통해 액세스 토큰을 가져오는 메서드
     *
     * @param code  OAuth 인증 코드
     * @param state OAuth 상태 값
     * @return 액세스 토큰
     * @throws Exception 네이버 API 요청 중 오류가 발생했을 때
     */
    public String getAccessToken(String code, String state) throws Exception {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(tokenUrl);

        String params = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + code +
                "&state=" + state;

        post.setEntity(new StringEntity(params));
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = client.execute(post);
        InputStream body = response.getEntity().getContent();
        String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);
        String accessToken = node.get("access_token").asText();

        response.close();
        client.close();

        return accessToken;
    }

    /**
     * 네이버 API를 통해 사용자 프로필 정보를 가져오는 메서드
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 프로필 정보
     * @throws Exception 네이버 API 요청 중 오류가 발생했을 때
     */
    public UserProfile getUserProfile(String accessToken) throws Exception {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(userInfoUrl);

        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = client.execute(post);
        InputStream body = response.getEntity().getContent();
        String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody).get("response");

        UserProfile userProfile = new UserProfile();
        userProfile.setName(node.get("name").asText());
        userProfile.setEmail(node.get("email").asText());
        userProfile.setGender(node.get("gender").asText());
        userProfile.setBirthday(node.get("birthday").asText());
        userProfile.setBirthyear(node.get("birthyear").asText());
        userProfile.setMobile(node.get("mobile").asText());
        userProfile.setNickname(node.get("nickname").asText());

        response.close();
        client.close();

        return userProfile;
    }

    /**
     * 사용자 정보를 저장하거나 업데이트하는 메서드 (네이버)
     *
     * @param userProfile 네이버에서 가져온 사용자 프로필 정보
     * @return 저장 또는 업데이트된 사용자 엔티티
     */
    public MemberEntity saveOrUpdate(UserProfile userProfile) {
        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(userProfile.getEmail());
        String publicIp = ipService.getPublicIp();
        String deviceOs = UserAgentUtil.getDeviceOs(request);
        String browser = UserAgentUtil.getBrowser(request);

        if (optionalMember.isPresent()) {
            // 기존 사용자 정보 업데이트
            MemberEntity existingMember = optionalMember.get();
            existingMember.setLastLoginDate(LocalDateTime.now());
            existingMember.setLastLoginIp(publicIp);

            // 로그인 기록 추가
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setHisMid(existingMember.getMId());
            loginHistoryDTO.setHisLoginDate(LocalDateTime.now());
            loginHistoryDTO.setHisIpAddress(publicIp);
            loginHistoryDTO.setHisDeviceOs(deviceOs);
            loginHistoryDTO.setHisBrowser(browser);
            loginHistoryService.saveLoginHistory(loginHistoryDTO);

            // 세션 정보 업데이트
            setSessionAttributes(existingMember);

            return memberRepository.save(existingMember);
        } else {
            // 신규 사용자 회원가입 처리
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(userProfile.getEmail());
            memberDTO.setEmail(userProfile.getEmail());
            memberDTO.setUsername(userProfile.getName());
            memberDTO.setNickname(userProfile.getNickname());
            memberDTO.setPhone(userProfile.getMobile());
            memberDTO.setGender(userProfile.getGender().equals("M") ? "남성" : "여성");
            memberDTO.setRegisterIp(publicIp);
            memberDTO.setStatus("활성");
            memberDTO.setGrade("Welcome");
            memberDTO.setPoint(0L); // 초기 포인트 설정
            memberDTO.setOpenProfile("승인");
            memberDTO.setReceiveEmail("승인");
            memberDTO.setReceiveNotify("승인");
            memberDTO.setLoginType("네이버");
            memberDTO.setRegisterDate(LocalDateTime.now());
            memberDTO.setPassword("OAUTH_USER");
            memberDTO.setLastLoginDate(LocalDateTime.now()); // 현재 시간 설정
            memberDTO.setLastLoginIp(publicIp); // 현재 IP 설정

            // 생년월일 처리
            String birthyear = userProfile.getBirthyear();
            String birthday = userProfile.getBirthday();
            LocalDate birthdayDate = (birthyear != null && !birthyear.isEmpty() && birthday != null && !birthday.isEmpty())
                    ? LocalDate.parse(birthyear + "-" + birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : LocalDate.of(1970, 1, 1);
            memberDTO.setBirthday(birthdayDate); // LocalDate로 설정

            // MemberDTO를 MemberEntity로 변환
            MemberEntity entity = MemberEntity.toEntity(memberDTO);
            MemberEntity savedMember = memberRepository.save(entity);

            // 신규 사용자 로그인 기록 추가
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setHisMid(savedMember.getMId());
            loginHistoryDTO.setHisLoginDate(LocalDateTime.now());
            loginHistoryDTO.setHisIpAddress(publicIp);
            loginHistoryDTO.setHisDeviceOs(deviceOs);
            loginHistoryDTO.setHisBrowser(browser);
            loginHistoryService.saveLoginHistory(loginHistoryDTO);

            // 세션에 사용자 정보 저장
            setSessionAttributes(savedMember);

            return savedMember;
        }
    }

    /**
     * 구글 API를 통해 액세스 토큰을 가져오는 메서드
     *
     * @param code OAuth 인증 코드
     * @return 액세스 토큰
     * @throws Exception 구글 API 요청 중 오류가 발생했을 때
     */
    public String getGAccessToken(String code) throws Exception {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(tokenUrl);

        String params = "grant_type=authorization_code" +
                "&client_id=" + gClientId +
                "&client_secret=" + gClientSecret +
                "&code=" + code +
                "&redirect_uri=" + gRedirectUri; // Google 리디렉트 URI 설정

        post.setEntity(new StringEntity(params));
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = client.execute(post);
        InputStream body = response.getEntity().getContent();
        String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);
        String accessToken = node.get("access_token").asText();

        response.close();
        client.close();

        return accessToken;
    }

    /**
     * 구글 API를 통해 사용자 프로필 정보를 가져오는 메서드
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 프로필 정보
     * @throws Exception 구글 API 요청 중 오류가 발생했을 때
     */
    public UserProfile getGUserProfile(String accessToken) throws Exception {
        // Google People API 요청 URL
        String userInfoUrl = "https://people.googleapis.com/v1/people/me?personFields=names,emailAddresses,birthdays,genders,phoneNumbers";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(userInfoUrl);

        // Authorization 헤더 추가
        get.setHeader("Authorization", "Bearer " + accessToken);

        CloseableHttpResponse response = client.execute(get);
        InputStream body = response.getEntity().getContent();
        String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);

        // UserProfile 생성 및 데이터 매핑
        UserProfile userProfile = new UserProfile();

        // 이름 정보 가져오기
        JsonNode namesNode = node.get("names");
        if (namesNode != null && namesNode.isArray() && namesNode.size() > 0) {
            JsonNode primaryName = namesNode.get(0);
            userProfile.setName(primaryName.get("displayName").asText());
        }

        // 이메일 정보 가져오기
        JsonNode emailNode = node.get("emailAddresses");
        if (emailNode != null && emailNode.isArray() && emailNode.size() > 0) {
            JsonNode primaryEmail = emailNode.get(0);
            userProfile.setEmail(primaryEmail.get("value").asText());
        }

        // 생일 정보 가져오기
        JsonNode birthdaysNode = node.get("birthdays");
        if (birthdaysNode != null && birthdaysNode.isArray() && birthdaysNode.size() > 0) {
            JsonNode primaryBirthday = birthdaysNode.get(0).get("date");
            if (primaryBirthday != null) {
                String birthyear = primaryBirthday.has("year") ? primaryBirthday.get("year").asText() : "1970";
                String month = primaryBirthday.has("month") ? String.format("%02d", primaryBirthday.get("month").asInt()) : "01";
                String day = primaryBirthday.has("day") ? String.format("%02d", primaryBirthday.get("day").asInt()) : "01";

                userProfile.setBirthyear(birthyear);
                userProfile.setBirthday(month + "-" + day);
            }
        }

        // 성별 정보 가져오기
        JsonNode gendersNode = node.get("genders");
        if (gendersNode != null && gendersNode.isArray() && gendersNode.size() > 0) {
            JsonNode primaryGender = gendersNode.get(0);
            userProfile.setGender(primaryGender.get("value").asText());
        }

        // 전화번호 정보 가져오기 (선택적으로 설정해야 함)
        JsonNode phoneNumbersNode = node.get("phoneNumbers");
        if (phoneNumbersNode != null && phoneNumbersNode.isArray() && phoneNumbersNode.size() > 0) {
            JsonNode primaryPhoneNumber = phoneNumbersNode.get(0);
            userProfile.setMobile(primaryPhoneNumber.get("value").asText());
        }

        response.close();
        client.close();

        return userProfile;
    }

    /**
     * 사용자 정보를 저장하거나 업데이트하는 메서드 (구글)
     *
     * @param userProfile 구글에서 가져온 사용자 프로필 정보
     * @return 저장 또는 업데이트된 사용자 엔티티
     */
    public MemberEntity GsaveOrUpdate(UserProfile userProfile) {
        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(userProfile.getEmail());
        String publicIp = ipService.getPublicIp();
        String deviceOs = UserAgentUtil.getDeviceOs(request);
        String browser = UserAgentUtil.getBrowser(request);

        if (optionalMember.isPresent()) {
            // 기존 사용자 정보 업데이트
            MemberEntity existingMember = optionalMember.get();
            existingMember.setLastLoginDate(LocalDateTime.now());
            existingMember.setLastLoginIp(publicIp);
            MemberEntity updatedMember = memberRepository.save(existingMember);

            // 로그인 기록 추가
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setHisMid(existingMember.getMId());
            loginHistoryDTO.setHisLoginDate(LocalDateTime.now());
            loginHistoryDTO.setHisIpAddress(publicIp);
            loginHistoryDTO.setHisDeviceOs(deviceOs);
            loginHistoryDTO.setHisBrowser(browser);
            loginHistoryService.saveLoginHistory(loginHistoryDTO);

            // 세션 정보 업데이트
            setSessionAttributes(updatedMember);

            return updatedMember;
        } else {
            // 신규 사용자 회원가입 처리
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(userProfile.getEmail());
            memberDTO.setEmail(userProfile.getEmail());
            memberDTO.setUsername(userProfile.getName());
            memberDTO.setNickname(userProfile.getNickname());
            memberDTO.setPhone(userProfile.getMobile());
            memberDTO.setGender(userProfile.getGender().equals("M") ? "남성" : "여성");
            memberDTO.setRegisterIp(publicIp);
            memberDTO.setStatus("활성");
            memberDTO.setGrade("Welcome");
            memberDTO.setPoint(0L); // 초기 포인트 설정
            memberDTO.setOpenProfile("승인");
            memberDTO.setReceiveEmail("승인");
            memberDTO.setReceiveNotify("승인");
            memberDTO.setLoginType("구글");
            memberDTO.setRegisterDate(LocalDateTime.now());
            memberDTO.setPassword("OAUTH_USER");
            memberDTO.setLastLoginDate(LocalDateTime.now()); // 현재 시간 설정
            memberDTO.setLastLoginIp(publicIp); // 현재 IP 설정

            // 생년월일 설정
            String birthyear = userProfile.getBirthyear();
            String birthday = userProfile.getBirthday();
            LocalDate birthdayDate = (birthyear != null && !birthyear.isEmpty() && birthday != null && !birthday.isEmpty())
                    ? LocalDate.parse(birthyear + "-" + birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : LocalDate.of(1970, 1, 1);
            memberDTO.setBirthday(birthdayDate); // LocalDate로 설정

            // MemberDTO를 MemberEntity로 변환
            MemberEntity entity = MemberEntity.toEntity(memberDTO);
            MemberEntity savedMember = memberRepository.save(entity);

            // 신규 사용자 로그인 기록 추가
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setHisMid(savedMember.getMId());
            loginHistoryDTO.setHisLoginDate(LocalDateTime.now());
            loginHistoryDTO.setHisIpAddress(publicIp);
            loginHistoryDTO.setHisDeviceOs(deviceOs);
            loginHistoryDTO.setHisBrowser(browser);
            loginHistoryService.saveLoginHistory(loginHistoryDTO);

            // 세션에 사용자 정보 저장
            setSessionAttributes(savedMember);

            return savedMember;
        }
    }

    /**
     * 카카오 API를 통해 액세스 토큰을 가져오는 메서드
     *
     * @param code OAuth 인증 코드
     * @return 액세스 토큰
     * @throws Exception 카카오 API 요청 중 오류가 발생했을 때
     */
    public String getKAccessToken(String code) throws Exception {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(tokenUrl);

        String params = "grant_type=authorization_code" +
                "&client_id=" + kClientId + // 카카오 REST API 앱 키
                "&client_secret=" + kClientSecret + // 선택: 앱 설정에 클라이언트 시크릿을 등록한 경우
                "&code=" + code +
                "&redirect_uri=" + kRedirectUri; // 카카오 리디렉트 URI 설정

        post.setEntity(new StringEntity(params));
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = client.execute(post);
        InputStream body = response.getEntity().getContent();
        String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);
        String accessToken = node.get("access_token").asText();

        response.close();
        client.close();

        return accessToken;
    }

    /**
     * 카카오 API를 통해 사용자 프로필 정보를 가져오는 메서드
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 프로필 정보
     * @throws Exception 카카오 API 요청 중 오류가 발생했을 때
     */
    public UserProfile getKUserProfile(String accessToken) throws Exception {
        // 카카오 사용자 정보 요청 URL
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(userInfoUrl);

        // Authorization 헤더 추가
        get.setHeader("Authorization", "Bearer " + accessToken);

        CloseableHttpResponse response = client.execute(get);
        InputStream body = response.getEntity().getContent();
        String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);

        // UserProfile 생성 및 데이터 매핑
        UserProfile userProfile = new UserProfile();

        // 이름 정보 가져오기
        JsonNode kakaoAccountNode = node.get("kakao_account");
        if (kakaoAccountNode != null) {
            JsonNode profileNode = kakaoAccountNode.get("profile");
            if (profileNode != null) {
                userProfile.setName(profileNode.get("nickname").asText());
            }

            // 이메일 정보 가져오기
            if (kakaoAccountNode.has("email")) {
                userProfile.setEmail(kakaoAccountNode.get("email").asText());
            }

            // 성별 정보 가져오기
            if (kakaoAccountNode.has("gender")) {
                String gender = kakaoAccountNode.get("gender").asText();
                userProfile.setGender(gender.equals("male") ? "남성" : "여성");
            }


            // 생년월일 정보 가져오기
            if (kakaoAccountNode.has("birthyear")) {
                userProfile.setBirthyear(kakaoAccountNode.get("birthyear").asText());
            }
            if (kakaoAccountNode.has("birthday")) {
                userProfile.setBirthday(kakaoAccountNode.get("birthday").asText()); // MM-DD 형식
            }
        }

        // 전화번호 정보 가져오기 (선택적으로 설정해야 함)
        if (kakaoAccountNode != null && kakaoAccountNode.has("phone_number")) {
            userProfile.setMobile(kakaoAccountNode.get("phone_number").asText());
        }

        response.close();
        client.close();

        return userProfile;
    }

    /**
     * 사용자 정보를 저장하거나 업데이트하는 메서드 (카카오)
     *
     * @param userProfile 카카오에서 가져온 사용자 프로필 정보
     * @return 저장 또는 업데이트된 사용자 엔티티
     */
    public MemberEntity KsaveOrUpdate(UserProfile userProfile) {
        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(userProfile.getEmail());
        String publicIp = ipService.getPublicIp();
        String deviceOs = UserAgentUtil.getDeviceOs(request);
        String browser = UserAgentUtil.getBrowser(request);

        if (optionalMember.isPresent()) {
            // 기존 사용자 정보 업데이트
            MemberEntity existingMember = optionalMember.get();
            existingMember.setLastLoginDate(LocalDateTime.now());
            existingMember.setLastLoginIp(publicIp);
            MemberEntity updatedMember = memberRepository.save(existingMember);

            // 로그인 기록 추가
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setHisMid(existingMember.getMId());
            loginHistoryDTO.setHisLoginDate(LocalDateTime.now());
            loginHistoryDTO.setHisIpAddress(publicIp);
            loginHistoryDTO.setHisDeviceOs(deviceOs);
            loginHistoryDTO.setHisBrowser(browser);
            loginHistoryService.saveLoginHistory(loginHistoryDTO);

            // 세션 정보 업데이트
            setSessionAttributes(updatedMember);

            return updatedMember;
        } else {
            // 신규 사용자 회원가입 처리
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(userProfile.getEmail());
            memberDTO.setEmail(userProfile.getEmail());
            memberDTO.setUsername(userProfile.getName());
            memberDTO.setNickname(userProfile.getNickname());
            memberDTO.setPhone(userProfile.getMobile());
            memberDTO.setGender(userProfile.getGender());
            memberDTO.setRegisterIp(publicIp);
            memberDTO.setStatus("활성");
            memberDTO.setGrade("Welcome");
            memberDTO.setPoint(0L); // 초기 포인트 설정
            memberDTO.setOpenProfile("승인");
            memberDTO.setReceiveEmail("승인");
            memberDTO.setReceiveNotify("승인");
            memberDTO.setLoginType("카카오");
            memberDTO.setRegisterDate(LocalDateTime.now());
            memberDTO.setPassword("OAUTH_USER");
            memberDTO.setLastLoginDate(LocalDateTime.now()); // 현재 시간 설정
            memberDTO.setLastLoginIp(publicIp); // 현재 IP 설정

            // 생년월일 설정
            String birthyear = userProfile.getBirthyear();
            String birthday = userProfile.getBirthday();
            LocalDate birthdayDate = (birthyear != null && !birthyear.isEmpty() && birthday != null && !birthday.isEmpty())
                    ? LocalDate.parse(birthyear + "-" + birthday.substring(0, 2) + "-" + birthday.substring(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : LocalDate.of(1970, 1, 1);
            memberDTO.setBirthday(birthdayDate); // LocalDate로 설정

            // MemberDTO를 MemberEntity로 변환
            MemberEntity entity = MemberEntity.toEntity(memberDTO);
            MemberEntity savedMember = memberRepository.save(entity);

            // 신규 사용자 로그인 기록 추가
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setHisMid(savedMember.getMId());
            loginHistoryDTO.setHisLoginDate(LocalDateTime.now());
            loginHistoryDTO.setHisIpAddress(publicIp);
            loginHistoryDTO.setHisDeviceOs(deviceOs);
            loginHistoryDTO.setHisBrowser(browser);
            loginHistoryService.saveLoginHistory(loginHistoryDTO);

            // 세션에 사용자 정보 저장
            setSessionAttributes(savedMember);

            return savedMember;
        }
    }

    /**
     * 세션에 사용자 정보를 설정하는 메서드
     *
     * @param member 회원 엔티티
     */
    private void setSessionAttributes(MemberEntity member) {
        session.setAttribute("mem_id", member.getMId());
        session.setAttribute("mem_userid", member.getUserId());
        session.setAttribute("mem_email", member.getEmail());
        session.setAttribute("mem_username", member.getUsername());
        session.setAttribute("mem_address", member.getAddress());
        session.setAttribute("mem_nickname", member.getNickname());
        session.setAttribute("mem_grade", member.getGrade());
        session.setAttribute("mem_status", member.getStatus());
    }
}
