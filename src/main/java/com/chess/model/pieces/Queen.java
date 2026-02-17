package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.Arrays;

public class Queen extends Piece {

    public Queen (Couleur color, Case position) {
        super(color, position, Arrays.asList(Direction.DIAGONAL,Direction.HORIZONTAL),7);
    }

    @Override
    public String getNom() {
        return "Queen";
    }
}
