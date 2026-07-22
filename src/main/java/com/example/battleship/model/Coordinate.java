package com.example.battleship.model;

import java.io.Serializable;
import java.util.Objects;

public class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    private int row;
    private int column;

    public Coordinate(int row, int column){
        this.row = row;
        this.column = column;
    }


    public int getRow(){return row;}

    public int getColumn(){return column;}

    //public Coordinate translate(int dRow, int dColumn)

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Coordinate)) return false;

        Coordinate other = (Coordinate) obj;
        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
