package com.example.batallanaval.model;

import java.io.*;

/**
 * @author David AlejandroBravo Ospina
 * @version 1.0.0
 * @since 09-12-2025
 */

public class PlayerStatsFile {

    private static final String FILE_NAME = "player_stats.txt";

    public static void save(String nickname, int playerSunk, int enemySunk) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write("nickname=" + nickname);
            writer.newLine();
            writer.write("playerSunk=" + playerSunk);
            writer.newLine();
            writer.write("enemySunk=" + enemySunk);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStats load() {

        String nickname = "Jugador";
        int playerSunk = 0;
        int enemySunk = 0;

        File file = new File(FILE_NAME);
        if (!file.exists()) return new PlayerStats(nickname, playerSunk, enemySunk);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("nickname="))
                    nickname = line.split("=")[1];

                if (line.startsWith("playerSunk="))
                    playerSunk = Integer.parseInt(line.split("=")[1]);

                if (line.startsWith("enemySunk="))
                    enemySunk = Integer.parseInt(line.split("=")[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PlayerStats(nickname, playerSunk, enemySunk);
    }
}
