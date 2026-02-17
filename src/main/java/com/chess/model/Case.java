package com.chess.model;

public class Case {
    private final int rows;
    private final int columns;

    public Case(int rows,int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows(){
        return rows;
    }

    public int getColumns(){
        return columns;
    }

    @Override
    public String toString() {
        return "(" + rows + ", " + columns + ")";
    }
}
