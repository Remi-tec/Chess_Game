package com.chess.model.pieces;

import com.chess.model.Couleur;
import com.chess.model.Case;
import com.chess.model.Direction;

import java.util.Arrays;

public class Knight extends Piece {

    public Knight(Couleur color, Case position) {
        super(color, position, Arrays.asList(Direction.L),1);
    }

    @Override
    public String getNom() {
        return "Knight";
    }
}
