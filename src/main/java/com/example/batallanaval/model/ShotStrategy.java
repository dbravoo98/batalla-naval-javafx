package com.example.batallanaval.model;

public interface ShotStrategy {

    /**
     * @author David AlejandroBravo Ospina
     * @version 1.0.0
     * @since 09-12-2025
     * Determines the next shot coordinates for the machine.
     *
     * @param board current player board
     * @return int array {row, col}
     */
    int[] nextShot(int[][] board);
}
