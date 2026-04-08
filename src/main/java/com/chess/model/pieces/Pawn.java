package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Couleur color, Case position) {
        super(color, position,Arrays.asList(color == Couleur.BLACK ? Direction.BLACKPAWN : Direction.WHITEPAWN),1);
    }

    @Override
    public Case[] getPossibleMoves(Piece[][] board) {
        List<Case> possibleMoves = new ArrayList<>();
        int[][] directions = getMergeTypeOfMouvement(typeOfMouvement);

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
        
        // En passant capture
        checkEnPassantCapture(board, possibleMoves);
        
        return possibleMoves.toArray(new Case[0]);
    }
    
    /**
     * Vérifie et ajoute les coups de prise en passant disponibles.
     * Conditions: pion ennemi adjacent avec distance == 2 et moveHistoric.size() == 1
     */
    private void checkEnPassantCapture(Piece[][] board, List<Case> possibleMoves) {
        int currentRow = position.getRows();
        int currentCol = position.getColumns();
        
        // Vérifier les pions adjacents (gauche et droite)
        int[] adjacentCols = {currentCol - 1, currentCol + 1};
        
        for (int adjacentCol : adjacentCols) {
            // Vérifier que la colonne est valide
            if (adjacentCol >= 0 && adjacentCol < 8) {
                Piece adjacentPiece = board[currentRow][adjacentCol];
                
                // Vérifier que c'est un pion ennemi
                if (adjacentPiece != null && 
                    adjacentPiece instanceof Pawn && 
                    adjacentPiece.getColor() != this.color) {
                    
                    // Vérifier les conditions: distance == 2 et moveHistoric.size() == 1
                    if (adjacentPiece.getDistance() == 2 && 
                        adjacentPiece.getMoveHistoricSize() == 1) {
                        
                        // Déterminer la direction du pion actuel (vers l'avant)
                        int direction = (this.color == Couleur.BLACK) ? 1 : -1;
                        
                        // La case de capture est diagonale devant le pion ennemi
                        int captureRow = currentRow + direction;
                        int captureCol = adjacentCol;
                        
                        // Vérifier que la case de capture est valide
                        if (captureRow >= 0 && captureRow < 8) {
                            possibleMoves.add(new Case(captureRow, captureCol));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getNom() {
        return "Pawn";
    }
}
