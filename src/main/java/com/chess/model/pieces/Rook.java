package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.Arrays;

public class Rook extends Piece {

    public Rook(Couleur color, Case position) {
        super(color, position, Arrays.asList(Direction.HORIZONTAL),7);
    }

    @Override
    public String getNom() {
        return "Rook";
    }
}
