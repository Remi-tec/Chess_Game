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
    protected boolean isStuck = false;
    protected int distance = 0;
    protected List<Case> moveHistoric = new ArrayList<>();
    protected int firstMove = -1;

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
        setPosition(position, -1);
    }

    public void setPosition(Case position, int moveNumber) {
        // Calculer la distance parcourue jusqu'à cette position
        if (this.position != null) {
            int deltaRow = Math.abs(position.getRows() - this.position.getRows());
            int deltaCol = Math.abs(position.getColumns() - this.position.getColumns());
            int moveDistance = Math.max(deltaRow, deltaCol);
            this.distance += moveDistance;
        }

        // Ajouter la nouvelle position à l'historique des mouvements
        this.moveHistoric.add(position);

        // Mettre à jour la position
        this.position = position;
        if (!this.hasMove && moveNumber >= 0) {
            this.firstMove = moveNumber;
        }
        this.hasMove = true;
        this.moveCount++;
    }

    public  boolean canMove(Case destination, Piece[][] board, List<Case> tabPossibleMoves) {
        for (Case possibleMoves : tabPossibleMoves) {
            if (possibleMoves != null && possibleMoves.getRows() == destination.getRows() && possibleMoves.getColumns() == destination.getColumns()) {
                return true;
            }
        }
        return false;
    };

    public Case[] getPossibleMoves(Piece[][] board) {
        return getPossibleMoves(board, -1);
    }

    public Case[] getPossibleMoves(Piece[][] board, int moveNumber) {
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

    public boolean getIsStuck() {
        return isStuck;
    }

    public void setIsStuck(boolean isStuck) {
        this.isStuck = isStuck;
    }

    public int getDistance() {
        return distance;
    }

    public int getFirstMove() {
        return firstMove;
    }

    public List<Case> getMoveHistoric() {
        return new ArrayList<>(moveHistoric);
    }

    public int getMoveHistoricSize() {
        return moveHistoric.size();
    }

    public Case getMoveHistoricAt(int index) {
        if (index >= 0 && index < moveHistoric.size()) {
            return moveHistoric.get(index);
        }
        return null;
    }

    public abstract String getNom();
}
