package com.chess.view;

import com.chess.model.Case;
import com.chess.model.Board;
import com.chess.model.pieces.Piece;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends GridPane {
    private static final int CASE_SIZE = 75;
    private Board board;
    private Case caseSelected = null;
    private List<Case> tabPossibleMoves = null;

    public BoardView(Board board) {
        this.board = board;
        drawBoard();
    }

    private void drawBoard() {
        for (int column = 0; column < 8; column ++) {
            for (int row = 0; row < 8; row++) {
                boolean isPossibleMove = isPossibleMove(row, column);
                StackPane casePane = createCase(row, column, isPossibleMove);
                this.add(casePane, row, column);
            }
        }
    }

    private StackPane createCase(int row, int colunm, boolean isPossibleMove) {
        StackPane casePane = new StackPane();


        Rectangle rectangle = new Rectangle(CASE_SIZE, CASE_SIZE);
        boolean isCaseWhite = (row + colunm) % 2 == 0;
        rectangle.setFill(isCaseWhite ? Color.WHEAT : Color.SADDLEBROWN);

        casePane.getChildren().add(rectangle);

        if (isPossibleMove) {
            Circle circle = new Circle(CASE_SIZE / 4);
            circle.setFill(Color.GREY);
            //rectangle.setStrokeWidth(3);
            casePane.getChildren().add(circle);
        }

        Piece piece = board.getPiece(row, colunm);
        if (piece != null) {
            Text textePiece = new Text(getSymbolPiece(piece));
            textePiece.setFont(new Font(50));
            textePiece.setFill(piece.getColor() == com.chess.model.Couleur.WHITE ? Color.WHITE : Color.BLACK);
            casePane.getChildren().add(textePiece);
        }

        final int rowFinal = row;
        final int columnFinal = colunm;
        casePane.setOnMouseClicked(e -> handleCaseClick(rowFinal, columnFinal));

        return  casePane;
    }

    private String getSymbolPiece(Piece piece) {
        String nom = piece.getNom();
        boolean isWhite = piece.getColor() == com.chess.model.Couleur.WHITE;

        switch (nom) {
            case "King":
                return isWhite ? "♔" : "♚";
            case "Queen":
                return isWhite ? "♕" : "♛";
            case "Rook":
                return isWhite ? "♖" : "♜";
            case "Bishop":
                return isWhite ? "♗" : "♝";
            case "Knight":
                return isWhite ? "♘" : "♞";
            case "Pawn":
                return isWhite ? "♙" : "♟";
            default:
                return "";
        }
    }

    private void handleCaseClick(int row, int column) {
        System.out.println("PARAMÈTRES CLASS BoardView" + caseSelected + ", " + board.getCurrentPlayer());
        if (caseSelected == null) {
            Piece piece = board.getPiece(row, column);

            if (piece != null && piece.getColor() == board.getCurrentPlayer()) {
                caseSelected = new Case(row, column);
                tabPossibleMoves = board.getPossibleMovesCache().get(piece);
                System.out.println("Pièce sélectionnée : " + piece.getNom() + " en " + caseSelected); //Piece sélectionnée
            }
        } else {
            Case caseDestination = new Case(row,column);
            Piece piece = board.getPiece(row, column);
            if (piece != null && board.getPiece(caseDestination).getColor() == board.getCurrentPlayer()) {
                caseSelected = caseDestination;
                tabPossibleMoves = board.getPossibleMovesCache().get(piece);
                System.out.println("Pièce sélectionnée : " + piece.getNom() + " en " + caseSelected); //Piece sélectionnée
            } else {

                boolean moveSucceded = board.movePiece(caseSelected, caseDestination, tabPossibleMoves);

                if (moveSucceded) {
                    System.out.println("Déplacement réussi de " + caseSelected + " à " + caseDestination);
                    board.updatePossibleMovesCache(caseSelected,caseDestination);
                } else {
                    System.out.println("Déplacement impossible");
                }
                tabPossibleMoves = null;
                caseSelected = null;
            } //Reset de la sélection et des mouvements possibles
        }
        if (tabPossibleMoves != null) {
            for (Case possibleMovetemp : tabPossibleMoves) { //Affichage des mouvements possibles dans la console
                System.out.print(" : " + possibleMovetemp);
            }
        }
        refreshBoard();
    }
    // Vérifie si la case cliquée est dans les mouvements possibles
    private boolean isPossibleMove(int row, int col) {
        if (tabPossibleMoves == null) return false;
        for (Case c : tabPossibleMoves) {
            if (c != null && c.getRows() == row && c.getColumns() == col) {
                return true;
            }
        }
        return false;
    }

    private void refreshBoard() {
        this.getChildren().clear();
        drawBoard();
    }
}
