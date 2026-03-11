package com.chess.view;

import com.chess.controller.BoardController;
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

import java.util.List;

public class BoardView extends GridPane {
    private static final int CASE_SIZE = 75;
    private Board board;
    private BoardController controller;

    public BoardView(Board board) {
        this.board = board;
        this.controller = new BoardController(board);
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
        controller.handleCaseClick(row, column, () -> {
            refreshBoard();
            checkGameOver();
        });
    }

    private void checkGameOver() {
        List<Object> result = controller.isGameOver();
        if ((boolean) result.get(0)) {
            String message = result.get(2) + " - Joueur " + result.get(1) + " a perdu !";
            System.out.println("=== FIN DE PARTIE : " + message + " ===");
            this.setDisable(true);
        }
    }

    // Vérifie si la case cliquée est dans les mouvements possibles
    private boolean isPossibleMove(int row, int col) {
        List<Case> tabPossibleMoves = controller.getTabPossibleMoves();
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
