package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Couleur color, Case position) {
        super(color, position, Direction.ALL);
    }

    @Override
    public Case[] getPossibleMoves(Piece[][] board) {
        List<Case> possibleMoves = new ArrayList<>();
        int[][] directions = this.typeOfMouvement.getDirections();

        for (int[] direction : directions) {
            int newRow = position.getRows() + direction[0];
            int newCol = position.getColumns() + direction[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece pieceAtDestination = board[newRow][newCol];
                if (pieceAtDestination == null || pieceAtDestination.getColor() != this.color) {
                    possibleMoves.add(new Case(newRow, newCol));
                }
            }
        }

        return possibleMoves.toArray(new Case[0]);
    }

    @Override
    public String getNom(){
        return "King";
    }
}
