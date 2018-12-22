package com.ali.cs491.carbuds;

class User {
    public static String token;
    public static int user_id;
    public static String userType;
    public static String username;
    public static boolean isTripSetted;
    String nickname;
    String profileUrl;
    int userId;

    public User(String nickname, int userId) {
        this.nickname = nickname;
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}