package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class Minimax extends AppCompatActivity
{

    static double [][] boardValues = {
            {0.5, 1, 1, 1, 1, 1, 1, 0.5},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 3, 3, 3, 3, 1, 1},
            {1, 1, 3.5, 6, 6, 3.5, 1, 1},
            {1, 1, 3.5, 6, 6, 3.5, 1, 1},
            {1, 1, 3, 3, 3, 3, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0.5, 1, 1, 1, 1, 1, 1, 0.5}
    };

    static int [][] pieceSquareTable =
            {
                    { // pawn
                            0,  0,  0,  0,  0,  0,  0,  0,
                            50, 50, 50, 50, 50, 50, 50, 50,
                            10, 10, 20, 30, 30, 20, 10, 10,
                            5,  5, 10, 25, 25, 10,  5,  5,
                            0,  0,  0, 20, 20,  0,  0,  0,
                            5, -5,-10,  0,  0,-10, -5,  5,
                            5, 10, 10,-20,-20, 10, 10,  5,
                            0,  0,  0,  0,  0,  0,  0,  0
                    },
                    { // knight
                            -50,-40,-30,-30,-30,-30,-40,-50,
                            -40,-20,  0,  0,  0,  0,-20,-40,
                            -30,  0, 10, 15, 15, 10,  0,-30,
                            -30,  5, 15, 20, 20, 15,  5,-30,
                            -30,  0, 15, 20, 20, 15,  0,-30,
                            -30,  5, 10, 15, 15, 10,  5,-30,
                            -40,-20,  0,  5,  5,  0,-20,-40,
                            -50,-40,-30,-30,-30,-30,-40,-50,
                    },
                    { // bishop
                            -20,-10,-10,-10,-10,-10,-10,-20,
                            -10,  0,  0,  0,  0,  0,  0,-10,
                            -10,  0,  5, 10, 10,  5,  0,-10,
                            -10,  5,  5, 10, 10,  5,  5,-10,
                            -10,  0, 10, 10, 10, 10,  0,-10,
                            -10, 10, 10, 10, 10, 10, 10,-10,
                            -10,  5,  0,  0,  0,  0,  5,-10,
                            -20,-10,-10,-10,-10,-10,-10,-20,
                    },
                    { // Rook
                            0,  0,  0,  0,  0,  0,  0,  0,
                            5, 10, 10, 10, 10, 10, 10,  5,
                            -5,  0,  0,  0,  0,  0,  0, -5,
                            -5,  0,  0,  0,  0,  0,  0, -5,
                            -5,  0,  0,  0,  0,  0,  0, -5,
                            -5,  0,  0,  0,  0,  0,  0, -5,
                            -5,  0,  0,  0,  0,  0,  0, -5,
                            0,  0,  0,  5,  5,  0,  0,  0
                    },
                    { // Queen
                            -20,-10,-10, -5, -5,-10,-10,-20,
                            -10,  0,  0,  0,  0,  0,  0,-10,
                            -10,  0,  5,  5,  5,  5,  0,-10,
                            -5,  0,  5,  5,  5,  5,  0, -5,
                            0,  0,  5,  5,  5,  5,  0, -5,
                            -10,  5,  5,  5,  5,  5,  0,-10,
                            -10,  0,  5,  0,  0,  0,  0,-10,
                            -20,-10,-10, -5, -5,-10,-10,-20
                    },
                    { // King (middle game)
                            -30,-40,-40,-50,-50,-40,-40,-30,
                            -30,-40,-40,-50,-50,-40,-40,-30,
                            -30,-40,-40,-50,-50,-40,-40,-30,
                            -30,-40,-40,-50,-50,-40,-40,-30,
                            -20,-30,-30,-40,-40,-30,-30,-20,
                            -10,-20,-20,-20,-20,-20,-20,-10,
                            20, 20,  0,  0,  0,  0, 20, 20,
                            20, 30, 10,  0,  0, 10, 30, 20
                    }
            };

    ImageButton[][] boardButtons = new ImageButton[8][8];

    Board board;
    Tree library;
    Move lastMove = null;
    int MAX_DEPTH = 2;

    protected static void updateTheBoard(Board board, ImageButton[][] boardButtons)
    {
        int counter = 0;
        for (ImageButton[] i : boardButtons)
        {
            for (ImageButton j : i)
            {
                if (counter % 2 == 0)
                    j.setBackgroundResource(R.drawable.white);
                else
                    j.setBackgroundResource(R.drawable.black);
                counter++;
                j.setImageResource(R.drawable.empty);
            }
            counter++;

        }
        for (Piece p : board.pieces)
        {
            if (p == null)
            {
                continue;
            }
            byte x = p.getX();
            byte y = p.getY();
            if (x >= 0 && x < 8 && y >= 0 && y < 8)
            {
                if (p instanceof King)
                    if (p.isColour())
                        boardButtons[x][y].setImageResource(R.drawable.king_white);
                    else
                        boardButtons[x][y].setImageResource(R.drawable.king_black);
                else if (p instanceof Rook)
                    if (p.isColour())
                        boardButtons[x][y].setImageResource(R.drawable.rook_white);
                    else
                        boardButtons[x][y].setImageResource(R.drawable.rook_black);
                else if (p instanceof Bishop)
                    if (p.isColour())
                        boardButtons[x][y].setImageResource(R.drawable.bishop_white);
                    else
                        boardButtons[x][y].setImageResource(R.drawable.bishop_black);
                else if (p instanceof Knight)
                    if (p.isColour())
                        boardButtons[x][y].setImageResource(R.drawable.knight_white);
                    else
                        boardButtons[x][y].setImageResource(R.drawable.knight_black);
                else if (p instanceof Queen)
                    if (p.isColour())
                        boardButtons[x][y].setImageResource(R.drawable.queen_white);
                    else
                        boardButtons[x][y].setImageResource(R.drawable.queen_black);
                else if (p instanceof Pawn)
                    if (p.isColour())
                        boardButtons[x][y].setImageResource(R.drawable.pawn_white);
                    else
                        boardButtons[x][y].setImageResource(R.drawable.pawn_black);
            }
        }

        if (board.selected != board.nuller)
            boardButtons[board.selected.getX()][board.selected.getY()].setBackgroundResource(R.drawable.selected);

    }

    private String toChessPos(byte x, byte y)
    {
        String out = "";

        char c = (char) ('a' + y + 1);
        out += c;
        out += (x + 1);

        return out;
    }

    protected static byte[] toCoord(String pos)
    {
        char c = pos.charAt(0);
        byte y = (byte) (c - 'a');
        byte x = (byte) (7 - (pos.charAt(1) - '1'));

        byte[] ans = {x, y};

        return ans;
    }

    protected static void removeTaken(Board board)
    {
        Iterator<Piece> iter = board.pieces.iterator();
        Piece p;
        while (iter.hasNext())
        {
            p = iter.next();
            if (p.getX() == -1 || p.getY() == -1)
            {
                board.taken.add(p);
            }
        }
        board.pieces.removeAll(board.taken);
        board.pieces.trimToSize();
    }

    protected static boolean isInCheck(boolean player, ArrayList<Piece> pieces)
    {
        King checked = new King((byte)-1,(byte)-1, player);
        for (Piece p : pieces)
        {
            if (p instanceof King && p.isColour() == player)
                checked = (King) p;
        }
        for (Piece p : pieces)
        {
            if (player != p.isColour() && p.getX() != -1 && p.getY() != -1)
                if ((p instanceof Pawn || p instanceof Knight || p instanceof King) &&
                        p.isAttackAllowed(checked.getX(), checked.getY()))
                    return true;
                else if ((p instanceof Bishop || p instanceof Rook || p instanceof Queen)
                        && p.isAttackAllowed(checked.getX(), checked.getY()))
                {
                    byte diffX = (byte) (p.getX() - checked.getX());
                    byte diffY = (byte) (p.getY() - checked.getY());

                    byte dx = (byte) (diffX >= 0 ? (diffX == 0 ? 0 : 1) : -1);
                    byte dy = (byte) (diffY >= 0 ? (diffY == 0 ? 0 : 1) : -1);

                    diffX = (byte) Math.abs(diffX);
                    diffY = (byte) Math.abs(diffY);

                    boolean blocked = false;

//                    if ((diffX > 1 || diffX < -1) && (diffY > 1 || diffY < -1))
                    for (Piece blocker : pieces)
                    {
                        if (blocker != checked && blocker != p)
                        {
                            byte biffX = (byte) (p.getX() - blocker.getX());
                            byte biffY = (byte) (p.getY() - blocker.getY());

                            byte bx = (byte) (biffX >= 0 ? (biffX == 0 ? 0 : 1) : -1);
                            byte by = (byte) (biffY >= 0 ? (biffY == 0 ? 0 : 1) : -1);

                            biffX = (byte) Math.abs(biffX);
                            biffY = (byte) Math.abs(biffY);

                            boolean coordCheck = bx == dx && by == dy && biffX <= diffX && biffY <= diffY;

                            if (p instanceof Bishop)
                            {
                                if (biffX == biffY)
                                    if (coordCheck)
                                        blocked = true;
                            }
                            else if (p instanceof Rook)
                            {
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

    protected static boolean isMoveLegal(Board board, Piece p, byte x, byte y)
    {
        for (Piece next : board.pieces)
            if (next.getX() == p.getX() && next.getY() == p.getY())
            {
                p = next;
                break;
            }
        if (x < 0 || x >= 8 || y < 0 || y >= 8)
            return false;
//        pieces = copyList(pieces);
        if (p instanceof King)
        {
            if (board.player != p.isColour())
                return false;

            if ((board.player && x == 7 || !board.player && x == 0) && (y == 2 || y == 6) && !((King) p).moved && !isInCheck(board.player, board.pieces))
            {
                byte firstRank = board.player ? (byte) 7 : 0;
                boolean canCastle = true;
                Rook rook = new Rook(board.player);
                Iterator<Piece> iterator = board.pieces.iterator();
                Piece blocker;
                while (iterator.hasNext())
                {
                    blocker = iterator.next();
                    if (y == 2)
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
                    board.selected.setY((byte) 3);
                    if (isInCheck(board.player, board.pieces))
                    {
                        board.selected.setY((byte) 4);
                        return false;
                    }
                    board.selected.setY((byte) 2);
                    if (isInCheck(board.player, board.pieces))
                    {
                        board.selected.setY((byte) 4);
                        return false;
                    }
                    board.selected.setY((byte) 4);
                    return true;
                }
                else
                {
                    board.selected.setY((byte) 5);
                    if (isInCheck(board.player, board.pieces))
                    {
                        board.selected.setY((byte) 4);
                        return false;
                    }
                    board.selected.setY((byte) 6);
                    if (isInCheck(board.player, board.pieces))
                    {
                        board.selected.setY((byte) 4);
                        return false;
                    }
                    board.selected.setY((byte) 4);
                    return true;
                }
            }
        }
        else if (!p.isMoveAllowed(x, y) && !p.isAttackAllowed(x, y) || board.player != p.isColour())
            return false;
        else if (board.enPassantPosition != board.nuller && p instanceof Pawn)
        {
            if (board.enPassantPosition.getX() == x && board.enPassantPosition.getY() == y)
                return p.isAttackAllowed(x, y);
        }
        else if ((p instanceof Bishop || p instanceof Rook || p instanceof Queen || p instanceof Pawn))
        {
            byte diffX = (byte) (p.getX() - x);
            byte diffY = (byte) (p.getY() - y);

            byte dx = (byte) (diffX >= 0 ? (diffX == 0 ? 0 : 1) : -1);
            byte dy = (byte) (diffY >= 0 ? (diffY == 0 ? 0 : 1) : -1);

            diffX = (byte) Math.abs(diffX);
            diffY = (byte) Math.abs(diffY);

            boolean blocked = false;

            Iterator<Piece> iterator = board.pieces.iterator();
            Piece blocker;

            if ((diffX > 1 || diffX < -1) || (diffY > 1 || diffY < -1))
                while (iterator.hasNext())
                {
                    blocker = iterator.next();
                    if (blocker != p)
                    {
                        byte biffX = (byte) (p.getX() - blocker.getX());
                        byte biffY = (byte) (p.getY() - blocker.getY());

                        byte bx = (byte) (biffX >= 0 ? (biffX == 0 ? 0 : 1) : -1);
                        byte by = (byte) (biffY >= 0 ? (biffY == 0 ? 0 : 1) : -1);

                        biffX = (byte) Math.abs(biffX);
                        biffY = (byte) Math.abs(biffY);


                        boolean coordCheck = bx == dx && by == dy; // && biffX < diffX && biffY < diffY;

                        if (p instanceof Bishop)
                        {
                            if (biffX == biffY)
                                if (coordCheck && biffX < diffX && biffY < diffY)
                                    blocked = true;
                        }
                        else if (p instanceof Rook || p instanceof Pawn)
                        {
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

        Piece toBeRemoved = board.nuller;
        Piece a;
        boolean attacking = false;
        Iterator<Piece> iterator = board.pieces.iterator();
        while (iterator.hasNext())
        {
            a = iterator.next();
            if (a.getX() == x && a.getY() == y)
            {
                attacking = true;
                if (a.isColour() == p.isColour() || !p.isAttackAllowed(x, y) || a instanceof King)
                    return false;
                else
                {
                    toBeRemoved = a;
                    a.setX((byte) -1);
                    a.setY((byte) -1);
                    break;
                }
            }
        }

        if (!attacking && !p.isMoveAllowed(x, y))
        {
            if (toBeRemoved != board.nuller)
            {
                toBeRemoved.setY(y);
                toBeRemoved.setX(x);
            }
            return false;
        }
        byte originalX = p.getX();
        byte originalY = p.getY();
        p.setX(x);
        p.setY(y);
        if (isInCheck(p.isColour(), board.pieces))
        {
            p.setX(originalX);
            p.setY(originalY);
            if (toBeRemoved != board.nuller)
            {
                toBeRemoved.setY(y);
                toBeRemoved.setX(x);
            }
            return false;
        }
        else
        {
            p.setX(originalX);
            p.setY(originalY);
            if (toBeRemoved != board.nuller)
            {
                toBeRemoved.setY(y);
                toBeRemoved.setX(x);
            }
            return true;
        }
    }

    protected static boolean makeMove(Board board, Piece moving, byte tempX, byte tempY)
    {

        for (Piece p : board.pieces)
            if (p.getX() == moving.getX() && p.getY() == moving.getY())
            {
                moving = p;
                break;
            }

        Piece takenPiece = null;

        boolean resetEnPassant = true;

        byte x = tempX;
        byte y = tempY;

        if (moving instanceof Pawn)
        {
            // next to if's prepare en passant:
            if (moving.isColour() && moving.getX() == 6 && tempX == 4)
            {
                board.enPassantPosition = new Piece();
                board.enPassantPosition.setX((byte) 5);
                board.enPassantPosition.setY(moving.getY());
                resetEnPassant = false;
//                                        enPassantPosition.setColour(true);
            }
            else if (!moving.isColour() && moving.getX() == 1 && tempX == 3)
            {
                board.enPassantPosition = new Piece();
                board.enPassantPosition.setX((byte) 2);
                board.enPassantPosition.setY(moving.getY());
                resetEnPassant = false;
//                                        enPassantPosition.setColour(false);
            }
            else if (board.enPassantPosition != board.nuller && board.enPassantPosition.getX() == tempX && board.enPassantPosition.getY() == tempY)
            {
                // find the pawn that needs to be removed:
                if (board.player)
                    x = (byte) 3;
                else
                    x = (byte) 4;

            }
            else if ((moving.isColour() && tempX == 0) || (!moving.isColour() && tempX == 7))
            {
                // promotion to Queen here:
                boolean colourSelected = moving.isColour();
                // remove the pawn:
                moving.setX((byte) -1);
                moving.setY((byte) -1);
//                pieces.remove(moving);
                // add the Queen:
                moving = new Queen(colourSelected);
                board.pieces.add(moving);
            }
        }


        if (moving instanceof King)
        {
            byte firstRank;
            if (moving.isColour())
            {
                firstRank = 7;
            }
            else
            {
                firstRank = 0;
            }

            if (tempX == firstRank && tempY == 2)
            {
                if (!((King) moving).moved)
                    for (Piece p : board.pieces)
                        if (p instanceof Rook && p.getX() == firstRank && p.getY() == 0 && !((Rook) p).moved)
                        {
                            p.setY((byte) 3);
                        }
            }
            else if (tempX == firstRank && tempY == 6)
            {
                if (!((King) moving).moved)
                    for (Piece p : board.pieces)
                        if (p instanceof Rook && p.getX() == firstRank && p.getY() == 7 && !((Rook) p).moved)
                        {
                            p.setY((byte) 5);
                        }

            }
            ((King) moving).moved = true;
        }
        else if (moving instanceof Rook)
        {
            ((Rook) moving).moved = true;
        }

        moving.setX(tempX);
        moving.setY(tempY);

        boolean wasRemoved = false;

        Iterator<Piece> iterator = board.pieces.iterator();
        Piece p;
        while (iterator.hasNext())
        {
            p = iterator.next();
            if (p != moving && p.getX() == x && p.getY() == y)
            {
                p.setX((byte) -1);
                p.setY((byte) -1);
                takenPiece = p;
//                pieces.remove(p);
                board.taken.add(p);
                wasRemoved = true;
                break;
            }
        }

        if (resetEnPassant)
            board.enPassantPosition = board.nuller;

        return wasRemoved;
    }

    protected static boolean hasMoves(Board board)
    {
        Piece p;
//        pieces = copyList(pieces);
        Iterator<Piece> iterator = board.pieces.iterator();
        while (iterator.hasNext())
        {
            p = iterator.next();

            if (p instanceof Pawn)
            {
                byte pawnPassantStartingRank = board.player ? (byte) 6 : 1;
                byte pawnPassantTargetRank = board.player ? (byte) 4 : 3;
                if (p.getX() == pawnPassantStartingRank && isMoveLegal(board, p, pawnPassantTargetRank, p.getY()))
                    return true;

                byte direction = board.player ? (byte) -1 : 1;
                if (isMoveLegal(board, p, (byte) (p.getX() + direction), p.getY())
                        || isMoveLegal(board, p, (byte) (p.getX() + direction), (byte) (p.getY() + 1))
                        || isMoveLegal(board, p, (byte) (p.getX() + direction), (byte) (p.getY() - 1)))
                    return true;
            }
            else if (p instanceof Knight)
            {
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() - 2))
                        || isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() + 2))
                        || isMoveLegal(board, p, (byte) (p.getX() - 2), (byte) (p.getY() - 1))
                        || isMoveLegal(board, p, (byte) (p.getX() - 2), (byte) (p.getY() + 1))
                        || isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() - 2))
                        || isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() + 2))
                        || isMoveLegal(board, p, (byte) (p.getX() + 2), (byte) (p.getY() - 1))
                        || isMoveLegal(board, p, (byte) (p.getX() + 2), (byte) (p.getY() + 1)))
                    return true;

            }
            else if (p instanceof King)
            {
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() - 1))
                        || isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY()))
                        || isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() + 1))
                        || isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() - 1))
                        || isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() + 1))
                        || isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() - 1))
                        || isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY()))
                        || isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() + 1)))
                    return true;
            }
            else
            {
                if (!(p instanceof Rook)) // means Bishop moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(board, p, (byte) (p.getX() - posChanger), (byte) (p.getY() - posChanger))
                                || isMoveLegal(board, p, (byte) (p.getX() - posChanger), (byte) (p.getY() + posChanger))
                                || isMoveLegal(board, p, (byte) (p.getX() + posChanger), (byte) (p.getY() - posChanger))
                                || isMoveLegal(board, p, (byte) (p.getX() + posChanger), (byte) (p.getY() + posChanger)))
                            return true;
                    }
                }
                else // means Rook moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(board, p, (byte) (p.getX() - posChanger), (byte) (p.getY()))
                                || isMoveLegal(board, p, (byte) (p.getX() + posChanger), (byte) (p.getY()))
                                || isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() - posChanger))
                                || isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() + posChanger)))
                            return true;
                    }
                }

            }
        }

        return false;
    }

    protected static ArrayList<Move> listPossibleMoves(Board board)
    {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Piece p;
        Coord source, target;
        Move adder;
//        board.pieces = copyList(board.pieces);
        Iterator<Piece> iterator = board.pieces.iterator();
        while (iterator.hasNext())
        {
            p = iterator.next();

            if (p.isColour() != board.player || p.getX() == -1 || p.getY() == -1)
                continue;

            source = new Coord(p.getX(), p.getY());

            if (p instanceof Pawn)
            {
                byte pawnPassantStartingRank = board.player ? (byte) 6 : 1;
                byte pawnPassantTargetRank = board.player ? (byte) 4 : 3;
                if (p.getX() == pawnPassantStartingRank && isMoveLegal(board, p, pawnPassantTargetRank, p.getY()))
                    possibleMoves.add(new Move(source, new Coord(pawnPassantTargetRank, p.getY())));

                byte direction = board.player ? (byte) -1 : 1;
                if (isMoveLegal(board, p, (byte) (p.getX() + direction), p.getY()))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + direction, p.getY())));
                if (isMoveLegal(board, p, (byte) (p.getX() + direction), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + direction, p.getY() + 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() + direction), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + direction, p.getY() - 1)));
            }
            else if (p instanceof Knight)
            {
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() - 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() - 2)));
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() + 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() + 2)));
                if (isMoveLegal(board, p, (byte) (p.getX() - 2), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 2, p.getY() - 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() - 2), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 2, p.getY() + 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() - 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() - 2)));
                if (isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() + 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() + 2)));
                if (isMoveLegal(board, p, (byte) (p.getX() + 2), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 2, p.getY() - 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() + 2), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 2, p.getY() + 1)));

            }
            else if (p instanceof King)
            {
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() - 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY())))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY())));
                if (isMoveLegal(board, p, (byte) (p.getX() - 1), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() + 1)));
                if (isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() - 1)));
                if (isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() + 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() - 1)));
                if (isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY())))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY())));
                if (isMoveLegal(board, p, (byte) (p.getX() + 1), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() + 1)));

                if (!((King) p).moved)
                {
                    for (Piece rook : board.pieces)
                    {
                        if (rook instanceof Rook && rook.isColour() == p.isColour())
                        {
                            if (!((Rook) rook).moved && (rook.getY() == 0 || rook.getY() == 7))
                            {
                                byte side = (byte)(rook.getY() == 0 ? 2 : 6);
                                Coord kingTarget = new Coord(p.getX(), side);
                                if (isMoveLegal(board, p, p.getX(), side))
                                    possibleMoves.add(new Move(source, kingTarget));
                            }
                        }
                    }

                }
            }
            else
            {
                if (p instanceof Bishop || p instanceof Queen) // means Bishop-like moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(board, p, (byte) (p.getX() - posChanger), (byte) (p.getY() - posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() - posChanger, p.getY() - posChanger)));
                        if (isMoveLegal(board, p, (byte) (p.getX() - posChanger), (byte) (p.getY() + posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() - posChanger, p.getY() + posChanger)));
                        if (isMoveLegal(board, p, (byte) (p.getX() + posChanger), (byte) (p.getY() - posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() + posChanger, p.getY() - posChanger)));
                        if (isMoveLegal(board, p, (byte) (p.getX() + posChanger), (byte) (p.getY() + posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() + posChanger, p.getY() + posChanger)));
                    }
                }
                if (p instanceof Rook || p instanceof Queen) // means Rook-like moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(board, p, (byte) (p.getX() - posChanger), (byte) (p.getY())))
                            possibleMoves.add(new Move(source, new Coord(p.getX() - posChanger, p.getY())));
                        if (isMoveLegal(board, p, (byte) (p.getX() + posChanger), (byte) (p.getY())))
                            possibleMoves.add(new Move(source, new Coord(p.getX() + posChanger, p.getY())));
                        if (isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() - posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() - posChanger)));
                        if (isMoveLegal(board, p, (byte) (p.getX()), (byte) (p.getY() + posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() + posChanger)));
                    }
                }

            }
        }

        return possibleMoves;
    }

    // CHESS EVALUATION VALUES:
    static double CHECKMATE = 10000.0;
    static double BACKWARD_PAWN = 20.0;
    static double DOUBLED = 15.0;
    static double ISOLATED = 25.0;
    static double PAWN_CHAIN = 10.0;

    protected static double staticEvaluation(Board board)
    {
        double score = 0.0;
        int white, black;
        Iterator<Piece> iterator;
        Piece p;
        String[][] pawnStruct = new String[2][8];

        for (int j = 0; j < 8; j++)
        {
            pawnStruct[0][j] = "";
            pawnStruct[1][j] = "";
        }

        Iterator<Piece> scndIter;
        Piece other;

        /*
            CHECKMATE IS WORTH 10000
         */
        ArrayList<Move> moves = listPossibleMoves(board);
        if (isInCheck(board.player, board.pieces) && moves.isEmpty())
        {
            if (board.player)
                return -CHECKMATE;
            else
                return CHECKMATE;
        }

        /*
            STALEMATE GIVES 0
         */

        if (moves.isEmpty() && !isInCheck(board.player, board.pieces))
            return 0.0;

        // resetting defense:
        iterator = board.pieces.iterator();

        while (iterator.hasNext())
        {
            p = iterator.next();
            p.setDefense(0);
            if (p instanceof Pawn)
                ((Pawn) p).setPawnProtected(false);
        }


        /*
            MATERIAL EVALUATION
            According to Larry Kaufman with slight alteration: B > N
         */

        iterator = board.pieces.iterator();

        while (iterator.hasNext())
        {
            p = iterator.next();
            if (p.getX() != -1 && p.getY() != -1)
            {
                if (p.isColour())
                    score += (double)p.getValue();
                else
                    score -= (double)p.getValue();

                /*
                    DEFENSE EVALUATION
                    Adding defenders or attackers
                 */

                scndIter = board.pieces.iterator();

                while (scndIter.hasNext())
                {
                    other = scndIter.next();
                    if (other != p)
                    {
                        if (other.isColour() == p.isColour())
                        {       // defender:
                            // reverse the colour:
                            other.setColour(!other.isColour());

                            if (isMoveLegal(board, p, other.getX(), other.getY()))
                            {
                                other.addDefender();
                                if (other instanceof Pawn)
                                    ((Pawn) other).setPawnProtected(true);
                            }

                            // reset the colour back:
                            other.setColour(!other.isColour());
                        }
                        else
                        {       // attacker:
                            if (isMoveLegal(board, p, other.getX(), other.getY()))
                                other.addAttacker();
                        }
                    }
                }

            }
        }

        iterator = board.pieces.iterator();

        while(iterator.hasNext())
        {
            p = iterator.next();

            if (p.getDefense() < 0)
                if (p.isColour())
                    score -= p.getValue() / 2.0;
                else
                    score += p.getValue() / 2.0;
        }

        /*
            PIECE SQUARE TABLES
         */

        iterator = board.pieces.iterator();
        byte x, y;


        while (iterator.hasNext())
        {
            p = iterator.next();
            if (p.getX() == -1 || p.getY() == -1)
                continue;

            if (!p.isColour())
            {
                x = (byte)(7 - p.getX());
            }
            else
                x = p.getX();

            y = p.getY();

            double tempScore = 0.0;

            if (p instanceof Pawn)
            {
                tempScore += pieceSquareTable[0][8*x + y];
            }
            else if (p instanceof Knight)
            {
                tempScore += pieceSquareTable[1][8*x + y];
            }
            else if (p instanceof Bishop)
            {
                tempScore += pieceSquareTable[2][8*x + y];
            }
            else if (p instanceof Rook)
            {
                tempScore += pieceSquareTable[3][8 * x + y];
            }
            else if (p instanceof Queen)
            {
                tempScore += pieceSquareTable[4][8*x + y];
            }
            else if (p instanceof King)
            {
                tempScore += pieceSquareTable[5][8*x + y];
            }

            if (p.isColour())
                score += tempScore;
            else
                score -= tempScore;
        }

        /*
            PAWN STRUCTURE
         */

        iterator = board.pieces.iterator();
//        boolean isolated;

        while(iterator.hasNext())
        {
            p = iterator.next();
//            isolated  = true;

            if (p instanceof Pawn)
            {
                if (p.getY() != -1)
                    if (p.isColour())
                        pawnStruct[0][p.getY()] += p.getX();
                    else
                        pawnStruct[1][p.getY()] += p.getX();


                scndIter = board.pieces.iterator();

                if(!((Pawn) p).isPawnProtected() && isMoveLegal(board, p, (byte)(p.isColour()?p.getX()-1:p.getX()+1), p.getY()))
                {
                    // BACKWARD PAWN
                    boolean sentryFound = false;
                    boolean advanceProtected = false;
                    boolean originalPlayer;

                    // move to look for sentry:
                    x = p.getX();

                    p.setX((byte)(p.isColour() ? p.getX() - 1 : p.getX() + 1));

                    while (scndIter.hasNext())
                    {
                        other = scndIter.next();
                        if (other instanceof Pawn && other.isColour() != p.isColour())
                        {
                            originalPlayer = board.player;
                            board.player = other.isColour();
                            if (isMoveLegal(board, other, p.getX(), p.getY()))
                            {
                                sentryFound = true;
                            }

                            board.player = originalPlayer;
                        }
                        else if (other instanceof Pawn && other.isColour() == p.isColour())
                        {
                            // check if position is protected:
                            p.setColour(!p.isColour());

                            originalPlayer = board.player;
                            board.player = other.isColour();
                            if (isMoveLegal(board, other, p.getX(), p.getY()))
                            {
                                advanceProtected = true;
                            }
                            board.player = originalPlayer;

                            p.setColour(!p.isColour());
                        }

                        if (sentryFound && advanceProtected)
                            break;
                    }

                    // BACKWARD PAWN PENALTY:
                    if (sentryFound && !advanceProtected)
                        score -= BACKWARD_PAWN;


                    // reset position
                    p.setX(x);
                }

//                // ISOLATED PAWN PENALTY
//                if (isolated)
//                    score -= 50.0;

            }
        }

        boolean[] isolated = new boolean[2];

        for (int i = 0; i < 8; i++)
        {
            // DOUBLED (OR MORE) PAWNS PENALTY:
            if (board.player)
            {
                if (pawnStruct[0][i].length() > 0)
                    score -= DOUBLED * (pawnStruct[0][i].length() - 1);
                if (pawnStruct[1][i].length() > 0)
                    score += DOUBLED * (pawnStruct[1][i].length() - 1);
            }
            else
            {
                if (pawnStruct[0][i].length() > 0)
                    score += DOUBLED * (pawnStruct[0][i].length() - 1);
                if (pawnStruct[1][i].length() > 0)
                    score -= DOUBLED * (pawnStruct[1][i].length() - 1);
            }

            if (pawnStruct[0][i].length() > 0)
                isolated[0] = true;
            else
                isolated[0] = false;

            if (pawnStruct[1][i].length() > 0)
                isolated[1] = true;
            else
                isolated[1] = false;

            // ISOLATED PAWN:
            if (i >= 1)
            {
                if (pawnStruct[0][i - 1].length() > 0)
                    isolated[0] = false;
                if (pawnStruct[1][i - 1].length() > 0)
                    isolated[1] = false;
            }

            if ( i < 7)
            {
                if (pawnStruct[0][i + 1].length() > 0)
                    isolated[0] = false;
                if (pawnStruct[1][i + 1].length() > 0)
                    isolated[1] = false;
            }

            if (board.player)
            {
                if (isolated[0])
                    score -= ISOLATED;
                if (isolated[1])
                    score += ISOLATED;
            }
            else
            {
                if (isolated[0])
                    score += ISOLATED;
                if (isolated[1])
                    score -= ISOLATED;
            }

            // PAWN CHAINS:
            if (i >= 1)
            {
                byte x1, x2;
                if (pawnStruct[0][i].length() > 0)
                {
                    for (int j = 0; j < pawnStruct[0][i].length(); j++)
                    {
                        x1 = (byte)((byte)pawnStruct[0][i].charAt(j) - 48);
                        if (pawnStruct[0][i - 1].length() > 0)
                        {
                            for (int k = 0; k < pawnStruct[0][i - 1].length(); k++)
                            {
                                x2 = (byte)((byte)pawnStruct[0][i - 1].charAt(k) - 48);
                                if (x2 - x1 == 1)
                                {
                                    if (board.player)
                                        score += 50.0;
                                    else
                                        score -= 50.0;
                                }
                            }
                        }
                    }
                }
                if (pawnStruct[1][i].length() > 0)
                {
                    for (int j = 0; j < pawnStruct[1][i].length(); j++)
                    {
                        x1 = (byte)((byte)pawnStruct[1][i].charAt(j) - 48);
                        if (pawnStruct[1][i - 1].length() > 0)
                        {
                            for (int k = 0; k < pawnStruct[1][i - 1].length(); k++)
                            {
                                x2 = (byte)((byte)pawnStruct[1][i - 1].charAt(k) - 48);
                                if (x1 - x2 == 1)
                                {
                                    if (board.player)
                                        score -= PAWN_CHAIN;
                                    else
                                        score += PAWN_CHAIN;
                                }
                            }
                        }
                    }
                }
            }
            if (i < 7)
            {
                byte x1, x2;
                if (pawnStruct[0][i].length() > 0)
                {
                    for (int j = 0; j < pawnStruct[0][i].length(); j++)
                    {
                        x1 = (byte)((byte)pawnStruct[0][i].charAt(j) - 48);
                        if (pawnStruct[0][i + 1].length() > 0)
                        {
                            for (int k = 0; k < pawnStruct[0][i + 1].length(); k++)
                            {
                                x2 = (byte)((byte)pawnStruct[0][i + 1].charAt(k) - 48);
                                if (x2 - x1 == 1)
                                {
                                    if (board.player)
                                        score += PAWN_CHAIN;
                                    else
                                        score -= PAWN_CHAIN;
                                }
                            }
                        }
                    }
                }
                if (pawnStruct[1][i].length() > 0)
                {
                    for (int j = 0; j < pawnStruct[1][i].length(); j++)
                    {
                        x1 = (byte)((byte)pawnStruct[1][i].charAt(j) - 48);
                        if (pawnStruct[1][i + 1].length() > 0)
                        {
                            for (int k = 0; k < pawnStruct[1][i + 1].length(); k++)
                            {
                                x2 = (byte)((byte)pawnStruct[1][i + 1].charAt(k) - 48);
                                if (x1 - x2 == 1)
                                {
                                    if (board.player)
                                        score -= PAWN_CHAIN;
                                    else
                                        score += PAWN_CHAIN;
                                }
                            }
                        }
                    }
                }
            }

        }

        return score;
    }

    private static ArrayList<Piece> copyList(ArrayList<Piece> list)
    {
        Iterator<Piece> it = list.iterator();
        ArrayList<Piece> newList = new ArrayList<>();
        Piece p, adder;
        while (it.hasNext())
        {
            p = it.next();
            if (p instanceof Pawn)
            {
                adder = new Pawn(p.getX(), p.getY(), p.isColour());
            }
            else if (p instanceof Bishop)
            {
                adder = new Bishop(p.getX(), p.getY(), p.isColour());
            }
            else if (p instanceof Knight)
            {
                adder = new Knight(p.getX(), p.getY(), p.isColour());
            }
            else if (p instanceof Rook)
            {
                adder = new Rook(p.getX(), p.getY(), p.isColour());
                ((Rook)adder).moved = ((Rook) p).moved;
            }
            else if (p instanceof Queen)
            {
                adder = new Queen(p.getX(), p.getY(), p.isColour());
            }
            else
            {
                adder = new King(p.getX(), p.getY(), p.isColour());
                ((King)adder).moved = ((King)p).moved;
            }
            newList.add(adder);
        }
        return newList;
    }

    protected static Move alfaBeta(Board board, int maxDepth, double alfa, double beta)
    {

//        Piece bestPiece = new Piece();
//        byte bestX = -1, bestY = -1;
        Move returnValue = new Move();
        returnValue.source = new Coord(-1,-1);
//        double score;
//        pieces = copyList(pieces);
        ArrayList<Move> possibleMoves = listPossibleMoves(board);

        if (possibleMoves.isEmpty() || maxDepth == 0)
        {
            returnValue.score = staticEvaluation(board);
            return returnValue;
        }
        else if (board.player)
        {
            returnValue.score = Double.NEGATIVE_INFINITY;
            byte originalX = -1;
            byte originalY = -1;
            Move returned;

            Board newBoard;
            boolean removed = false;
            Piece p = new Piece();

            // BEAM SEARCH:
            for (Move move : possibleMoves)
            {
                newBoard = board.copyBoard();
                Iterator<Piece> piecesIterator = newBoard.pieces.iterator();
                while (piecesIterator.hasNext())
                {
                    p = piecesIterator.next();
                    if (p.getX() == move.source.x && p.getY() == move.source.y)
                        break;
                }

                makeMove(newBoard, p, move.target.x, move.target.y);

                move.score = staticEvaluation(newBoard);
            }

            Collections.sort(possibleMoves);

            int cutIndex;

            if (possibleMoves.size() < 10)
                cutIndex = possibleMoves.size();
            else
                cutIndex  = (int)(possibleMoves.size() / (1 * Math.log10(possibleMoves.size())));

            if (possibleMoves.size() - 1 - cutIndex < 0)
                cutIndex = 0;
            else
                cutIndex = possibleMoves.size() - 1 - cutIndex;

            possibleMoves = new ArrayList<>(possibleMoves.subList(cutIndex, possibleMoves.size()));


            // END OF BEAM SEARCH

            for (Move move : possibleMoves)
            {
                newBoard = board.copyBoard();
                Iterator<Piece> piecesIterator = newBoard.pieces.iterator();
                while (piecesIterator.hasNext())
                {
                    p = piecesIterator.next();
                    if (p.getX() == move.source.x && p.getY() == move.source.y)
                        break;
                }

                originalX = p.getX();
                originalY = p.getY();

                if(makeMove(newBoard, p, move.target.x, move.target.y))
                    removed = true;

                newBoard.player = !newBoard.player;

                returned = alfaBeta(newBoard, maxDepth - 1, alfa, beta);

                if (returned.score > returnValue.score || (returned.score == returnValue.score && Math.random() > 0.5))
                {
                    returnValue.score = returned.score;
                    returnValue.target = move.target;
                    returnValue.source = move.source;
                }

                p.setX(originalX);
                p.setY(originalY);

                /*

                if (removed)
                {
                    Piece toAdd = new Piece();
                    Iterator<Piece> takenIterator = taken.iterator();
                    while (takenIterator.hasNext())
                        toAdd = takenIterator.next();
                    takenIterator.remove();
                    toAdd.setX(move.target.x);
                    toAdd.setY(move.target.y);
                    newList.add(toAdd);

                    removed = false;
                }

                */

                if (alfa < returned.score)
                    alfa = returned.score;

                if (alfa >= beta)
                    break;
            }

            /*
            while (piecesIterator.hasNext())
            {
                p = piecesIterator.next();
                for (Piece next : newList)
                    if (p.getY() == next.getY() && p.getX() == next.getX())
                    {
                        p = next;
                        break;
                    }
                exit = false;
                originalX = p.getX();
                originalY = p.getY();
                if (p.isColour())
                {
                    for (byte i = 0; i < 8 && !exit; i++)
                    {
                        for (byte j = 0; j < 8 && !exit; j++)
                        {
                            if (isMoveLegal(player, p, newList, i, j))
                            {
                                if (moveBtn(newList, p, i, j))
                                    removed = true;

                                returned = alfaBeta(!player, maxDepth - 1, newList, alfa, beta);

                                if (returned > score)
                                {
                                    bestPiece = p;
                                    score = returned;
                                    bestX = i;
                                    bestY = j;
                                }

                                p.setX(originalX);
                                p.setY(originalY);

                                if (removed)
                                {
                                    Piece toAdd = new Piece();
                                    Iterator<Piece> takenIterator = taken.iterator();
                                    while (takenIterator.hasNext())
                                        toAdd = takenIterator.next();
                                    takenIterator.remove();
                                    toAdd.setX(i);
                                    toAdd.setY(j);
                                    newList.add(toAdd);

//                                pieces.trimToSize();
//                                taken.trimToSize();

                                    removed = false;
                                }

                                if (alfa < returned)
                                    alfa = returned;

                                if (alfa >= beta)
                                    exit = true;
                            }
                        }
                    }
                }
            }
            */

        }
        else
        {

            returnValue.score = Double.POSITIVE_INFINITY;
            byte originalX = -1;
            byte originalY = -1;
            Move returned;

            Board newBoard; // = copyList(pieces);
            boolean removed = false;
            Piece p = new Piece();

            // BEAM SEARCH:
            for (Move move : possibleMoves)
            {
                newBoard = board.copyBoard();
                Iterator<Piece> piecesIterator = newBoard.pieces.iterator();
                while (piecesIterator.hasNext())
                {
                    p = piecesIterator.next();
                    if (p.getX() == move.source.x && p.getY() == move.source.y)
                        break;
                }

                makeMove(newBoard, p, move.target.x, move.target.y);

                move.score = staticEvaluation(newBoard);
            }

            Collections.sort(possibleMoves);

            int cutIndex;

            if (possibleMoves.size() < 10)
                cutIndex = possibleMoves.size();
            else
                cutIndex  = (int)(possibleMoves.size() / (1 * Math.log10(possibleMoves.size())));

            possibleMoves = new ArrayList<>(possibleMoves.subList(0, cutIndex));

            // END OF BEAM SEARCH

            for (Move move : possibleMoves)
            {
                newBoard = board.copyBoard();
//                ArrayList<Piece> iterationList = copyList(pieces);
                Iterator<Piece> piecesIterator = newBoard.pieces.iterator();
                while (piecesIterator.hasNext())
                {
                    p = piecesIterator.next();
                    if (p.getX() == move.source.x && p.getY() == move.source.y)
                        break;
                }

                originalX = p.getX();
                originalY = p.getY();

                if (makeMove(newBoard, p, move.target.x, move.target.y))
                    removed = true;

                newBoard.player = !newBoard.player;

                returned = alfaBeta(newBoard, maxDepth - 1, alfa, beta);

                if (returned.score < returnValue.score || (returned.score == returnValue.score && Math.random() > 0.5))
                {
                    returnValue.score = returned.score;
                    returnValue.target = move.target;
                    returnValue.source = move.source;
                }

                p.setX(originalX);
                p.setY(originalY);

                /*

                if (removed)
                {
                    Piece toAdd = new Piece();
                    Iterator<Piece> takenIterator = taken.iterator();
                    while (takenIterator.hasNext())
                        toAdd = takenIterator.next();
                    takenIterator.remove();
                    toAdd.setX(move.target.x);
                    toAdd.setY(move.target.y);
                    newList.add(toAdd);

                    removed = false;
                }

                */

                if (beta > returned.score)
                    beta = returned.score;

                if (alfa >= beta)
                    break;
            }
        }



        return returnValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimax);

        board = new Board();
        library = new Tree(Minimax.this);

        // adding Kings to the board
        String tempPos = "e8";
        byte[] coords = toCoord(tempPos);
        board.pieces.add(new King(coords[0], coords[1], false));

        tempPos = "a8";
        coords = toCoord(tempPos);
        board.pieces.add(new Rook(coords[0], coords[1], false));
        tempPos = "b8";
        coords = toCoord(tempPos);
        board.pieces.add(new Knight(coords[0], coords[1], false));
        tempPos = "c8";
        coords = toCoord(tempPos);
        board.pieces.add(new Bishop(coords[0], coords[1], false));
        tempPos = "d8";
        coords = toCoord(tempPos);
        board.pieces.add(new Queen(coords[0], coords[1], false));
        tempPos = "f8";
        coords = toCoord(tempPos);
        board.pieces.add(new Bishop(coords[0], coords[1], false));
        tempPos = "g8";
        coords = toCoord(tempPos);
        board.pieces.add(new Knight(coords[0], coords[1], false));
        tempPos = "h8";
        coords = toCoord(tempPos);
        board.pieces.add(new Rook(coords[0], coords[1], false));
        for (int i = 0; i < 8; i++)
        {
            tempPos = ((char) ('a' + i) + "7");
            coords = toCoord(tempPos);
            board.pieces.add(new Pawn(coords[0], coords[1], false));
        }

        tempPos = "e1";
        coords = toCoord(tempPos);
        board.pieces.add(new King(coords[0], coords[1], true));

        tempPos = "a1";
        coords = toCoord(tempPos);
        board.pieces.add(new Rook(coords[0], coords[1], true));
        tempPos = "b1";
        coords = toCoord(tempPos);
        board.pieces.add(new Knight(coords[0], coords[1], true));
        tempPos = "c1";
        coords = toCoord(tempPos);
        board.pieces.add(new Bishop(coords[0], coords[1], true));
        tempPos = "d1";
        coords = toCoord(tempPos);
        board.pieces.add(new Queen(coords[0], coords[1], true));
        tempPos = "f1";
        coords = toCoord(tempPos);
        board.pieces.add(new Bishop(coords[0], coords[1], true));
        tempPos = "g1";
        coords = toCoord(tempPos);
        board.pieces.add(new Knight(coords[0], coords[1], true));
        tempPos = "h1";
        coords = toCoord(tempPos);
        board.pieces.add(new Rook(coords[0], coords[1], true));
        for (int i = 0; i < 8; i++)
        {
            tempPos = ((char) ('a' + i) + "2");
            coords = toCoord(tempPos);
            board.pieces.add(new Pawn(coords[0], coords[1], true));
        }


        TableLayout table = findViewById(R.id.board);
        TableRow row;
        for (byte i = 0; i < 8; i++)
        {
            row = (TableRow) table.getChildAt(i);
            for (byte j = 0; j < 8; j++)
            {
                boardButtons[i][j] = (ImageButton) row.getChildAt(j);
            }
        }

        updateTheBoard(board, boardButtons);

        for (byte i = 0; i < 8; i++)
        {
            for (byte j = 0; j < 8; j++)
            {
                ImageButton btn = boardButtons[i][j];
                final byte tempX = i;
                final byte tempY = j;
                btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (board.selected != board.nuller)
                        {
                            Piece selected = board.selected;
                            if (isMoveLegal(board, selected, tempX, tempY))
                            {
                                lastMove = new Move(new Coord(selected.getX(), selected.getY()), new Coord(tempX, tempY));
                                if (library != null)
                                {
                                    boolean found = false;
                                    for (Node n: library.root.children)
                                    {
                                        if (n.move.equals(lastMove))
                                        {
                                            library.root = n;
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found)
                                    {
                                        library = null;
                                    }
                                }
                                makeMove(board, selected, tempX, tempY);

                                board.player = !board.player;

                                if (isInCheck(board.player, board.pieces))
                                {

                                    if (!hasMoves(board))
                                    {
                                        CharSequence message;
                                        if (!board.player) // black is in check and has no available moves
                                            message = "Check mate! White has won!";
                                        else
                                            message = "Check mate! Black has won!";
                                        new AlertDialog.Builder(Minimax.this)
                                                .setTitle("Game over")
                                                .setMessage(message)
                                                .setNeutralButton("Show the board", new DialogInterface.OnClickListener()
                                                {
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                                                {
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        startActivity(new Intent(Minimax.this, MainActivity.class));
                                                    }
                                                })
                                                .show();
                                    }
                                }
                                else if (!hasMoves(board))
                                    new AlertDialog.Builder(Minimax.this)
                                            .setTitle("Game over")
                                            .setMessage("Stalemate!")
                                            .setNeutralButton("Show the board", new DialogInterface.OnClickListener()
                                            {
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                                            {
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    startActivity(new Intent(Minimax.this, MainActivity.class));
                                                }
                                            })
                                            .show();

                            }


                            board.selected = board.nuller;
                        }
                        else
                        {
                            for (Piece p : board.pieces)
                            {
                                if (board.player == p.isColour() && p.getX() == tempX && p.getY() == tempY)
                                {
                                    board.selected = p;
                                    break;
                                }
                            }
                        }
                        removeTaken(board);
                        updateTheBoard(board, boardButtons);
                    }
                });
            }
        }

        Button moveBtn = findViewById(R.id.moveBtn);

        moveBtn.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(View v)
                                       {
                                           Move answer;
                                           if (lastMove == null && library != null)
                                           {
                                               if (library.root.children.size() == 0)
                                               {
                                                   library = null;
                                                   answer = alfaBeta(board, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                                               }
                                               else
                                               {
                                                   int index = new Random().nextInt(library.root.children.size());
                                                   library.root = library.root.children.get(index);
                                                   answer = library.root.move;
                                                   lastMove = null;
                                               }
                                           }
                                           else
                                           {
                                               if (library != null)
                                               {
                                                   int index = new Random().nextInt(library.root.children.size());
                                                   library.root = library.root.children.get(index);
                                                   answer = library.root.move;
                                                   lastMove = null;
                                               }
                                               else
                                               {
                                                   answer = alfaBeta(board, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                                                   lastMove = null;
                                               }
                                           }

//                                           ArrayList<Move> moves = listPossibleMoves(board);

                                           if (answer.source.x == -1)
                                           {
                                               Toast popup = Toast.makeText(getApplicationContext(), "No moves can be made!", Toast.LENGTH_SHORT);
                                               popup.show();
                                           }
                                           else
                                           {
                                               Piece movedPiece = board.nuller;
                                               for (Piece searched : board.pieces)
                                                   if (searched.getX() == answer.source.x && searched.getY() == answer.source.y)
                                                   {
                                                       movedPiece = searched;
                                                       break;
                                                   }

                                               makeMove(board, movedPiece, answer.target.x, answer.target.y);
//                                               bestPiece = nuller;
//                                               bestX = -1;
//                                               bestY = -1;

                                               board.player = !board.player;

                                               if (isInCheck(board.player, board.pieces))
                                               {

                                                   if (!hasMoves(board))
                                                   {
                                                       CharSequence message;
                                                       if (!board.player)
                                                           message = "Check mate! White has won!";
                                                       else
                                                           message = "Check mate! Black has won!";
                                                       new AlertDialog.Builder(Minimax.this)
                                                               .setTitle("Game over")
                                                               .setMessage(message)
                                                               .setNeutralButton("Show the board", new DialogInterface.OnClickListener()
                                                               {
                                                                   public void onClick(DialogInterface dialog, int which)
                                                                   {
                                                                       dialog.dismiss();
                                                                   }
                                                               })
                                                               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                                                               {
                                                                   public void onClick(DialogInterface dialog, int which)
                                                                   {
                                                                       startActivity(new Intent(Minimax.this, MainActivity.class));
                                                                   }
                                                               })
                                                               .show();
                                                   }
                                               }
                                               else if (!hasMoves(board))
                                                   new AlertDialog.Builder(Minimax.this)
                                                           .setTitle("Game over")
                                                           .setMessage("Stalemate!")
                                                           .setNeutralButton("Show the board", new DialogInterface.OnClickListener()
                                                           {
                                                               public void onClick(DialogInterface dialog, int which)
                                                               {
                                                                   dialog.dismiss();
                                                               }
                                                           })
                                                           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                                                           {
                                                               public void onClick(DialogInterface dialog, int which)
                                                               {
                                                                   startActivity(new Intent(Minimax.this, MainActivity.class));
                                                               }
                                                           })
                                                           .show();

                                               removeTaken(board);
                                               updateTheBoard(board, boardButtons);
                                           }
                                       }
                                   }
        );

    }


    public static class Move implements Comparable<Move>
    {
        public Coord source;
        public Coord target;
        public double score;

        public Move()
        {

        }

        public Move(Coord source, Coord target)
        {
            this.source = source;
            this.target = target;
        }

        public Move(Coord source, Coord target, double score)
        {
            this.source = source;
            this.target = target;
            this.score = score;
        }

        public boolean equals(Move t)
        {
            if (t == null)
                return false;
            return this.source.equals(t.source) && this.target.equals(t.target);
        }

        @Override
        public int compareTo(Move t)
        {
            if (this.score == t.score)
                return 0;
            if (this.score > t.score)
                return 1;

            return -1;
        }
    }

    public static class Coord
    {
        public byte x;
        public byte y;


        public Coord(int x, int y)
        {
            this.x = (byte)x;
            this.y = (byte)y;
        }

        public boolean equals(Coord c)
        {
            return this.x == c.x && this.y == c.y;
        }
    }

    public static class Board
    {
        public ArrayList<Piece> pieces;
        public ArrayList<Piece> taken;
        public Piece enPassantPosition;
        public Piece selected;
        public Piece nuller;

        boolean player;

        public Board()
        {
            player = true;
            nuller = new Piece();
            selected = nuller;
            enPassantPosition = nuller;

            pieces = new ArrayList<>();
            taken = new ArrayList<>();
        }

        public Board copyBoard()
        {
            Board copied = new Board();
            copied.player = player;
            copied.pieces = copyList(pieces);
            copied.taken = copyList(taken);

            if (enPassantPosition != nuller)
            {
                copied.enPassantPosition = new Piece();
                copied.enPassantPosition.setX(enPassantPosition.getX());
                copied.enPassantPosition.setY(enPassantPosition.getY());
            }
            else
                copied.enPassantPosition = copied.nuller;

            return copied;
        }

        private void fromString(String representation)
        {
            String[] rows = representation.split(";");

            String[] cols;

            Piece p;
            boolean colour;

            for (byte i = 0; i < 8; i++)
            {
                cols = rows[i].split(",");
                for (byte j = 0; j < 8; j++)
                {
                    if (cols[j].equals("--"))
                        continue;
                    else
                    {
                        colour = cols[j].charAt(0) == 'W';

                        if (cols[j].charAt(1) == 'b')
                        {
                            p = new Bishop(i, j, colour);
                        }
                        else if (cols[j].charAt(1) == 'n')
                        {
                            p = new Knight(i, j, colour);
                        }
                        else if (cols[j].charAt(1) == 'q')
                        {
                            p = new Queen(i, j, colour);
                        }
                        else if (cols[j].charAt(1) == 'r')
                        {
                            p = new Rook(i, j, colour);
                        }
                        else if (cols[j].charAt(1) == 'R')
                        {
                            p = new Rook(i, j, colour, true);
                        }
                        else if (cols[j].charAt(1) == 'k')
                        {
                            p = new King(i, j, colour);
                        }
                        else if (cols[j].charAt(1) == 'K')
                        {
                            p = new King(i, j, colour, true);
                        }
                        else
                        {
                            p = new Pawn(i, j, colour);
                        }

                        pieces.add(p);
                    }
                }
            }
        }

        public String toString()
        {
            String representation = "";
            String[][] board = new String[8][8];

            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    board[i][j] = "--";
                }
            }

            for (Piece p: this.pieces)
            {
                if (p.getX() == -1 || p.getY() == -1)
                    continue;

                if (p.isColour())
                {
                    board[p.getX()][p.getY()] = "W";
                }
                else
                {
                    board[p.getX()][p.getY()] = "B";
                }

                if (p instanceof Pawn)
                {
                    board[p.getX()][p.getY()] += "p";
                }
                else if (p instanceof Bishop)
                {
                    board[p.getX()][p.getY()] += "b";
                }
                else if (p instanceof Knight)
                {
                    board[p.getX()][p.getY()] += "n";
                }
                else if (p instanceof Queen)
                {
                    board[p.getX()][p.getY()] += "q";
                }
                else if (p instanceof Rook)
                {
                    if (((Rook) p).moved)
                        board[p.getX()][p.getY()] += "R";
                    else
                        board[p.getX()][p.getY()] += "r";
                }
                else if (p instanceof King)
                {
                    if (((King) p).moved)
                        board[p.getX()][p.getY()] += "K";
                    else
                        board[p.getX()][p.getY()] += "k";
                }
            }

            board[enPassantPosition.getX()][enPassantPosition.getY()] = "eP";

            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 7; j++)
                {
                    representation += board[i][j] + ",";
                }
                representation += board[i][7] + ";";
            }

            return representation;
        }
    }

    public static class Tree
    {
        public Node root;
        private Activity activity;
        private File file;

        public Tree(Activity activity)
        {
            this.activity = activity;
            file = new File(activity.getFilesDir(), "library.txt");
            root = new Node();
            // ADD KNOWN OPENINGS HERE:

            Node adder, parent, breakpoint;
            boolean found = false;
            // Italian Game:
            // e4

            parent = root;
            adder = new Node();
            adder.move = new Move(new Coord(6, 4), new Coord(4, 4));
            parent.children.add(adder);
            parent = adder;
            // e5
            adder = new Node();
            adder.move = new Move(new Coord(1, 4), new Coord(3, 4));
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nf3
            adder = new Node();
            adder.move = new Move(new Coord(7, 6), new Coord(5, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nc6
            adder = new Node();
            adder.move = new Move(new Coord(0, 1), new Coord(2, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Bc4
            adder = new Node();
            adder.move = new Move(new Coord(7, 5), new Coord(4, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Bc5
            adder = new Node();
            adder.move = new Move(new Coord(0, 5), new Coord(3, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // c3
            adder = new Node();
            adder.move = new Move(new Coord(6, 2), new Coord(5, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nf6
            adder = new Node();
            adder.move = new Move(new Coord(0, 6), new Coord(2, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // d3
            adder = new Node();
            adder.move = new Move(new Coord(6, 3), new Coord(5, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // d6
            adder = new Node();
            adder.move = new Move(new Coord(1, 3), new Coord(2, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // 0-0
            adder = new Node();
            adder.move = new Move(new Coord(7, 4), new Coord(7, 6));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // h6
            adder = new Node();
            adder.move = new Move(new Coord(1, 7), new Coord(2, 7));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // End of Italian Game

            // Sicilian Defense
            parent = root;

            // e4
            adder = new Node();
            adder.move = new Move(new Coord(6, 4), new Coord(4, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // c5
            adder = new Node();
            adder.move = new Move(new Coord(1, 2), new Coord(3, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
                found = false;
            }

            // Nf3
            adder = new Node();
            adder.move = new Move(new Coord(7, 6), new Coord(5, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // d6
            adder = new Node();
            adder.move = new Move(new Coord(1, 3), new Coord(2, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // d4
            adder = new Node();
            adder.move = new Move(new Coord(6, 3), new Coord(4, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // cxd4
            adder = new Node();
            adder.move = new Move(new Coord(3, 2), new Coord(4, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nxd4
            adder = new Node();
            adder.move = new Move(new Coord(5, 5), new Coord(4, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nf6
            adder = new Node();
            adder.move = new Move(new Coord(0, 6), new Coord(2, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nc3
            adder = new Node();
            adder.move = new Move(new Coord(7, 1), new Coord(5, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // a6
            adder = new Node();
            adder.move = new Move(new Coord(1, 0), new Coord(2, 0));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }


            // End of Sicilian Defense

            // French Defense A

            parent = root;

            // e4
            adder = new Node();
            adder.move = new Move(new Coord(6, 4), new Coord(4, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // e6
            adder = new Node();
            adder.move = new Move(new Coord(1, 4), new Coord(2, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // d4
            adder = new Node();
            adder.move = new Move(new Coord(6, 3), new Coord(4, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // d5
            adder = new Node();
            adder.move = new Move(new Coord(1, 3), new Coord(3, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // Nc3
            adder = new Node();
            adder.move = new Move(new Coord(7, 1), new Coord(5, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // two moves:

            breakpoint = parent;

            // Bb4
            adder = new Node();
            adder.move = new Move(new Coord(0, 5), new Coord(4, 1));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // e5
            adder = new Node();
            adder.move = new Move(new Coord(4, 4), new Coord(3, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // c5
            adder = new Node();
            adder.move = new Move(new Coord(1, 2), new Coord(3, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // a3
            adder = new Node();
            adder.move = new Move(new Coord(6, 0), new Coord(5, 0));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // The other move subtree:

            parent = breakpoint;

            // Nf6
            adder = new Node();
            adder.move = new Move(new Coord(0, 6), new Coord(2, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // two moves again:

            breakpoint = parent;

            // e5
            adder = new Node();
            adder.move = new Move(new Coord(4, 4), new Coord(3, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // Nd7
            adder = new Node();
            adder.move = new Move(new Coord(2, 5), new Coord(1, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }


            // end of breakpoint:

            parent = breakpoint;

            // Bg5
            adder = new Node();
            adder.move = new Move(new Coord(7, 2), new Coord(3, 6));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // dxe4
            adder = new Node();
            adder.move = new Move(new Coord(3, 3), new Coord(4, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nxe4
            adder = new Node();
            adder.move = new Move(new Coord(5, 2), new Coord(4, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // Be7
            adder = new Node();
            adder.move = new Move(new Coord(0, 5), new Coord(1, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }

            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // End of French Defense B

            // The Ruy Lopez

            parent = root;

            // e4
            adder = new Node();
            adder.move = new Move(new Coord(6, 4), new Coord(4, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // e5
            adder = new Node();
            adder.move = new Move(new Coord(1, 4), new Coord(3, 4));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nf3
            adder = new Node();
            adder.move = new Move(new Coord(7, 6), new Coord(5, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nc6
            adder = new Node();
            adder.move = new Move(new Coord(0, 1), new Coord(2, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Bb5
            adder = new Node();
            adder.move = new Move(new Coord(7, 5), new Coord(3, 1));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // a6
            adder = new Node();
            adder.move = new Move(new Coord(1, 0), new Coord(2, 0));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Ba4
            adder = new Node();
            adder.move = new Move(new Coord(3, 1), new Coord(4, 0));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // Nf6
            adder = new Node();
            adder.move = new Move(new Coord(0, 6), new Coord(2, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // 0-0
            adder = new Node();
            adder.move = new Move(new Coord(7, 4), new Coord(7, 6));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // End of the Ruy Lopez

            // Slav Defense

            parent = root;

            // d4
            adder = new Node();
            adder.move = new Move(new Coord(6, 3), new Coord(4, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // d5
            adder = new Node();
            adder.move = new Move(new Coord(1, 3), new Coord(3, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // c4
            adder = new Node();
            adder.move = new Move(new Coord(6, 2), new Coord(4, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // c6
            adder = new Node();
            adder.move = new Move(new Coord(1, 2), new Coord(2, 2));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nf3
            adder = new Node();
            adder.move = new Move(new Coord(7, 6), new Coord(5, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // Nf6
            adder = new Node();
            adder.move = new Move(new Coord(0, 6), new Coord(2, 5));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // cxd5
            adder = new Node();
            adder.move = new Move(new Coord(4, 2), new Coord(3, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }
            // cxd5
            adder = new Node();
            adder.move = new Move(new Coord(2, 2), new Coord(3, 3));
            found = false;
            for (Node child : parent.children)
            {
                if (child.move.equals(adder.move))
                {
                    found = true;
                    parent = child;
                    break;
                }
            }
            if (!found)
            {
                parent.children.add(adder);
                parent = adder;
            }

            // End of Slav Defense

            readLibraryFile();
        }

        public void readLibraryFile()
        {
            try
            {
                FileReader reader = new FileReader(file);

                Node adder, parent = root;
                boolean found;

                int nextVal;
                byte sx, sy, tx, ty;
                char c;
                nextVal = reader.read();
                do
                {
                    found = false;
                    sx = (byte) (nextVal - 48);
                    nextVal = reader.read();
                    sy = (byte) (nextVal - 48);
                    nextVal = reader.read();
                    tx = (byte) (nextVal - 48);
                    nextVal = reader.read();
                    ty = (byte) (nextVal - 48);

                    c = (char) reader.read();
                    if (c == ',')
                    {
                        adder = new Node();
                        adder.move = new Move(new Coord(sx, sy), new Coord(tx, ty));

                        for (Node child : parent.children)
                        {
                            if (child.move.equals(adder.move))
                            {
                                found = true;
                                parent = child;
                                break;
                            }
                        }
                        if (!found)
                        {
                            parent.children.add(adder);
                            parent = adder;
                        }
                    }
                    else if (c == ';')
                    {
                        adder = new Node();
                        adder.move = new Move(new Coord(sx, sy), new Coord(tx, ty));

                        for (Node child : parent.children)
                        {
                            if (child.move.equals(adder.move))
                            {
                                found = true;
                                parent = child;
                                break;
                            }
                        }
                        if (!found)
                        {
                            parent.children.add(adder);
                        }
                        parent = root;
                        c = (char) reader.read();
                        if (c != '\n' && c != '\r')
                            throw new Exception("New line not found: " + c);
                    }
                    else
                    {
                        throw new Exception("Unknown character: " + c);
                    }
                    nextVal = reader.read();
                } while(nextVal != -1);

                reader.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void writeLibraryFile(ArrayList<Move> opening)
        {
            try
            {
                FileWriter writer = new FileWriter(file, true);
                int i = 0;
                for (Move m : opening)
                {
                    writer.write("" + m.source.x);
                    writer.write("" + m.source.y);
                    writer.write("" + m.target.x);
                    writer.write("" + m.target.y);
                    if (i != opening.size() - 1)
                        writer.write(',');
                    i++;
                }
                writer.write(";\n");
                writer.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class Node
    {
        public Move move;
        public ArrayList<Node> children;

        public Node()
        {
            children = new ArrayList<>();
        }
    }

}


