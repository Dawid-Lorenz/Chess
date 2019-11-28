package com.example.chess;

public class Pawn extends Piece {
    private short value = 8;

    public Pawn(boolean colour) {
        super(colour);
    }

    public Pawn(byte x, byte y, boolean colour){
        super(x, y, colour);
    }

    @Override
    public boolean isMoveAllowed(byte x, byte y)
    {
        if (isColour())
            return getY() == y && ((getX() - x == 1) || (getX() == 6 && getX() - x == 2));
        else
           return getY() == y && ((x - getX() == 1) || (getX() == 1 && x - getX() == 2));
    }

    @Override
    public boolean isAttackAllowed(byte x, byte y)
    {
        if (isColour())
            return getX() - x == 1 && (getY() - y == 1 || getY() - y == -1);
        else
            return getX() - x == -1 && (getY() - y == 1 || getY() - y == -1);
    }
}
