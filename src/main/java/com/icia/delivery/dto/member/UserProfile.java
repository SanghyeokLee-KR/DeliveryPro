package com.icia.delivery.dto.member;


import lombok.Data;


@Data
public class UserProfile {
    private String name;
    private String email;
    private String gender;
    private String birthday;  // MM-DD 형태
    private String birthyear; // YYYY 형태
    private String mobile;
    private String nickname;
}
