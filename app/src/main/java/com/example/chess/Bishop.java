package com.example.chess;

public class Bishop extends Piece {
    private double value = 350;

    public Bishop(boolean colour) {
        super(colour);
    }

    public Bishop(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    @Override
    public double getValue()
    {
        return value;
    }

    @Override
    public boolean isMoveAllowed(byte x, byte y) {
        return getX() != x && getY() != y &&
                ((getX() - x == getY() - y) || (getX() - x == y - getY()));
    }
}