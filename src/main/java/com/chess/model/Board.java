package com.chess.model;

import com.chess.model.pieces.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Board {
    private Piece[][] board;
    private Couleur currentPlayer;
    private Couleur lastPlayer;
    private List<Piece> kingsList;
    private Map<Piece, List<Case>> possibleMovesCache;
    private Map<Piece,List<Case>> possibleMovesCacheEchec;
    private boolean isInCheck;
    private int moveNumber;
    private List<Piece> deferredUpdate;

    public Board() {
        board = new Piece[8][8];
        currentPlayer = Couleur.WHITE;
        lastPlayer = Couleur.BLACK;
        moveNumber = 0;
        deferredUpdate = new ArrayList<>();
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
                    for (Case possibleMove : piece.getPossibleMoves(board, moveNumber)) {
                        if (isMoveValidForCache(piece, possibleMove, tempList, null)) {
                            tempList.add(possibleMove);
                        }
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

    private List<Piece> wallPieceDetection(Case kingReferenceCase, Couleur alliedColor) {
        List<Piece> pinnedPieces = new ArrayList<>();

        for (Direction rayDirectionType : Arrays.asList(Direction.DIAGONAL, Direction.HORIZONTAL)) {
            for (int[] rayStep : rayDirectionType.getDirections()) {
                int scanRow = kingReferenceCase.getRows();
                int scanCol = kingReferenceCase.getColumns();

                Piece alliedCandidatePinned = null;
                boolean enemyFound = false;

                while (true) {
                    scanRow += rayStep[0];
                    scanCol += rayStep[1];

                    if (scanRow < 0 || scanRow >= 8 || scanCol < 0 || scanCol >= 8) break;

                    Piece encounteredPiece = board[scanRow][scanCol];
                    if (encounteredPiece == null) continue;

                    if (encounteredPiece.getColor() == alliedColor) {
                        if (alliedCandidatePinned != null) {
                            if (encounteredPiece.getIsStuck()) {
                                alliedCandidatePinned.setIsStuck(false);
                                pinnedPieces.add(alliedCandidatePinned);
                            }
                            // Deuxième pièce alliée sur le même rayon : aucun clouage possible, on arrête ce rayon.
                            break;
                        } else {
                            alliedCandidatePinned = encounteredPiece;
                        }
                    } else {
                        // Pièce ennemie : clouage si elle se déplace sur ce type de rayon et qu'une alliée est devant.
                        if (encounteredPiece.getTypeOfMouvement().contains(rayDirectionType) && alliedCandidatePinned != null) {
                            alliedCandidatePinned.setIsStuck(true);
                            pinnedPieces.add(alliedCandidatePinned);
                        }
                        enemyFound = true;
                        break; // Pièce ennemie bloque le rayon dans tous les cas
                    }
                }

                // Rayon sorti du plateau sans pièce ennemie :
                // si la candidate était clouée avant, elle ne l'est plus.
                if (!enemyFound && alliedCandidatePinned != null && alliedCandidatePinned.getIsStuck()) {
                    alliedCandidatePinned.setIsStuck(false);
                    pinnedPieces.add(alliedCandidatePinned);
                }
            }
        }
        return pinnedPieces;
    }

    public void updatePossibleMovesCache(Case start,Case finish) {
        Piece pieceWhoCheck = null;
        Object[] checkIsInCheck = isInCheck(board, currentPlayer,pieceWhoCheck);
        possibleMovesCacheEchec.clear();

        if ((boolean) checkIsInCheck[0]) {
            pieceWhoCheck = (Piece) checkIsInCheck[1];
        }

        List<Piece> listPiecesStart = piecesToUpdateDetection(start);
        listPiecesStart.add(getPiece(finish));
        List<Piece> listWall = wallPieceDetection(findKingPosition(board, currentPlayer), currentPlayer);
        System.out.println("WallPieceDection :" + findKingPosition(board, lastPlayer).toString() + lastPlayer.toString());
        if (getPiece(finish) instanceof King) { // débloque les pièces clouées dans le cas où le roi est déplacé
            listWall.addAll(wallPieceDetection(start, getPiece(finish).getColor()));
            System.out.println("WallPieceDection King movement :" + start.toString() + getPiece(finish).getColor().toString());
        }
        List<Piece> listPiecesFinish = piecesToUpdateDetection(finish);

        listWall.addAll(kingsList); // Toujours mettre à jour les mouvements des rois pour la détection d'échec

        List<Piece> merged = Stream.concat(listPiecesStart.stream(), listPiecesFinish.stream())
                .distinct()
                .collect(Collectors.toList());

        if (listWall != null && !listWall.isEmpty()) {
            merged = Stream.concat(merged.stream(), listWall.stream())
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Les pions différés du tour précédent sont recalculés ici, puis consommés.
        merged = Stream.concat(merged.stream(), deferredUpdate.stream())
                .distinct()
                .collect(Collectors.toList());
        deferredUpdate.clear();

        for (Piece piece : merged) {
            System.out.println("Mise à jour du cache pour " + piece.getNom() + " en " + piece.getPosition());
        }


        for (Piece piece : merged) {
            List<Case> tempList = new java.util.ArrayList<>();
            for (Case possibleMove : piece.getPossibleMoves(board, moveNumber)) {
                if (isMoveValidForCache(piece, possibleMove, tempList, pieceWhoCheck)) {
                    tempList.add(possibleMove);
                }
            }
            possibleMovesCache.put(piece, tempList);
        }

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
            Piece kingCurrentPlayer = kingsList.stream().filter(k -> k.getColor() == getCurrentPlayer()).findFirst().orElse(null);
            if (kingCurrentPlayer != null && !possibleMovesCache.get(kingCurrentPlayer).isEmpty()) {
                possibleMovesCacheEchec.put(kingCurrentPlayer, possibleMovesCache.get(kingCurrentPlayer));
            }
            for (Map.Entry<Piece, List<Case>> entry : possibleMovesCacheEchec.entrySet()) {
                Piece piece = entry.getKey();
                System.out.println("Mouvements possibles pour " + piece.getNom() + " en " + piece.getPosition() + " :");
                for (Case c : entry.getValue()) {
                    System.out.print(" (" + c.getRows() + ", " + c.getColumns() + ")");
                }
                System.out.println(" ");
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

        boolean isCastlingMove = isCastlingMove(piece, start, finish);
        if (isCastlingMove && !hasValidCastlingRook(board, piece, start, finish)) {
            return false;
        }

        int nextMoveNumber = moveNumber + 1;

        // Gestion de la prise en passant pour les pions
        boolean isEnPassantMove = false;
        Piece capturedPiece = board[finish.getRows()][finish.getColumns()];

        if (piece instanceof Pawn && capturedPiece == null &&
            Math.abs(finish.getRows() - start.getRows()) == 1 &&
            Math.abs(finish.getColumns() - start.getColumns()) == 1) {
            // C'est un mouvement diagonal du pion vers une case vide : prise en passant
            Piece adjacentPiece = board[finish.getRows()][start.getColumns()];
            if (adjacentPiece instanceof Pawn
                    && adjacentPiece.getColor() != piece.getColor()
                    && adjacentPiece.getDistance() == 2
                    && adjacentPiece.getMoveHistoricSize() == 1
                    && adjacentPiece.getFirstMove() == moveNumber) {
                isEnPassantMove = true;
                capturedPiece = adjacentPiece;
            }
        }

        if (capturedPiece != null) {
            possibleMovesCache.remove(capturedPiece);
        }

        board[finish.getRows()][finish.getColumns()] = piece;
        board[start.getRows()][start.getColumns()] = null;
        piece.setPosition(finish, nextMoveNumber);

        // Supprimer le pion capturé en passant
        if (isEnPassantMove && capturedPiece != null) {
            board[finish.getRows()][start.getColumns()] = null;
        }

        if (isCastlingMove) {
            moveCastlingRook(board, start, finish, true, nextMoveNumber);
        }

        moveNumber = nextMoveNumber;
        deferredUpdate.addAll(collectDeferredUpdates(piece));

        lastPlayer = currentPlayer;
        currentPlayer = (currentPlayer == Couleur.WHITE) ? Couleur.BLACK : Couleur.WHITE;

        return true;
    }

    private Case findKingPosition(Piece[][] boardCopy, Couleur couleur) {
        if (boardCopy == null) return null;
        for (int i = 0; i < boardCopy.length; i++) {
            for (int j = 0; j < boardCopy[i].length; j++) {
                Piece piece = boardCopy[i][j];
                if (piece instanceof King && piece.getColor() == couleur) {
                    return new Case(i, j);
                }
            }
        }
        return null;
    }

    private Object[] isInCheck(Piece[][] boardCopy, Couleur kingColor, Piece pieceWhoCheck) {
        Piece piece = null;
        List<Case> blocage= new ArrayList<>();
        if (boardCopy == null) return new Object[]{false, piece, blocage};

        Case kingPosition = findKingPosition(boardCopy, kingColor);
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
                        } else if (boardCopy[newRow][newCol].getColor() != kingColor
                                && boardCopy[newRow][newCol].getTypeOfMouvement().contains(directionType) 
                                && range <= boardCopy[newRow][newCol].getMoveRange() 
                                && boardCopy[newRow][newCol] != pieceWhoCheck ) {
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
        directions = kingColor == Couleur.WHITE ? Direction.WHITEPAWN.getDirections() : Direction.BLACKPAWN.getDirections();
        directions = new int[][]{directions[2], directions[3]}; // Only diagonal attacks
        for (int[] direction : directions) {
            newRow = kingPosition.getRows() + direction[0];
            newCol = kingPosition.getColumns() + direction[1];
            if (newRow < 8 && newRow >= 0 && newCol < 8 && newCol >= 0) {
                blocage.add(new Case(newRow, newCol));
                if (boardCopy[newRow][newCol] != null
                        && boardCopy[newRow][newCol].getColor() != kingColor
                        && boardCopy[newRow][newCol].getTypeOfMouvement().contains((kingColor == Couleur.WHITE ? Direction.BLACKPAWN : Direction.WHITEPAWN))) {
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

    private boolean isMoveValidForCache(Piece piece, Case possibleMove, List<Case> tempList, Piece pieceWhoCheck) {
        if (!moveSimulation(piece.getPosition(), possibleMove, pieceWhoCheck)) {
            return false;
        }

        if (!(piece instanceof King)) {
            return true;
        }

        int deltaRow = possibleMove.getRows() - piece.getPosition().getRows();
        int deltaCol = possibleMove.getColumns() - piece.getPosition().getColumns();
        boolean isCastlingMove = deltaCol == 0 && Math.abs(deltaRow) == 2;

        if (!isCastlingMove) {
            return true;
        }

        if (piece.getHasMove()) {
            return false;
        }

        // Castling is forbidden while the king is currently in check.
        if ((boolean) isInCheck(board, piece.getColor(), null)[0]) {
            return false;
        }

        int step = deltaRow > 0 ? 1 : -1;
        Case intermediateCase = new Case(piece.getPosition().getRows() + step, piece.getPosition().getColumns());
        return containsCaseByCoordinates(tempList, intermediateCase);
    }

    public boolean moveSimulation(Case start, Case destination,Piece pieceWhoCheck) {
        Piece[][] boardCopy = new Piece[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null) {
                    boardCopy[i][j] = board[i][j];
                }
            }
        }

        Piece piece = boardCopy[start.getRows()][start.getColumns()];
        if (piece == null) return false;

        boolean isEnPassantMove = false;
        Piece destinationPiece = boardCopy[destination.getRows()][destination.getColumns()];
        if (piece instanceof Pawn
                && destinationPiece == null
                && Math.abs(destination.getRows() - start.getRows()) == 1
                && Math.abs(destination.getColumns() - start.getColumns()) == 1) {
            Piece adjacentPiece = boardCopy[destination.getRows()][start.getColumns()];
            if (adjacentPiece instanceof Pawn
                    && adjacentPiece.getColor() != piece.getColor()
                    && adjacentPiece.getDistance() == 2
                    && adjacentPiece.getMoveHistoricSize() == 1
                    && adjacentPiece.getFirstMove() == moveNumber) {
                isEnPassantMove = true;
            }
        }

        boolean isCastlingMove = isCastlingMove(piece, start, destination);
        if (isCastlingMove && !hasValidCastlingRook(boardCopy, piece, start, destination)) {
            return false;
        }

        boardCopy[destination.getRows()][destination.getColumns()] = piece;
        boardCopy[start.getRows()][start.getColumns()] = null;

        if (isEnPassantMove) {
            boardCopy[destination.getRows()][start.getColumns()] = null;
        }

        if (isCastlingMove) {
            moveCastlingRook(boardCopy, start, destination, false, moveNumber);
        }

        // Pour un déplacement du roi, ne pas ignorer la pièce qui donnait échec.
        Piece ignoredCheckingPiece = (piece instanceof King) ? null : pieceWhoCheck;
        boolean moveValid = !(boolean) isInCheck(boardCopy, piece.getColor(), ignoredCheckingPiece)[0];
        // Ajout de la piece dans deferredUpdate si la prise en passant est valide
        if (moveValid && isEnPassantMove && !deferredUpdate.contains(piece)) {
            deferredUpdate.add(piece);
        }

        return moveValid;
    }

    private List<Piece> collectDeferredUpdates(Piece movedPiece) {
        List<Piece> nextDeferredUpdate = new ArrayList<>();

        if (!(movedPiece instanceof Pawn) || movedPiece.getDistance() != 2 || movedPiece.getMoveHistoricSize() != 1) {
            return nextDeferredUpdate;
        }

        int currentRow = movedPiece.getPosition().getRows();
        int currentCol = movedPiece.getPosition().getColumns();
        int[] adjacentRows = {currentRow - 1, currentRow + 1};

        for (int adjacentRow : adjacentRows) {
            if (adjacentRow >= 0 && adjacentRow < 8) {
                Piece adjacentPiece = board[adjacentRow][currentCol];
                if (adjacentPiece instanceof Pawn && adjacentPiece.getColor() != movedPiece.getColor()) {
                    nextDeferredUpdate.add(adjacentPiece);
                }
            }
        }

        return nextDeferredUpdate;
    }

    private boolean isCastlingMove(Piece piece, Case start, Case finish) {
        if (!(piece instanceof King)) {
            return false;
        }
        return start.getColumns() == finish.getColumns()
                && Math.abs(finish.getRows() - start.getRows()) == 2;
    }

    private boolean hasValidCastlingRook(Piece[][] boardState, Piece king, Case start, Case finish) {
        int step = finish.getRows() > start.getRows() ? 1 : -1;
        int rookStartRow = step > 0 ? 7 : 0;
        Piece rook = boardState[rookStartRow][start.getColumns()];
        return rook instanceof Rook && rook.getColor() == king.getColor() && !rook.getHasMove();
    }

    private void moveCastlingRook(Piece[][] boardState, Case kingStart, Case kingFinish, boolean updateRookPosition, int moveNumberForUpdate) {
        int step = kingFinish.getRows() > kingStart.getRows() ? 1 : -1;
        int rookStartRow = step > 0 ? 7 : 0;
        int rookEndRow = kingFinish.getRows() - step;

        Piece rook = boardState[rookStartRow][kingStart.getColumns()];
        boardState[rookEndRow][kingStart.getColumns()] = rook;
        boardState[rookStartRow][kingStart.getColumns()] = null;

        if (updateRookPosition && rook != null) {
            rook.setPosition(new Case(rookEndRow, kingStart.getColumns()), moveNumberForUpdate);
        }
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

    public int getMoveNumber() {
        return moveNumber;
    }

    public void printPossibleMovesCache() {
        for (int i = 0; i < possibleMovesCache.size(); i++) {
            System.out.println(possibleMovesCache.keySet().toArray()[i] + " : " + possibleMovesCache.get(possibleMovesCache.keySet().toArray()[i]));
        }
    }
}
