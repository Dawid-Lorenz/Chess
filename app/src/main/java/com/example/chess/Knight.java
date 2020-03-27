package com.example.chess;

public class Knight extends Piece {
    private double value = 330;

    public Knight(boolean colour) {
        super(colour);
    }

    public Knight(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    @Override
    public double getValue()
    {
        return value;
    }
    @Override
    public boolean isMoveAllowed(byte x, byte y) {
        return ((getX() - x == 1 || x - getX() == 1) && (getY() - y == 2 || y - getY() == 2))
                ||
                ((getX() - x == 2 || x - getX() == 2) && (getY() - y == 1 || y - getY() == 1));
    }
}