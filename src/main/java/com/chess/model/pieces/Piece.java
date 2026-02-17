package com.chess.model.pieces;

import com.chess.model.Couleur;
import com.chess.model.Case;
import com.chess.model.Direction;

public abstract class Piece {
    protected Couleur color;
    protected Case position;
    protected boolean hasMove = false;
    protected int moveCount = 0;
    protected Direction typeOfMouvement;

    public Piece(Couleur color,Case position, Direction typeOfMouvement) {
        this.color = color;
        this.position = position;
        this.typeOfMouvement = typeOfMouvement;
    }

    public Couleur getColor() {
        return color;
    }

    public Direction getTypeOfMouvement() {
        return typeOfMouvement;
    }

    public Case getPosition() {
        return position;
    }

    public boolean getHasMove() {
        return hasMove;
    }

    public void setPosition(Case position) {
        this.position = position;
        this.hasMove = true;
        this.moveCount++;
    }

    public  boolean canMove(Case destination, Piece[][] board, Case[] tabPossibleMoves) {
        for (Case possibleMoves : tabPossibleMoves) {
            if (possibleMoves != null && possibleMoves.getRows() == destination.getRows() && possibleMoves.getColumns() == destination.getColumns()) {
                return true;
            }
        }
        return false;
    };

    public abstract String getNom();

    public abstract Case[] getPossibleMoves(Piece[][] board);
}
