package com.example.chess;

public class Rook extends Piece {
    private short value = 5;

    public Rook(boolean colour) {
        super(colour);
    }

    public Rook(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    @Override
    public boolean isMoveAllowed(byte x, byte y)
    {
        return (x >= 0 && x < 8 && y >= 0 && y < 8 &&
                ((getX() == x && getY() != y) || (getX() != x && getY() == y)));
    }
}