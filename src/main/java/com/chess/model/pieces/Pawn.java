package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Couleur color, Case position) {
        super(color, position, color == Couleur.BLACK ? Direction.BLACKPAWN : Direction.WHITEPAWN);
    }

    @Override
    public Case[] getPossibleMoves(Piece[][] board) {
        System.out.println("Hasmove: " + hasMove);
        List<Case> possibleMoves = new ArrayList<>();
        int[][] directions = this.typeOfMouvement.getDirections();

        for (int[] direction : directions) {
            int newRow = position.getRows() + direction[0];
            int newCol = position.getColumns() + direction[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece pieceAtDestination = board[newRow][newCol];

                // Moving forward ONE step
                if (direction[0] == 0 && Math.abs(direction[1]) == 1) {
                    if (pieceAtDestination == null) {
                        possibleMoves.add(new Case(newRow, newCol));
                    }
                }
                // Moving forward TWO steps (first move only)
                else if (direction[0] == 0 && Math.abs(direction[1]) == 2 && !this.hasMove) {
                    if (pieceAtDestination == null) {
                        // Vérifier aussi que la case intermédiaire est vide
                        int middleRow = position.getRows();
                        int middleCol = position.getColumns() + (direction[1] / 2);
                        if (board[middleRow][middleCol] == null) {
                            possibleMoves.add(new Case(newRow, newCol));
                        }
                    }
                }
                // Capturing (diagonal)
                else if (Math.abs(direction[0]) == 1 && Math.abs(direction[1]) == 1) {
                    if (pieceAtDestination != null && pieceAtDestination.getColor() != this.color) {
                        possibleMoves.add(new Case(newRow, newCol));
                    }
                }
            }
        }
        return possibleMoves.toArray(new Case[0]);
    }

    @Override
    public String getNom() {
        return "Pawn";
    }
}
