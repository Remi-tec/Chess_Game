package com.chess.controller;

import com.chess.model.Board;
import com.chess.model.Case;
import com.chess.model.pieces.Piece;

import java.util.List;

public class BoardController {
    private Board board;
    private Case caseSelected = null;
    private List<Case> tabPossibleMoves = null;

    public BoardController(Board board) {
        this.board = board;
    }

    public void handleCaseClick(int row, int column, Runnable onBoardChange) {
        System.out.println("CURRENT PLAYER : " + board.getCurrentPlayer());
        if (caseSelected == null) {
            Piece piece = board.getPiece(row, column);

            if (piece != null && piece.getColor() == board.getCurrentPlayer()) {
                caseSelected = new Case(row, column);
                tabPossibleMoves = getMovesForPiece(piece);
                System.out.println("Pièce sélectionnée : " + piece.getNom() + " en " + caseSelected);
                onBoardChange.run();
            }
        } else {
            Case caseDestination = new Case(row, column);
            Piece piece = board.getPiece(caseDestination);
            if (piece != null && piece.getColor() == board.getCurrentPlayer()) {
                caseSelected = caseDestination;
                tabPossibleMoves = getMovesForPiece(piece);
                System.out.println("Pièce sélectionnée : " + piece.getNom() + " en " + caseSelected);
                onBoardChange.run();
            } else {
                boolean moveSucceded = board.movePiece(caseSelected, caseDestination, tabPossibleMoves);

                if (moveSucceded) {
                    System.out.println("Déplacement réussi de " + caseSelected + " à " + caseDestination);
                    board.updatePossibleMovesCache(caseSelected, caseDestination);
                } else {
                    System.out.println("Déplacement impossible");
                }
                tabPossibleMoves = null;
                caseSelected = null;
                onBoardChange.run();
            }
        }
        if (tabPossibleMoves != null) {
            for (Case possibleMovetemp : tabPossibleMoves) {
                System.out.print(" : " + possibleMovetemp);
            }
            System.out.println();
        }
    }

    public List<Case> getMovesForPiece(Piece piece) {
        return board.getIsInCheck()
                ? board.getPossibleMovesCacheEchec().get(piece)
                : board.getPossibleMovesCache().get(piece);
    }

    public Board getBoard() {
        return board;
    }

    public List<Case> getTabPossibleMoves() {
        return tabPossibleMoves;
    }
}
