package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Iterator;

public class Player extends AppCompatActivity {

    ArrayList<Piece> pieces = new ArrayList<>();
    ArrayList<Piece> taken = new ArrayList<>();

    ImageButton[][] board = new ImageButton[8][8];

    Piece nuller = new Piece();
    Piece enPassantPosition = nuller;
//    Piece enPassant = new Piece();
    Piece selected = nuller;

    boolean player = true;

    King kingW, kingB;

    int counter = 0;

    private void updateTheBoard()
    {
        for (ImageButton[] i: board)
        {
            for (ImageButton j: i)
            {
                if(counter % 2 == 0)
                    j.setBackgroundResource(R.drawable.white);
                else
                    j.setBackgroundResource(R.drawable.black);
                counter++;
                j.setImageResource(R.drawable.empty);
            }
            counter++;

        }
        for( Piece p : pieces)
        {
            if(p == null)
            {
                continue;
            }
            byte x = p.getX();
            byte y = p.getY();
            if (p instanceof King)
                if (p.isColour())
                    board[x][y].setImageResource(R.drawable.king_white);
                else
                    board[x][y].setImageResource(R.drawable.king_black);
            else if (p instanceof Rook)
                if (p.isColour())
                    board[x][y].setImageResource(R.drawable.rook_white);
                else
                    board[x][y].setImageResource(R.drawable.rook_black);
            else if (p instanceof Bishop)
                if (p.isColour())
                    board[x][y].setImageResource(R.drawable.bishop_white);
                else
                    board[x][y].setImageResource(R.drawable.bishop_black);
            else if (p instanceof Knight)
                if (p.isColour())
                    board[x][y].setImageResource(R.drawable.knight_white);
                else
                    board[x][y].setImageResource(R.drawable.knight_black);
            else if (p instanceof Queen)
                if (p.isColour())
                    board[x][y].setImageResource(R.drawable.queen_white);
                else
                    board[x][y].setImageResource(R.drawable.queen_black);
            else if (p instanceof Pawn)
                if (p.isColour())
                    board[x][y].setImageResource(R.drawable.pawn_white);
                else
                    board[x][y].setImageResource(R.drawable.pawn_black);
        }

        if (selected != nuller)
            board[selected.getX()][selected.getY()].setBackgroundResource(R.drawable.selected);

    }

    private String toChessPos(byte x, byte y)
    {
        String out = "";

        char c = (char)('a' + y + 1);
        out += c;
        out += (x + 1);

        return out;
    }

    private byte[] toCoord(String pos)
    {
        char c = pos.charAt(0);
        byte y = (byte)(c - 'a');
        byte x = (byte)(7 - (pos.charAt(1) - '1'));

        byte[] ans = {x, y};

        return ans;
    }

    private void removeTaken()
    {
        Iterator<Piece> iter = pieces.iterator();
        Piece p;
        while(iter.hasNext())
        {
            p = iter.next();
            if (p.getX() == -1 || p.getY() == -1)
            {
                taken.add(p);
            }
        }
        pieces.removeAll(taken);
        pieces.trimToSize();
    }

    private boolean isInCheck(boolean player)
    {
        King checked = player ? kingW : kingB;
        for (Piece p : pieces)
        {
            if (player != p.isColour())
                if ((p instanceof Pawn || p instanceof Knight || p instanceof King) &&
                        p.isAttackAllowed(checked.getX(),checked.getY()))
                    return true;
                else if ((p instanceof Bishop || p instanceof Rook || p instanceof Queen)
                    && p.isAttackAllowed(checked.getX(),checked.getY()))
                {
                    byte diffX = (byte)(p.getX() - checked.getX());
                    byte diffY = (byte)(p.getY() - checked.getY());

                    byte dx = (byte)(diffX >= 0 ? (diffX == 0 ? 0 : 1) : -1);
                    byte dy = (byte)(diffY >= 0 ? (diffY == 0 ? 0 : 1) : -1);

                    diffX = (byte)Math.abs(diffX);
                    diffY = (byte)Math.abs(diffY);

                    boolean blocked = false;

//                    if ((diffX > 1 || diffX < -1) && (diffY > 1 || diffY < -1))
                        for (Piece blocker : pieces)
                        {
                            if (blocker != checked && blocker != p)
                            {
                                byte biffX = (byte)(p.getX() - blocker.getX());
                                byte biffY = (byte)(p.getY() - blocker.getY());

                                byte bx = (byte)(biffX >= 0 ? (biffX == 0 ? 0 : 1) : -1);
                                byte by = (byte)(biffY >= 0 ? (biffY == 0 ? 0 : 1) : -1);

                                biffX = (byte)Math.abs(biffX);
                                biffY = (byte)Math.abs(biffY);

                                boolean coordCheck = bx == dx && by == dy && biffX <= diffX && biffY <= diffY;

                                if (p instanceof Bishop) {
                                    if (biffX == biffY)
                                        if (coordCheck)
                                            blocked = true;
                                }
                                else if (p instanceof Rook) {
                                    if (biffX == 0 || biffY == 0)
                                        if (coordCheck)
                                            blocked = true;
                                }
                                else
                                {
                                    if (biffX == biffY || biffX == 0 || biffY == 0)
                                        if (coordCheck)
                                            blocked = true;

                                }


                            }
                        }

                    if (!blocked)
                        return true;

                }
        }

        return false;
    }

    private boolean isMoveLegal(boolean player, @org.jetbrains.annotations.NotNull Piece p, ArrayList<Piece> pieces, byte x, byte y)
    {
        if (p instanceof King)
        {
            if (player != p.isColour())
                return false;

            if ((player && x == 7 || !player && x == 0) && (y == 2 || y == 6) &&  !((King) p).moved && !isInCheck(player))
            {
                byte firstRank = player ? (byte)7 : 0;
                boolean canCastle = true;
                Rook rook = new Rook(player);
                for (Piece blocker : pieces)
                {
                    if(y == 2)
                    {
                        if (blocker.getX() == firstRank && (blocker.getY() > 0 && blocker.getY() <= 3))
                        {
                            return false;
                        }
                        if (blocker instanceof Rook && blocker.getY() == 0)
                            rook = (Rook) blocker;
                    }
                    else
                    {
                        if (blocker.getX() == firstRank && (blocker.getY() == 5 || blocker.getY() == 6))
                        {
                            return false;
                        }
                        if (blocker instanceof Rook && blocker.getY() == 7)
                            rook = (Rook) blocker;
                    }
                }

                if (rook.moved)
                    return false;

                if (y == 2)
                {
                    selected.setY((byte)3);
                    if (isInCheck(player))
                    {
                        selected.setY((byte)4);
                        return false;
                    }
                    selected.setY((byte)2);
                    if (isInCheck(player))
                    {
                        selected.setY((byte)4);
                        return false;
                    }
                    selected.setY((byte)4);
                    return true;
                }
                else
                {
                    selected.setY((byte)5);
                    if (isInCheck(player))
                    {
                        selected.setY((byte)4);
                        return false;
                    }
                    selected.setY((byte)6);
                    if (isInCheck(player))
                    {
                        selected.setY((byte)4);
                        return false;
                    }
                    selected.setY((byte)4);
                    return true;
                }
            }
        }
        else if (!p.isMoveAllowed(x, y) && !p.isAttackAllowed(x, y) || player != p.isColour())
            return false;
        else if (enPassantPosition != nuller && p instanceof Pawn)
        {
            if (enPassantPosition.getX() == x && enPassantPosition.getY() == y)
                return p.isAttackAllowed(x,y);
        }
        else if ((p instanceof Bishop || p instanceof Rook || p instanceof Queen || p instanceof Pawn))
        {
            byte diffX = (byte)(p.getX() - x);
            byte diffY = (byte)(p.getY() - y);

            byte dx = (byte)(diffX >= 0 ? (diffX == 0 ? 0 : 1) : -1);
            byte dy = (byte)(diffY >= 0 ? (diffY == 0 ? 0 : 1) : -1);

            diffX = (byte)Math.abs(diffX);
            diffY = (byte)Math.abs(diffY);

            boolean blocked = false;

            if ((diffX > 1 || diffX < -1) || (diffY > 1 || diffY < -1))
                for (Piece blocker : pieces)
                {
                    if (blocker != p)
                    {
                        byte biffX = (byte)(p.getX() - blocker.getX());
                        byte biffY = (byte)(p.getY() - blocker.getY());

                        byte bx = (byte)(biffX >= 0 ? (biffX == 0 ? 0 : 1) : -1);
                        byte by = (byte)(biffY >= 0 ? (biffY == 0 ? 0 : 1) : -1);

                        biffX = (byte)Math.abs(biffX);
                        biffY = (byte)Math.abs(biffY);


                        boolean coordCheck = bx == dx && by == dy; // && biffX < diffX && biffY < diffY;

                        if (p instanceof Bishop) {
                            if (biffX == biffY)
                                if (coordCheck && biffX < diffX && biffY < diffY)
                                    blocked = true;
                        }
                        else if (p instanceof Rook || p instanceof Pawn) {
                            if (biffX == 0)
                            {
                                if (coordCheck && biffY < diffY)
                                    blocked = true;
                            }
                            else if (biffY == 0)
                            {
                                if (coordCheck && biffX < diffX)
                                    blocked = true;
                            }
                        }
                        else
                        {
                            if (biffX == biffY)
                            {
                                if (coordCheck && biffX < diffX && biffY < diffY)
                                    blocked = true;
                            }
                            else if (biffX == 0 || biffY == 0)
                                if (biffX == 0)
                                {
                                    if (coordCheck && biffY < diffY)
                                        blocked = true;
                                }
                                else if (biffY == 0)
                                {
                                    if (coordCheck && biffX < diffX)
                                        blocked = true;
                                }

                        }
                    }
                    if (blocked)
                        return false;
                }

        }

        int toBeRemoved = -1;
        Piece a;
        boolean attacking = false;
        for (int i = 0; i < pieces.size(); i++)
        {
            a = pieces.get(i);
            if (a.getX() == x && a.getY() == y) {
                attacking = true;
                if (a.isColour() == p.isColour() || !p.isAttackAllowed(x,y))
                    return false;
                else {
                    toBeRemoved = i;
                    a.setX((byte)-1);
                    a.setY((byte)-1);
                    break;
                }
            }
        }

        if (!attacking && !p.isMoveAllowed(x,y)) {
            if (toBeRemoved != -1) {
                pieces.get(toBeRemoved).setY(y);
                pieces.get(toBeRemoved).setX(x);
            }
            return false;
        }
        byte originalX = p.getX();
        byte originalY = p.getY();
        p.setX(x);
        p.setY(y);
        if (isInCheck(p.isColour()))
        {
            p.setX(originalX);
            p.setY(originalY);
            if (toBeRemoved != -1) {
                pieces.get(toBeRemoved).setY(y);
                pieces.get(toBeRemoved).setX(x);
            }
            return false;
        }
        else {
            p.setX(originalX);
            p.setY(originalY);
            if (toBeRemoved != -1) {
                pieces.get(toBeRemoved).setY(y);
                pieces.get(toBeRemoved).setX(x);
            }
            return true;
        }
    }

    private void makeMove(ArrayList<Piece> pieces, byte tempX, byte tempY)
    { // TODO promotion (somewhere xD)
        boolean resetEnPassant = true;

        byte x = tempX;
        byte y = tempY;

        if (selected instanceof Pawn)
        {
            if (selected.isColour() && selected.getX() == 6 && tempX == 4)
            {
                enPassantPosition = new Piece();
                enPassantPosition.setX((byte)5);
                enPassantPosition.setY(selected.getY());
                resetEnPassant = false;
//                                        enPassantPosition.setColour(true);
            }
            else if (!selected.isColour() && selected.getX() == 1 && tempX == 3)
            {
                enPassantPosition = new Piece();
                enPassantPosition.setX((byte)2);
                enPassantPosition.setY(selected.getY());
                resetEnPassant = false;
//                                        enPassantPosition.setColour(false);
            }
            else if (enPassantPosition != nuller && enPassantPosition.getX() == tempX && enPassantPosition.getY() == tempY)
            {
                if (player)
                    x = (byte)3;
                else
                    x = (byte)4;

            }
        }


        if (selected instanceof King)
        {
            byte firstRank;
            if (selected.isColour())
            {
                firstRank = 7;
            }
            else
            {
                firstRank = 0;
            }

            if (tempX == firstRank && tempY == 2)
            {
                for (Piece p : pieces)
                    if (p instanceof Rook && p.getX() == firstRank && p.getY() == 0)
                    {
                        p.setY((byte)3);
                    }
            }
            else if (tempX == firstRank && tempY == 6)
            {
                for (Piece p : pieces)
                    if (p instanceof Rook && p.getX() == firstRank && p.getY() == 7)
                    {
                        p.setY((byte)5);
                    }

            }
            ((King) selected).moved = true;
        }
        else if (selected instanceof Rook)
        {
            ((Rook) selected).moved = true;
        }

        selected.setX(tempX);
        selected.setY(tempY);

        Iterator<Piece> iterator = pieces.iterator();
        Piece p;
        while (iterator.hasNext())
        {
            p = iterator.next();
            if (p != selected && p.getX() == x && p.getY() == y)
            {
                p.setX((byte) -1);
                p.setY((byte) -1);
                break;
            }
        }

        if (resetEnPassant)
            enPassantPosition = nuller;

    }


    private boolean hasMoves(boolean player, ArrayList<Piece> pieces)
    {
        Piece p;
        Iterator<Piece> iterator = pieces.iterator();
        while (iterator.hasNext())
        {
            p = iterator.next();
            for (byte i = 0; i < 8; i++)
                for (byte j = 0; j < 8; j++)
                    if (isMoveLegal(player, p, pieces, i, j))
                    {
                        return true;
                    }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // adding Kings to the board
        String tempPos = "e8";
        byte[] coords = toCoord(tempPos);
        kingB = new King(coords[0], coords[1], false);
        pieces.add(kingB);
        tempPos = "a8";
        coords = toCoord(tempPos);
        pieces.add(new Rook(coords[0], coords[1], false));
        tempPos = "b8";
        coords = toCoord(tempPos);
        pieces.add(new Knight(coords[0], coords[1], false));
        tempPos = "c8";
        coords = toCoord(tempPos);
        pieces.add(new Bishop(coords[0], coords[1], false));
        tempPos = "d8";
        coords = toCoord(tempPos);
        pieces.add(new Queen(coords[0], coords[1], false));
        tempPos = "f8";
        coords = toCoord(tempPos);
        pieces.add(new Bishop(coords[0], coords[1], false));
        tempPos = "g8";
        coords = toCoord(tempPos);
        pieces.add(new Knight(coords[0], coords[1], false));
        tempPos = "h8";
        coords = toCoord(tempPos);
        pieces.add(new Rook(coords[0], coords[1], false));
        for (int i = 0; i < 8; i++)
        {
            tempPos = ((char)('a' + i) + "7");
            coords = toCoord(tempPos);
            pieces.add(new Pawn(coords[0], coords[1], false));
        }


        tempPos = "e1";
        coords = toCoord(tempPos);
        kingW = new King(coords[0], coords[1], true);
        pieces.add(kingW);
        tempPos = "a1";
        coords = toCoord(tempPos);
        pieces.add(new Rook(coords[0], coords[1], true));
        tempPos = "b1";
        coords = toCoord(tempPos);
        pieces.add(new Knight(coords[0], coords[1], true));
        tempPos = "c1";
        coords = toCoord(tempPos);
        pieces.add(new Bishop(coords[0], coords[1], true));
        tempPos = "d1";
        coords = toCoord(tempPos);
        pieces.add(new Queen(coords[0], coords[1], true));
        tempPos = "f1";
        coords = toCoord(tempPos);
        pieces.add(new Bishop(coords[0], coords[1], true));
        tempPos = "g1";
        coords = toCoord(tempPos);
        pieces.add(new Knight(coords[0], coords[1], true));
        tempPos = "h1";
        coords = toCoord(tempPos);
        pieces.add(new Rook(coords[0], coords[1], true));
        for (int i = 0; i < 8; i++)
        {
            tempPos = ((char)('a' + i) + "2");
            coords = toCoord(tempPos);
            pieces.add(new Pawn(coords[0], coords[1], true));
        }



        TableLayout table = findViewById(R.id.board);
        TableRow row;
        for (byte i = 0; i < 8; i++)
        {
            row = (TableRow) table.getChildAt(i);
            for (byte j = 0; j < 8; j++)
            {
                board[i][j] = (ImageButton) row.getChildAt(j);
            }
        }

        updateTheBoard();

        for (byte i = 0; i < 8; i++)
        {
            for (byte j = 0; j < 8; j++)
            {
                ImageButton btn = board[i][j];
                final byte tempX = i;
                final byte tempY = j;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selected != nuller)
                        {
                            if(isMoveLegal(player, selected, pieces, tempX, tempY))
                            {

                                /*
                                boolean resetEnPassant = true;

                                byte x = tempX;
                                byte y = tempY;

                                if (selected instanceof Pawn)
                                {
                                    if (selected.isColour() && selected.getX() == 6 && tempX == 4)
                                    {
                                        enPassantPosition = new Piece();
                                        enPassantPosition.setX((byte)5);
                                        enPassantPosition.setY(selected.getY());
                                        resetEnPassant = false;
//                                        enPassantPosition.setColour(true);
                                    }
                                    else if (!selected.isColour() && selected.getX() == 1 && tempX == 3)
                                    {
                                        enPassantPosition = new Piece();
                                        enPassantPosition.setX((byte)2);
                                        enPassantPosition.setY(selected.getY());
                                        resetEnPassant = false;
//                                        enPassantPosition.setColour(false);
                                    }
                                    else if (enPassantPosition != nuller && enPassantPosition.getX() == tempX && enPassantPosition.getY() == tempY)
                                    {
                                        if (player)
                                            x = (byte)3;
                                        else
                                            x = (byte)4;

                                    }
                                }

                                selected.setX(tempX);
                                selected.setY(tempY);

                                boolean moves = false;
                                Iterator<Piece> iterator = pieces.iterator();
                                Piece p;
                                while (iterator.hasNext())
                                {
                                    p = iterator.next();
                                    if (p != selected && p.getX() == x && p.getY() == y)
                                    {
                                        p.setX((byte) -1);
                                        p.setY((byte) -1);
                                        break;
                                    }
                                }
                                iterator = pieces.iterator();
                                */


                                makeMove(pieces, tempX, tempY);


                                Iterator<Piece> iterator = pieces.iterator();

                                if (isInCheck(!player))
                                {

                                    if(!hasMoves(!player, pieces))
                                    {
                                        CharSequence message;
                                        if (player)
                                            message = "Check mate! White has won!";
                                        else
                                            message = "Check mate! Black has won!";
                                        new AlertDialog.Builder(Player.this)
                                                .setTitle("Game over")
                                                .setMessage(message)
                                                .setNeutralButton("Show the board", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        startActivity(new Intent(Player.this, MainActivity.class));
                                                    }
                                                })
                                                .show();
                                    }
                                }
                                else if (!hasMoves(!player, pieces))
                                    new AlertDialog.Builder(Player.this)
                                            .setTitle("Game over")
                                            .setMessage("Stalemate!")
                                            .setNeutralButton("Show the board", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(Player.this, MainActivity.class));
                                                }
                                            })
                                            .show();

                                player = !player;
                            }


                            selected = nuller;
                        }
                        else
                        {
                            for (Piece p : pieces)
                            {
                                if (player == p.isColour() && p.getX() == tempX && p.getY() == tempY){
                                    selected = p;
                                    break;
                                }
                            }
                        }
                        removeTaken();
                        updateTheBoard();
                    }
                });
            }
        }



    }
}
