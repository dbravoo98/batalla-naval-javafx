package com.example.batallanaval.model;

import java.util.Random;

/**
 * @author David AlejandroBravo Ospina
 * @version 1.0.0
 * @since 09-12-2025
 */

public class RandomShotStrategy implements ShotStrategy {

    private final Random random = new Random();
    private static final int SIZE = 10;

    @Override
    public int[] nextShot(int[][] board) {

        int r, c;
        do {
            r = random.nextInt(SIZE);
            c = random.nextInt(SIZE);
        } while (board[r][c] >= 2); // evita disparos repetidos

        return new int[]{r, c};
    }
}
