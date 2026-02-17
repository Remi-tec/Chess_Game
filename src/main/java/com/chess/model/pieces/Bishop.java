package com.chess.model.pieces;

import com.chess.model.Couleur;
import com.chess.model.Case;
import com.chess.model.Direction;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece{

    public Bishop(Couleur color, Case position) {
        super(color, position,Direction.DIAGONAL);
    }

    @Override
    public String getNom() {
        return "Bishop";
    }

    @Override
    public Case[] getPossibleMoves(Piece[][] board) {
        List<Case> possibleMoves = new ArrayList<>();
        int newRow;
        int newCol;
        int[][] directions = this.typeOfMouvement.getDirections();
        boolean move;
        for (int[] direction : directions) {
            newRow = position.getRows();
            newCol = position.getColumns();
            move = true;
            while (move) {
                newRow += direction[0];
                newCol += direction[1];
                if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                    if (board[newRow][newCol] == null) {
                        possibleMoves.add(new Case(newRow, newCol));
                    } else {
                        if (board[newRow][newCol].getColor() != this.color) {
                            possibleMoves.add(new Case(newRow, newCol));
                        }
                        move = false;
                    }
                } else {
                    move = false;
                }

            }
        }
        return possibleMoves.toArray(new Case[0]);
    }
}
