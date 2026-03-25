package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.ArrayList;
import java.util.Arrays;

public class King extends Piece {

    public King(Couleur color, Case position) {
        super(color, position, Arrays.asList(Direction.DIAGONAL,Direction.HORIZONTAL),1);
    }

    @Override
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
        if (!this.hasMove) {
            for (int i = -1; i < 2; i += 2) {
                newRow = position.getRows();
                newCol = position.getColumns();

                while (true) {
                    newRow += i;
                    // Stop if we leave the board.
                    if (newRow < 0 || newRow >= 8) {
                        break;
                    }
                    Piece targetPiece = board[newRow][newCol];
                    // Empty squares between king and rook are allowed.
                    if (targetPiece == null) {
                        continue;
                    }
                    // Valid castling rook found.
                    if (targetPiece instanceof Rook && targetPiece.getColor() == this.color && !targetPiece.hasMove) {
                        possibleMoves.add(new Case(position.getRows() + (i * 2), position.getColumns()));
                    }
                    // Stop on any non-null piece (rook or blocker).
                    break;
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
