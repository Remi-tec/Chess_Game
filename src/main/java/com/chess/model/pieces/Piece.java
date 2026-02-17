package com.chess.model.pieces;

import com.chess.model.Couleur;
import com.chess.model.Case;
import com.chess.model.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Piece {
    protected Couleur color;
    protected Case position;
    protected boolean hasMove = false;
    protected int moveCount = 0;
    protected List<Direction> typeOfMouvement;
    protected int moveRange;

    public Piece(Couleur color,Case position, List<Direction> typeOfMouvement, int moveRange) {
        this.color = color;
        this.position = position;
        this.typeOfMouvement = typeOfMouvement;
        this.moveRange = moveRange;
    }

    public Couleur getColor() {
        return color;
    }

    public List<Direction> getTypeOfMouvement() {
        return typeOfMouvement;
    }

    public int[][] getMergeTypeOfMouvement(List<Direction> listTypeOfMouvement) {
        return listTypeOfMouvement.stream()
                .flatMap(dir -> Arrays.stream(dir.getDirections()))
                .toArray(int[][]::new);
    }

    public Case getPosition() {
        return position;
    }

    public boolean getHasMove() {
        return hasMove;
    }

    public void setPosition(Case position) {
        this.position = position;
        this.hasMove = true;
        this.moveCount++;
    }

    public  boolean canMove(Case destination, Piece[][] board, Case[] tabPossibleMoves) {
        for (Case possibleMoves : tabPossibleMoves) {
            if (possibleMoves != null && possibleMoves.getRows() == destination.getRows() && possibleMoves.getColumns() == destination.getColumns()) {
                return true;
            }
        }
        return false;
    };

    public Case[] getPossibleMoves(Piece[][] board) {
        ArrayList<Case> possibleMoves = new ArrayList<>();
        int[][] directions = getMergeTypeOfMouvement(typeOfMouvement);
        int newRow;
        int newCol;

        for (int[] direction : directions) {
            int range = moveRange;
            newRow = position.getRows();
            newCol = position.getColumns();

            for (int step = 1; step <= range; step++) {
                newRow += direction[0];
                newCol += direction[1];

                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                    break;
                }

                Piece targetPiece = board[newRow][newCol];

                if (targetPiece == null) {
                    possibleMoves.add(new Case(newRow, newCol));
                } else {
                    if (targetPiece.getColor() != this.color) {
                        possibleMoves.add(new Case(newRow, newCol));
                    }
                    break;
                }
            }
        }

        return possibleMoves.toArray(new Case[0]);
    }
    public  int getMoveRange(){
        return moveRange;
    }
    public abstract String getNom();
}
