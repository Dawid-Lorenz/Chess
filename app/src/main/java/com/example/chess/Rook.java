package com.example.chess;

public class Rook extends Piece {

    private double value = 525;
    public boolean moved = false;

    public Rook(boolean colour) {
        super(colour);
    }

    public Rook(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    public Rook(byte x, byte y, boolean colour, boolean moved) {super(x, y, colour); this.moved = moved;}

    @Override
    public double getValue()
    {
        return value;
    }
    @Override
    public boolean isMoveAllowed(byte x, byte y)
    {
        return (x >= 0 && x < 8 && y >= 0 && y < 8 &&
                ((getX() == x && getY() != y) || (getX() != x && getY() == y)));
    }
}