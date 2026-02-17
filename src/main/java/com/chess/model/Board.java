package com.chess.model;

import com.chess.model.pieces.*;

public class Board {
    private Piece[][] board;
    private Couleur currentPlayer;
    private Piece[][] boardCopy;

    public Board(){
        board = new Piece[8][8];
        currentPlayer = Couleur.BLACK;
        initializeBoard();
    }

    private void initializeBoard() {
        // Positioning White pieces
        board[4][0] = new King(Couleur.WHITE, new Case(4,0));
        board[0][0] = new Rook(Couleur.WHITE, new Case(0,0));
        board[7][0] = new Rook(Couleur.WHITE, new Case(7,0));
        board[1][0] = new Knight(Couleur.WHITE, new Case(1,0));
        board[6][0] = new Knight(Couleur.WHITE, new Case(6,0));
        board[2][0] = new Bishop(Couleur.WHITE, new Case(2,0));
        board[5][0] = new Bishop(Couleur.WHITE, new Case(5,0));
        board[3][0] = new Queen(Couleur.WHITE, new Case(3,0));
        for (int i =0; i < board[0].length; i++) {
            board[i][1] = new Pawn(Couleur.WHITE, new Case(i,1));
        }
        // Positioning Black Pieces
        board[4][7] = new King(Couleur.BLACK, new Case(4,7));
        board[0][7] = new Rook(Couleur.BLACK, new Case(0,7));
        board[7][7] = new Rook(Couleur.BLACK, new Case(7,7));
        board[1][7] = new Knight(Couleur.BLACK, new Case(1,7));
        board[6][7] = new Knight(Couleur.BLACK, new Case(6,7));
        board[2][7] = new Bishop(Couleur.BLACK, new Case(2,7));
        board[5][7] = new Bishop(Couleur.BLACK, new Case(5,7));
        board[3][7] = new Queen(Couleur.BLACK, new Case(3,7));
        for (int i =0; i < board[0].length; i++) {
            board[i][6] = new Pawn(Couleur.BLACK, new Case(i,6));
        }
    }

    public Piece getPiece(int row, int column) {
        if (row < 0 || row >= 8 || column < 0 || column >= 8) {
            return null;
        }
        return board[row][column];
    }

    public Piece getPiece(Case case_) {
        return getPiece(case_.getRows(), case_.getColumns());
    }

    public boolean movePiece(Case start, Case finish, Case[] tabPossibleMoves) {
        System.out.println("Déplacement de " + start +  " à " + finish.getRows() + "," + finish.getColumns());
        Piece piece = getPiece(start);

        if (piece == null) {
            return false;
        }

        if (piece.getColor() != currentPlayer) {
            return false;
        }

        if (!piece.canMove(finish,board,tabPossibleMoves)) {
            return false;
        }

        board[finish.getRows()][finish.getColumns()] = piece;
        board[start.getRows()][start.getColumns()] = null;
        piece.setPosition(finish);

        currentPlayer = (currentPlayer == Couleur.WHITE) ? Couleur.BLACK : Couleur.WHITE;

        return true;
    }

    private Case findKingPosition() {
        if (boardCopy == null) return null;
        for (int i = 0; i < boardCopy.length ; i++ ){
            for (int j = 0; j < boardCopy[i].length; j++){
                Piece piece = boardCopy[i][j];
                if (piece instanceof King && piece.getColor() == this.getCurrentPlayer()) {
                    return new Case(i,j);
                }
            }
        }
        return null;
    }

    private boolean isInCheck() {
        if (boardCopy == null) return false;
        Case kingPosition = findKingPosition();
        if (kingPosition == null) return false;

        int newRow;
        int newCol;
        int[][] directions = Direction.DIAGONAL.getDirections(); // Bishop and Queen moves
        boolean move;
        for (int[] direction : directions) {
            newRow = kingPosition.getRows();
            newCol = kingPosition.getColumns();
            move = true;
            while (move) {
                newRow += direction[0];
                newCol += direction[1];
                if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                    if (boardCopy[newRow][newCol] == null) {
                        continue;
                    }
                    if (boardCopy[newRow][newCol].getColor() != this.getCurrentPlayer()
                            && (boardCopy[newRow][newCol].getTypeOfMouvement() == Direction.DIAGONAL
                            || boardCopy[newRow][newCol].getTypeOfMouvement() == Direction.ALL)) {
                        return true;
                    } else {
                        move = false;
                    }
                } else {
                    move = false;
                }
            }
        }

        directions = Direction.HORIZONTAL.getDirections(); // Rook and Queen moves
        for (int[] direction : directions) {
            newRow = kingPosition.getRows();
            newCol = kingPosition.getColumns();
            move = true;
            while (move) {
                newRow += direction[0];
                newCol += direction[1];
                if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                    if (boardCopy[newRow][newCol] == null) {
                        continue;
                    }
                    if (boardCopy[newRow][newCol].getColor() != this.getCurrentPlayer()
                            && (boardCopy[newRow][newCol].getTypeOfMouvement() == Direction.HORIZONTAL
                            || boardCopy[newRow][newCol].getTypeOfMouvement() == Direction.ALL)) {
                        return true;
                    } else {
                        move = false;
                    }
                } else {
                    move = false;
                }
            }
        }

        directions = Direction.L.getDirections(); // Knight moves
        for (int[] direction : directions) {
            newRow = kingPosition.getRows() + direction[0];
            newCol = kingPosition.getColumns() + direction[1];
            if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                if (boardCopy[newRow][newCol] != null
                        && boardCopy[newRow][newCol].getColor() != this.getCurrentPlayer()
                        && boardCopy[newRow][newCol].getTypeOfMouvement() == Direction.L) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean moveSimulation(Case start,Case destination) {
        boardCopy = new Piece[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null) {
                    boardCopy[i][j] = board[i][j];
                }
            }
        }

        Piece piece = boardCopy[start.getRows()][start.getColumns()];
        if (piece == null) return false;

        boardCopy[destination.getRows()][destination.getColumns()] = piece;
        boardCopy[start.getRows()][start.getColumns()] = null;

        return !isInCheck();
    }

    public Couleur getCurrentPlayer() {
        return currentPlayer;
    }

    public Piece[][] getBoard() {
        return board;
    }
}
