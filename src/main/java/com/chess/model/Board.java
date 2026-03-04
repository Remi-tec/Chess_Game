package com.chess.model;

import com.chess.model.pieces.*;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Board {
    private Piece[][] board;
    private Couleur currentPlayer;
    private Piece[][] boardCopy;
    private List<Piece> kingsList;
    private Map<Piece, List<Case>> possibleMovesCache;
    private Map<Piece,List<Case>> possibleMovesCacheEchec;
    private boolean isInCheck;

    public Board() {
        board = new Piece[8][8];
        currentPlayer = Couleur.WHITE;
        initializeBoard();
        initializeKingsList();
        initializePossibleMovesCache();
    }

    private void initializeBoard() {
        // Positioning White pieces
        board[4][0] = new King(Couleur.WHITE, new Case(4, 0));
        board[0][0] = new Rook(Couleur.WHITE, new Case(0, 0));
        board[7][0] = new Rook(Couleur.WHITE, new Case(7, 0));
        board[1][0] = new Knight(Couleur.WHITE, new Case(1, 0));
        board[6][0] = new Knight(Couleur.WHITE, new Case(6, 0));
        board[2][0] = new Bishop(Couleur.WHITE, new Case(2, 0));
        board[5][0] = new Bishop(Couleur.WHITE, new Case(5, 0));
        board[3][0] = new Queen(Couleur.WHITE, new Case(3, 0));
        for (int i = 0; i < board[0].length; i++) {
            board[i][1] = new Pawn(Couleur.WHITE, new Case(i, 1));
        }
        // Positioning Black Pieces
        board[4][7] = new King(Couleur.BLACK, new Case(4, 7));
        board[0][7] = new Rook(Couleur.BLACK, new Case(0, 7));
        board[7][7] = new Rook(Couleur.BLACK, new Case(7, 7));
        board[1][7] = new Knight(Couleur.BLACK, new Case(1, 7));
        board[6][7] = new Knight(Couleur.BLACK, new Case(6, 7));
        board[2][7] = new Bishop(Couleur.BLACK, new Case(2, 7));
        board[5][7] = new Bishop(Couleur.BLACK, new Case(5, 7));
        board[3][7] = new Queen(Couleur.BLACK, new Case(3, 7));
        for (int i = 0; i < board[0].length; i++) {
            board[i][6] = new Pawn(Couleur.BLACK, new Case(i, 6));
        }
    }

    private void initializePossibleMovesCache() {
        possibleMovesCache = new java.util.HashMap<>();
        possibleMovesCacheEchec = new java.util.HashMap<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    List<Case> tempList = new java.util.ArrayList<>();
                    for (Case possibleMove : piece.getPossibleMoves(board)) {
                        if (moveSimulation(piece.getPosition(), possibleMove)) {
                            tempList.add(possibleMove);
                        }
                        ;
                    }
                    possibleMovesCache.put(piece, tempList);
                }
            }
        }
        System.out.println("Cache des mouvements possibles initialisé.");
    }

    private void initializeKingsList() {
        kingsList = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece instanceof King) {
                    kingsList.add(piece);
                }
            }
        }
    }

    private List<Piece> piecesToUpdateDetection(Case position) {
        List<Piece> piecesToUpdate = new ArrayList<>();
        boolean move;
        for (Direction directionType : Arrays.stream(Direction.values()).limit(3).toList()) {
            int newRow;
            int newCol;
            int range;
            int[][] directions = directionType.getDirections();
            for (int[] direction : directions) {
                newRow = position.getRows();
                newCol = position.getColumns();
                move = true;
                range = 0;
                while (move) {
                    range++;
                    newRow += direction[0];
                    newCol += direction[1];
                    if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                        if (board[newRow][newCol] == null) {
                            continue;
                        } else if (board[newRow][newCol].getTypeOfMouvement().contains(directionType) && range <= board[newRow][newCol].getMoveRange()) {
                            piecesToUpdate.add(board[newRow][newCol]);
                        } else {
                            move = false;
                        }
                    } else {
                        move = false;
                    }
                }
            }
        }
        // Détection des pions blancs et noirs affectés
        int newRow;
        int newCol;
        int[][] whitePawnDirections = Direction.WHITEPAWN.getDirections();
        for (int i = 0; i < whitePawnDirections.length; i++) {
            newRow = position.getRows() + whitePawnDirections[i][0];
            newCol = position.getColumns() + whitePawnDirections[i][1];
            if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                if (board[newRow][newCol] != null
                        && board[newRow][newCol].getTypeOfMouvement().contains(Direction.BLACKPAWN)) {
                    if (i == 0 && board[newRow][newCol].getHasMove()) {
                    } else {
                        piecesToUpdate.add(board[newRow][newCol]);
                    }
                }
            }
        }
        int[][] blackPawnDirections = Direction.BLACKPAWN.getDirections();
        for (int i = 0; i < blackPawnDirections.length; i++) {
            newRow = position.getRows() + blackPawnDirections[i][0];
            newCol = position.getColumns() + blackPawnDirections[i][1];
            if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                if (board[newRow][newCol] != null
                        && board[newRow][newCol].getTypeOfMouvement().contains(Direction.WHITEPAWN)) {
                    if (i == 0 && board[newRow][newCol].getHasMove()) {
                    } else {
                        piecesToUpdate.add(board[newRow][newCol]);
                    }
                }
            }
        }
        return piecesToUpdate;
    }

    private List<Piece> wallPieceDetection(Case finish, Couleur color) {
        List<Piece> pinned = new ArrayList<>();

        // Ne garder que les directions "rayons" (pas de L)
        for (Direction dir : Arrays.asList(Direction.DIAGONAL, Direction.HORIZONTAL)) {
            for (int[] direction : dir.getDirections()) {
                int newRow = finish.getRows();
                int newCol = finish.getColumns();
                Piece firstEnemy = null;
                Piece brotherCount = null;

                while (true) {
                    newRow += direction[0];
                    newCol += direction[1];

                    if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) break;

                    Piece target = board[newRow][newCol];
                    if (target == null) continue;

                    if (target.getColor() == color) {
                        brotherCount = target; // une pièce alliée entre le roi et la pièce potentiellement clouée
                    }else {
                        if(target.getTypeOfMouvement().contains(dir) && brotherCount != null) {
                            pinned.add(brotherCount);
                            break;// une pièce ennemie avant le roi => pas de clouage
                        }
                    }

                    if (target instanceof King) {
                        if (firstEnemy != null) {
                            pinned.add(firstEnemy);
                        }
                        break; // roi trouvé, on arrête dans cette direction
                    }

                    if (firstEnemy == null) {
                        firstEnemy = target; // première pièce ennemie potentiellement clouée
                    } else {
                        break; // une deuxième pièce ennemie => pas de clouage
                    }
                }
            }
        }
        return pinned;
    }

    public void updatePossibleMovesCache(Case start,Case finish) {
        List<Piece> listPiecesStart = piecesToUpdateDetection(start);
        listPiecesStart.add(getPiece(finish));
        List<Piece> listWall = wallPieceDetection(findKingPosition(board), getCurrentPlayer());
        System.out.println("WallPieceDection :" + findKingPosition(board).toString() + getCurrentPlayer().toString());
        if (getPiece(finish) instanceof King) { // débloque les pièces clouées dans le cas où le roi est déplacé
            listWall.addAll(wallPieceDetection(start, getPiece(finish).getColor()));
            System.out.println("WallPieceDection King movement :" + start.toString() + getPiece(finish).getColor().toString());
        }
        List<Piece> listPiecesFinish = piecesToUpdateDetection(finish);

        List<Piece> merged = Stream.concat(listPiecesStart.stream(), listPiecesFinish.stream())
                .distinct()
                .collect(Collectors.toList());

        if (listWall != null && !listWall.isEmpty()) {
            merged = Stream.concat(merged.stream(), listWall.stream())
                    .distinct()
                    .collect(Collectors.toList());
        }

        for (Piece piece : merged) {
            System.out.println("Mise à jour du cache pour " + piece.getNom() + " en " + piece.getPosition());
        }


        for (Piece piece : merged) {
            List<Case> tempList = new java.util.ArrayList<>();
            for (Case possibleMove : piece.getPossibleMoves(board)) {
                if (moveSimulation(piece.getPosition(), possibleMove)) {
                    tempList.add(possibleMove);
                }
                ;
            }
            possibleMovesCache.put(piece, tempList);
        }
        Object[] checkIsInCheck = isInCheck(board);
        possibleMovesCacheEchec.clear();

        if ((boolean) checkIsInCheck[0]) {
            isInCheck = true;
            System.out.println("Le roi est en échec par " + ((Piece) checkIsInCheck[1]).getNom() + " en " + ((Piece) checkIsInCheck[1]).getPosition());

            @SuppressWarnings("unchecked")
            List<Case> casesBlocage = (List<Case>) checkIsInCheck[2];

            for (Map.Entry<Piece, List<Case>> entry : possibleMovesCache.entrySet()) {
                Piece piece = entry.getKey();
                if (piece.getColor() != currentPlayer) {
                    continue;
                }

                List<Case> casesValidesEnEchec = new ArrayList<>();
                for (Case c : entry.getValue()) {
                    if (containsCaseByCoordinates(casesBlocage, c)) {
                        casesValidesEnEchec.add(c);
                    }
                }

                if (!casesValidesEnEchec.isEmpty()) {
                    possibleMovesCacheEchec.put(piece, casesValidesEnEchec);
                }
            }
        } else {
            isInCheck = false;
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

    public boolean movePiece(Case start, Case finish, List<Case> tabPossibleMoves) {
        System.out.println("Déplacement de " + start + " à " + finish.getRows() + "," + finish.getColumns());
        Piece piece = getPiece(start);

        if (piece == null) {
            return false;
        }

        if (piece.getColor() != currentPlayer) {
            return false;
        }

        if (!piece.canMove(finish, board, tabPossibleMoves)) {
            return false;
        }

        board[finish.getRows()][finish.getColumns()] = piece;
        board[start.getRows()][start.getColumns()] = null;
        piece.setPosition(finish);

        currentPlayer = (currentPlayer == Couleur.WHITE) ? Couleur.BLACK : Couleur.WHITE;

        return true;
    }

    private Case findKingPosition(Piece[][] boardCopy) {
        if (boardCopy == null) return null;
        for (int i = 0; i < boardCopy.length; i++) {
            for (int j = 0; j < boardCopy[i].length; j++) {
                Piece piece = boardCopy[i][j];
                if (piece instanceof King && piece.getColor() == this.getCurrentPlayer()) {
                    return piece.getPosition();
                }
            }
        }
        return null;
    }

    private Object[] isInCheck(Piece[][] boardCopy) {
        Piece piece = null;
        List<Case> blocage= new ArrayList<>();
        if (boardCopy == null) return new Object[]{false, piece, blocage};

        Case kingPosition = findKingPosition(boardCopy);
        if (kingPosition == null) return new Object[]{false, piece, blocage};

        List<Direction> directionList = Arrays.stream(Direction.values()).limit(3).toList();
        boolean move;

        for (Direction directionType : directionList) {
            int newRow;
            int newCol;
            int range;
            int[][] directions = directionType.getDirections();
            for (int[] direction : directions) {
                newRow = kingPosition.getRows();
                newCol = kingPosition.getColumns();
                move = true;
                range = 0;
                while (move) {
                    range++;
                    newRow += direction[0];
                    newCol += direction[1];

                    if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                        blocage.add(new Case(newRow, newCol));
                        if (boardCopy[newRow][newCol] == null) {
                            continue;
                        } else if (boardCopy[newRow][newCol].getColor() != this.getCurrentPlayer()
                                && boardCopy[newRow][newCol].getTypeOfMouvement().contains(directionType) && range <= boardCopy[newRow][newCol].getMoveRange()) {
                            piece = boardCopy[newRow][newCol];
                            return new Object[]{true, piece, new ArrayList<>(blocage)};
                        } else {
                            move = false;
                        }
                    } else {
                        move = false;
                    }
                }
                blocage.clear();
            }
        }

        // Pawn attacks
        int[][] directions;
        int newRow;
        int newCol;
        directions = this.getCurrentPlayer() == Couleur.WHITE ? Direction.WHITEPAWN.getDirections() : Direction.BLACKPAWN.getDirections();
        directions = new int[][]{directions[2], directions[3]}; // Only diagonal attacks
        for (int[] direction : directions) {
            newRow = kingPosition.getRows() + direction[0];
            newCol = kingPosition.getColumns() + direction[1];
            if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                blocage.add(new Case(newRow, newCol));
                if (boardCopy[newRow][newCol] != null
                        && boardCopy[newRow][newCol].getColor() != this.getCurrentPlayer()
                        && boardCopy[newRow][newCol].getTypeOfMouvement().contains((this.getCurrentPlayer() == Couleur.WHITE ? Direction.BLACKPAWN : Direction.WHITEPAWN))) {
                    piece = boardCopy[newRow][newCol];
                    return new Object[]{true, piece, new ArrayList<>(blocage)};
                }
            }
            blocage.clear();
        }
        return new Object[]{false, piece, blocage};
    }

    private boolean containsCaseByCoordinates(List<Case> cases, Case target) {
        for (Case c : cases) {
            if (c.getRows() == target.getRows() && c.getColumns() == target.getColumns()) {
                return true;
            }
        }
        return false;
    }

    public boolean moveSimulation(Case start, Case destination) {
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

        return !(boolean) isInCheck(boardCopy)[0];
    }

    public Couleur getCurrentPlayer() {
        return currentPlayer;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Map<Piece,List<Case>> getPossibleMovesCache() {
        return possibleMovesCache;
    }

    public Map<Piece,List<Case>> getPossibleMovesCacheEchec() {
        return possibleMovesCacheEchec;
    }

    public boolean getIsInCheck() {
        return isInCheck;
    }

    public void printPossibleMovesCache() {
        for (int i = 0; i < possibleMovesCache.size(); i++) {
            System.out.println(possibleMovesCache.keySet().toArray()[i] + " : " + possibleMovesCache.get(possibleMovesCache.keySet().toArray()[i]));
        }
    }
}
