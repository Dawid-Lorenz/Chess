package com.example.chess;

public class Bishop extends Piece {
    private int value = 30;

    public Bishop(boolean colour) {
        super(colour);
    }

    public Bishop(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    @Override
    public int getValue()
    {
        return value;
    }

    @Override
    public boolean isMoveAllowed(byte x, byte y) {
        return getX() != x && getY() != y &&
                ((getX() - x == getY() - y) || (getX() - x == y - getY()));
    }
}