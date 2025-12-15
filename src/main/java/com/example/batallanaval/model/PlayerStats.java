package com.example.batallanaval.model;

/**
 * @author David AlejandroBravo Ospina
 * @version 1.0.0
 * @since 09-12-2025
 */

public class PlayerStats {

    private String nickname;
    private int playerSunk;
    private int enemySunk;

    public PlayerStats(String nickname, int playerSunk, int enemySunk) {
        this.nickname = nickname;
        this.playerSunk = playerSunk;
        this.enemySunk = enemySunk;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPlayerSunk() {
        return playerSunk;
    }

    public int getEnemySunk() {
        return enemySunk;
    }
}
