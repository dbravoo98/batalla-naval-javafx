package com.example.batallanaval.controller;

import com.example.batallanaval.model.*;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.*;

/**
 * @author David AlejandroBravo Ospina
 * @version 1.0.0
 * @since 09-12-2025
 */

public class BNSecondController {

    /* ===================== FXML ===================== */
    @FXML private Pane ShipsPane;
    @FXML private GridPane BoardGrid;
    @FXML private GridPane EnemyGrid;
    @FXML private Label nicknameLabel;

    /* ===================== CONSTANTES ===================== */
    private static final int SIZE = 10;
    private static final double CELL = 35;
    private final int[] SHIPS = {4,3,3,2,2,2,1,1,1,1};

    /* ===================== TABLEROS ===================== */
    private Rectangle[][] playerCells = new Rectangle[SIZE][SIZE];
    private Rectangle[][] enemyCells  = new Rectangle[SIZE][SIZE];

    private int[][] playerBoard = new int[SIZE][SIZE];
    private int[][] enemyBoard  = new int[SIZE][SIZE];

    private int[][] playerShipId = new int[SIZE][SIZE];
    private int[][] enemyShipId  = new int[SIZE][SIZE];

    private int[] playerShipHealth = new int[11];
    private int[] enemyShipHealth  = new int[11];

    private int nextPlayerShipId = 1;
    private int nextEnemyShipId  = 1;

    /* ===================== ESTADO ===================== */
    private boolean placementPhase = true;
    private boolean playerTurn = true;

    /* ===================== MODELO ===================== */
    private BN model = new BN();
    private ShotStrategy shotStrategy = new RandomShotStrategy();

    private String nickname = "Jugador";

    /* ===================== INIT ===================== */
    public void initGame(String nickname, boolean cargar) {

        this.nickname = nickname;
        nicknameLabel.setText("Jugador: " + nickname);

        createPlayerBoard();
        createEnemyBoard();

        if (cargar && loadGameIfExists()) {
            // ✅ Partida cargada → ya puede jugar
            placementPhase = false;
            playerTurn = true;
        } else {
            // 🆕 Juego nuevo
            placeEnemyShips();
            createPlayerShips();
        }
    }

    /* ===================== TABLEROS ===================== */
    private void createPlayerBoard() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                Rectangle cell = new Rectangle(CELL, CELL);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.BLACK);
                playerCells[r][c] = cell;
                BoardGrid.add(cell, c, r);
            }
    }

    private void createEnemyBoard() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {

                Rectangle cell = new Rectangle(CELL, CELL);
                cell.setFill(Color.DARKBLUE);
                cell.setStroke(Color.BLACK);

                final int row = r, col = c;
                cell.setOnMouseClicked(e -> {
                    if (placementPhase || !playerTurn) return;
                    if (enemyBoard[row][col] >= 2) return;
                    playerShoot(row, col);
                });

                enemyCells[r][c] = cell;
                EnemyGrid.add(cell, c, r);
            }
    }

    /* ===================== BARCOS JUGADOR ===================== */
    private void createPlayerShips() {

        ShipsPane.getChildren().clear();
        double x = 20, y = 20;
        int index = 0;

        for (int size : SHIPS) {
            Rectangle ship = new Rectangle(size * CELL, CELL);
            ship.setFill(Color.GRAY);
            ship.setStroke(Color.BLACK);

            ship.setLayoutX(x + (index % 4) * 150);
            ship.setLayoutY(y + (index / 4) * 50);

            makeShipDraggable(ship, size);
            ShipsPane.getChildren().add(ship);
            index++;
        }
    }

    private void makeShipDraggable(Rectangle ship, int size) {

        final double[] offset = new double[2];

        ship.setOnMousePressed(e -> {
            offset[0] = ship.getLayoutX() - e.getSceneX();
            offset[1] = ship.getLayoutY() - e.getSceneY();
        });

        ship.setOnMouseDragged(e -> {
            ship.setLayoutX(e.getSceneX() + offset[0]);
            ship.setLayoutY(e.getSceneY() + offset[1]);
        });

        ship.setOnMouseReleased(e -> snapShip(ship, size));

        ship.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                ship.setRotate(ship.getRotate() == 0 ? 90 : 0);
        });
    }

    private void snapShip(Rectangle ship, int size) {

        double gx = BoardGrid.localToScene(0,0).getX();
        double gy = BoardGrid.localToScene(0,0).getY();
        double sx = ship.localToScene(0,0).getX();
        double sy = ship.localToScene(0,0).getY();

        int col = (int)((sx - gx) / CELL);
        int row = (int)((sy - gy) / CELL);
        boolean horizontal = ship.getRotate() == 0;

        if (!canPlace(row, col, size, horizontal)) return;

        int id = nextPlayerShipId++;
        playerShipHealth[id] = size;

        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row+i;
            int c = horizontal ? col+i : col;
            playerBoard[r][c] = 1;
            playerShipId[r][c] = id;
            playerCells[r][c].setFill(Color.DARKGREEN);
        }

        ShipsPane.getChildren().remove(ship);
        if (nextPlayerShipId > 10) placementPhase = false;
        saveGame();
    }

    private boolean canPlace(int row, int col, int size, boolean h) {
        for (int i = 0; i < size; i++) {
            int r = h ? row : row+i;
            int c = h ? col+i : col;
            if (r<0||c<0||r>=SIZE||c>=SIZE) return false;
            if (playerBoard[r][c]==1) return false;
        }
        return true;
    }

    /* ===================== ENEMIGO ===================== */
    private void placeEnemyShips() {
        for (int s : SHIPS) {
            while (true) {
                int r = (int)(Math.random()*SIZE);
                int c = (int)(Math.random()*SIZE);
                boolean h = Math.random()<0.5;
                if (!canEnemyPlace(r,c,s,h)) continue;

                int id = nextEnemyShipId++;
                enemyShipHealth[id]=s;

                for (int i=0;i<s;i++){
                    int rr=h?r:r+i, cc=h?c+i:c;
                    enemyBoard[rr][cc]=1;
                    enemyShipId[rr][cc]=id;
                }
                break;
            }
        }
    }

    private boolean canEnemyPlace(int r,int c,int s,boolean h){
        for(int i=0;i<s;i++){
            int rr=h?r:r+i, cc=h?c+i:c;
            if(rr<0||cc<0||rr>=SIZE||cc>=SIZE) return false;
            if(enemyBoard[rr][cc]==1) return false;
        }
        return true;
    }

    /* ===================== DISPAROS ===================== */
    private void playerShoot(int r,int c){
        Rectangle cell = enemyCells[r][c];
        if(enemyBoard[r][c]==1){
            enemyBoard[r][c]=3;
            cell.setFill(Color.RED);
            saveGame();
            return;
        }
        enemyBoard[r][c]=2;
        cell.setFill(Color.GRAY);
        saveGame();
        playerTurn=false;
        machineTurn();
    }

    private void machineTurn(){
        PauseTransition p=new PauseTransition(Duration.seconds(1));
        p.setOnFinished(e->{
            int[] s=shotStrategy.nextShot(playerBoard);
            int r=s[0],c=s[1];
            Rectangle cell=playerCells[r][c];
            if(playerBoard[r][c]==1){
                playerBoard[r][c]=3;
                cell.setFill(Color.RED);
                saveGame();
                machineTurn();
                return;
            }
            playerBoard[r][c]=2;
            cell.setFill(Color.LIGHTGRAY);
            saveGame();
            playerTurn=true;
        });
        p.play();
    }

    /* ===================== SAVE / LOAD ===================== */
    private void saveGame() {
        try(ObjectOutputStream o=new ObjectOutputStream(
                new FileOutputStream("batallanaval.save"))){

            o.writeObject(playerBoard);
            o.writeObject(enemyBoard);
            o.writeObject(playerShipId);
            o.writeObject(enemyShipId);
            o.writeObject(playerShipHealth);
            o.writeObject(enemyShipHealth);
            o.writeInt(nextPlayerShipId);
            o.writeInt(nextEnemyShipId);
            o.writeBoolean(playerTurn);
            o.writeBoolean(placementPhase);

        }catch(Exception e){ model.ErrorAlert(); }
    }

    private boolean loadGameIfExists() {

        File file = new File("batallanaval.save");
        if (!file.exists()) return false;

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {

            playerBoard = (int[][]) in.readObject();
            enemyBoard  = (int[][]) in.readObject();
            playerShipId = (int[][]) in.readObject();
            enemyShipId  = (int[][]) in.readObject();
            playerShipHealth = (int[]) in.readObject();
            enemyShipHealth  = (int[]) in.readObject();
            nextPlayerShipId = in.readInt();
            nextEnemyShipId  = in.readInt();

            redrawBoards();
            ShipsPane.getChildren().clear();
            return true;

        } catch (Exception e) {
            model.ErrorAlert();
            return false;
        }
    }

    /* ===================== REDRAW ===================== */
    private void redrawBoards() {
        for (int r=0;r<SIZE;r++)
            for(int c=0;c<SIZE;c++){
                if(playerBoard[r][c]==1) playerCells[r][c].setFill(Color.DARKGREEN);
                if(playerBoard[r][c]==2) playerCells[r][c].setFill(Color.LIGHTGRAY);
                if(playerBoard[r][c]==3) playerCells[r][c].setFill(Color.RED);

                if(enemyBoard[r][c]==2) enemyCells[r][c].setFill(Color.GRAY);
                if(enemyBoard[r][c]==3) enemyCells[r][c].setFill(Color.RED);
            }
    }

    @FXML
    private void InstructionsClick(){ model.ShowInstructions(); }

    @FXML
    private void MostrarTableroClick() {
        for (int r=0;r<SIZE;r++)
            for(int c=0;c<SIZE;c++)
                if(enemyBoard[r][c]==1)
                    enemyCells[r][c].setFill(Color.DARKSEAGREEN);
    }
}
