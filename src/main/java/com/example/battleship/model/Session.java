package com.example.battleship.model;

public class Session {
    private static String currentNickname;

    public static String getCurrentNickname() {
        return currentNickname;
    }

    public static void setCurrentNickname(String nickname) {
        currentNickname = nickname;
    }
}