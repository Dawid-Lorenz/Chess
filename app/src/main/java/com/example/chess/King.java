package com.example.chess;

public class King extends Piece {

    private int value = 10000;
    public boolean moved = false;

    public King(boolean colour){
        super(colour);
    }

    public King(byte x, byte y, boolean colour){
        super(x,y,colour);
    }

    @Override
    public int getValue()
    {
        return value;
    }
    @Override
    public boolean isMoveAllowed(byte x, byte y)
    {
        return (x >= 0 && x < 8 && y >= 0 && y < 8) &&
                ((getX() == x && (getY() - y == 1 || y - getY() == 1) ) ||
                        ((getX() - x == 1 || x - getX() == 1) && getY() == y) ||
                        (getX() - x == 1 && (getY() - y == 1 || y - getY() == 1)) ||
                        (x - getX() == 1 && (getY() - y == 1 || y - getY() == 1)));
    }
}
