package com.example.chess;

public class Piece {

    private byte x, y;
    private boolean colour, selected;
    private int value;
    private int defense;

    public Piece(){}

    public Piece(boolean colour){
        this.colour = colour;
    }

    public Piece(byte x, byte y, boolean colour) {
        this.colour = colour;
        this.x = x;
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public byte getX() {
        return x;
    }

    public void setX(byte x) {
        this.x = x;
    }

    public byte getY() {
        return y;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setColour(boolean colour) {
        this.colour = colour;
    }

    public boolean isColour() {
        return colour;
    }

    public void setY(byte y) {
        this.y = y;
    }

    public boolean isMoveAllowed(byte x, byte y)
    {
        return true;
    }

    public boolean isAttackAllowed(byte x, byte y) {return isMoveAllowed(x,y);}

    public void addDefender() {defense++;}

    public void addAttacker() {defense--;}

    public int getDefense() {return defense;}

    public void setDefense(int defense) {this.defense = defense;}
}

