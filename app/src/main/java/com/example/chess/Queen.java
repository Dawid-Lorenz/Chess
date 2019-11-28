package com.example.chess;

public class Queen extends Piece {
    private short value = 9;

    public Queen(boolean colour) {
        super(colour);
    }

    public Queen(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    @Override
    public boolean isMoveAllowed(byte x, byte y) {
        return (getX() != x && getY() != y &&
                ((getX() - x == getY() - y) || (getX() - x == y - getY())))
                ||
                (x >= 0 && x < 8 && y >= 0 && y < 8 &&
                ((getX() == x && getY() != y) || (getX() != x && getY() == y)));
    }
}
