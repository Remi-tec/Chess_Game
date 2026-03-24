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
    private final Board board;
    private final BoardController controller;
    private final StackPane[][] cellPanes = new StackPane[8][8];
    private final Circle[][] moveIndicators = new Circle[8][8];
    private final Text[][] pieceTexts = new Text[8][8];

    public BoardView(Board board) {
        this.board = board;
        this.controller = new BoardController(board);
        initializeBoardUI();
        updateBoardState();
    }

    private void initializeBoardUI() {
        for (int column = 0; column < 8; column ++) {
            for (int row = 0; row < 8; row++) {
                StackPane casePane = createCase(row, column);
                cellPanes[row][column] = casePane;
                this.add(casePane, row, column);
            }
        }
    }

    private StackPane createCase(int row, int colunm) {
        StackPane casePane = new StackPane();


        Rectangle rectangle = new Rectangle(CASE_SIZE, CASE_SIZE);
        boolean isCaseWhite = (row + colunm) % 2 == 0;
        rectangle.setFill(isCaseWhite ? Color.WHEAT : Color.SADDLEBROWN);

        casePane.getChildren().add(rectangle);

        Circle circle = new Circle(CASE_SIZE / 4);
        circle.setFill(Color.GREY);
        circle.setVisible(false);
        circle.setMouseTransparent(true);
        moveIndicators[row][colunm] = circle;
        casePane.getChildren().add(circle);

        Text textePiece = new Text("");
        textePiece.setFont(new Font(50));
        textePiece.setMouseTransparent(true);
        pieceTexts[row][colunm] = textePiece;
        casePane.getChildren().add(textePiece);

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

    private void updateBoardState() {
        boolean[][] possibleMovesMask = new boolean[8][8];
        List<Case> tabPossibleMoves = controller.getTabPossibleMoves();
        if (tabPossibleMoves != null) {
            for (Case c : tabPossibleMoves) {
                if (c != null) {
                    possibleMovesMask[c.getRows()][c.getColumns()] = true;
                }
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                moveIndicators[row][column].setVisible(possibleMovesMask[row][column]);

                Piece piece = board.getPiece(row, column);
                Text pieceText = pieceTexts[row][column];
                if (piece != null) {
                    pieceText.setText(getSymbolPiece(piece));
                    pieceText.setFill(piece.getColor() == com.chess.model.Couleur.WHITE ? Color.WHITE : Color.BLACK);
                } else {
                    pieceText.setText("");
                }
            }
        }
    }

    private void refreshBoard() {
        updateBoardState();
    }
}
