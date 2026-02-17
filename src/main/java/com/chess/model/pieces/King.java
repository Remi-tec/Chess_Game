package com.chess.model.pieces;

import com.chess.model.Case;
import com.chess.model.Couleur;
import com.chess.model.Direction;

import java.util.Arrays;

public class King extends Piece {

    public King(Couleur color, Case position) {
        super(color, position, Arrays.asList(Direction.DIAGONAL,Direction.HORIZONTAL),1);
    }

    @Override
    public String getNom(){
        return "King";
    }
}
