package com.example.chess;

public class Piece {

    private byte x, y;
    private boolean colour;
    private double value;
    private int defense;
    private int attack;
    private double defense_value;
    private double attack_value;

    public Piece(){}

    public Piece(boolean colour){
        this.colour = colour;
    }

    public Piece(byte x, byte y, boolean colour) {
        this.colour = colour;
        this.x = x;
        this.y = y;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public double getValue() {
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

    public void addDefender(Piece p) {defense++; defense_value += p.getValue();}

    public void addAttacker(Piece p) {attack++; attack_value += p.getValue();}

    public int getDefense() {return defense;}

    public void setDefense(int defense) {this.defense = defense;}

    public void setDefense_value(double value) {defense_value = value;}

    public double getDefense_value()
    {
        return defense_value;
    }

    public double getAttack_value()
    {
        return attack_value;
    }

    public int getAttack()
    {
        return attack;
    }

    public void setAttack(int attack)
    {
        this.attack = attack;
    }

    public void setAttack_value(double attack_value)
    {
        this.attack_value = attack_value;
    }

}

