package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class Minimax extends AppCompatActivity
{
    ArrayList<Piece> pieces = new ArrayList<>();
    ArrayList<Piece> taken = new ArrayList<>();

    double[][] boardValues = {
            {0.5, 1, 1, 1, 1, 1, 1, 0.5},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 3, 3, 3, 3, 1, 1},
            {1, 1, 3.5, 6, 6, 3.5, 1, 1},
            {1, 1, 3.5, 6, 6, 3.5, 1, 1},
            {1, 1, 3, 3, 3, 3, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0.5, 1, 1, 1, 1, 1, 1, 0.5}
    };

    ImageButton[][] board = new ImageButton[8][8];

    Piece nuller = new Piece();
    Piece enPassantPosition = nuller;
    //    Piece enPassant = new Piece();
    Piece selected = nuller;
    Piece bestPiece = nuller;
    byte bestX = -1, bestY = -1;

    boolean player = true;

//    King kingW, kingB;

    int counter = 0;

    private void updateTheBoard()
    {
        for (ImageButton[] i : board)
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
        for (Piece p : pieces)
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
        }

        if (selected != nuller)
            board[selected.getX()][selected.getY()].setBackgroundResource(R.drawable.selected);

    }

    private String toChessPos(byte x, byte y)
    {
        String out = "";

        char c = (char) ('a' + y + 1);
        out += c;
        out += (x + 1);

        return out;
    }

    private byte[] toCoord(String pos)
    {
        char c = pos.charAt(0);
        byte y = (byte) (c - 'a');
        byte x = (byte) (7 - (pos.charAt(1) - '1'));

        byte[] ans = {x, y};

        return ans;
    }

    private void removeTaken()
    {
        Iterator<Piece> iter = pieces.iterator();
        Piece p;
        while (iter.hasNext())
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

    private boolean isInCheck(boolean player, ArrayList<Piece> pieces)
    {
        King checked = new King((byte)-1,(byte)-1, player);
        for (Piece p : pieces)
        {
            if (p instanceof King && p.isColour() == player)
                checked = (King) p;
        }
        for (Piece p : pieces)
        {
            if (player != p.isColour())
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

    private boolean isMoveLegal(boolean player, @org.jetbrains.annotations.NotNull Piece p, ArrayList<Piece> pieces, byte x, byte y)
    {
        for (Piece next : pieces)
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
            if (player != p.isColour())
                return false;

            if ((player && x == 7 || !player && x == 0) && (y == 2 || y == 6) && !((King) p).moved && !isInCheck(player, pieces))
            {
                byte firstRank = player ? (byte) 7 : 0;
                boolean canCastle = true;
                Rook rook = new Rook(player);
                Iterator<Piece> iterator = pieces.iterator();
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
                    selected.setY((byte) 3);
                    if (isInCheck(player, pieces))
                    {
                        selected.setY((byte) 4);
                        return false;
                    }
                    selected.setY((byte) 2);
                    if (isInCheck(player, pieces))
                    {
                        selected.setY((byte) 4);
                        return false;
                    }
                    selected.setY((byte) 4);
                    return true;
                }
                else
                {
                    selected.setY((byte) 5);
                    if (isInCheck(player, pieces))
                    {
                        selected.setY((byte) 4);
                        return false;
                    }
                    selected.setY((byte) 6);
                    if (isInCheck(player, pieces))
                    {
                        selected.setY((byte) 4);
                        return false;
                    }
                    selected.setY((byte) 4);
                    return true;
                }
            }
        }
        else if (!p.isMoveAllowed(x, y) && !p.isAttackAllowed(x, y) || player != p.isColour())
            return false;
        else if (enPassantPosition != nuller && p instanceof Pawn)
        {
            if (enPassantPosition.getX() == x && enPassantPosition.getY() == y)
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

            Iterator<Piece> iterator = pieces.iterator();
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

        Piece toBeRemoved = nuller;
        Piece a;
        boolean attacking = false;
        Iterator<Piece> iterator = pieces.iterator();
        while (iterator.hasNext())
        {
            a = iterator.next();
            if (a.getX() == x && a.getY() == y)
            {
                attacking = true;
                if (a.isColour() == p.isColour() || !p.isAttackAllowed(x, y))
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
            if (toBeRemoved != nuller)
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
        if (isInCheck(p.isColour(), pieces))
        {
            p.setX(originalX);
            p.setY(originalY);
            if (toBeRemoved != nuller)
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
            if (toBeRemoved != nuller)
            {
                toBeRemoved.setY(y);
                toBeRemoved.setX(x);
            }
            return true;
        }
    }

    private boolean makeMove(ArrayList<Piece> pieces, Piece selected, byte tempX, byte tempY)
    {

        /*for (Piece p : pieces)
            if (p.getX() == selected.getX() && p.getY() == selected.getY())
            {
                selected = p;
                break;
            } */

        boolean resetEnPassant = true;

        byte x = tempX;
        byte y = tempY;

        if (selected instanceof Pawn)
        {
            // next to if's prepare en passant:
            if (selected.isColour() && selected.getX() == 6 && tempX == 4)
            {
                enPassantPosition = new Piece();
                enPassantPosition.setX((byte) 5);
                enPassantPosition.setY(selected.getY());
                resetEnPassant = false;
//                                        enPassantPosition.setColour(true);
            }
            else if (!selected.isColour() && selected.getX() == 1 && tempX == 3)
            {
                enPassantPosition = new Piece();
                enPassantPosition.setX((byte) 2);
                enPassantPosition.setY(selected.getY());
                resetEnPassant = false;
//                                        enPassantPosition.setColour(false);
            }
            else if (enPassantPosition != nuller && enPassantPosition.getX() == tempX && enPassantPosition.getY() == tempY)
            {
                // find the pawn that needs to be removed:
                if (player)
                    x = (byte) 3;
                else
                    x = (byte) 4;

            }
            else if ((selected.isColour() && tempX == 0) || (!selected.isColour() && tempX == 7))
            {
                // promotion to Queen here:
                // remove the pawn:
                selected.setX((byte) -1);
                selected.setY((byte) -1);
                // add the Queen:
                selected = new Queen(selected.isColour());
                pieces.add(selected);
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
                        p.setY((byte) 3);
                    }
            }
            else if (tempX == firstRank && tempY == 6)
            {
                for (Piece p : pieces)
                    if (p instanceof Rook && p.getX() == firstRank && p.getY() == 7)
                    {
                        p.setY((byte) 5);
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

        boolean wasRemoved = false;

        Iterator<Piece> iterator = pieces.iterator();
        Piece p;
        while (iterator.hasNext())
        {
            p = iterator.next();
            if (p != selected && p.getX() == x && p.getY() == y)
            {
                p.setX((byte) -1);
                p.setY((byte) -1);
                taken.add(p);
                wasRemoved = true;
                break;
            }
        }

        if (resetEnPassant)
            enPassantPosition = nuller;

        return wasRemoved;
    }

    private boolean hasMoves(boolean player, ArrayList<Piece> pieces)
    {
        Piece p;
        pieces = copyList(pieces);
        Iterator<Piece> iterator = pieces.iterator();
        while (iterator.hasNext())
        {
            p = iterator.next();

            if (p instanceof Pawn)
            {
                byte pawnPassantStartingRank = player ? (byte) 6 : 1;
                byte pawnPassantTargetRank = player ? (byte) 4 : 3;
                if (p.getX() == pawnPassantStartingRank && isMoveLegal(player, p, pieces, pawnPassantTargetRank, p.getY()))
                    return true;

                byte direction = player ? (byte) -1 : 1;
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + direction), p.getY())
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + direction), (byte) (p.getY() + 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + direction), (byte) (p.getY() - 1)))
                    return true;
            }
            else if (p instanceof Knight)
            {
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() - 2))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() + 2))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() - 2), (byte) (p.getY() - 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() - 2), (byte) (p.getY() + 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() - 2))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() + 2))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 2), (byte) (p.getY() - 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 2), (byte) (p.getY() + 1)))
                    return true;

            }
            else if (p instanceof King)
            {
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() - 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY()))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() + 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() - 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() + 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() - 1))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY()))
                        || isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() + 1)))
                    return true;
            }
            else
            {
                if (!(p instanceof Rook)) // means Bishop moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() - posChanger), (byte) (p.getY() - posChanger))
                                || isMoveLegal(player, p, pieces, (byte) (p.getX() - posChanger), (byte) (p.getY() + posChanger))
                                || isMoveLegal(player, p, pieces, (byte) (p.getX() + posChanger), (byte) (p.getY() - posChanger))
                                || isMoveLegal(player, p, pieces, (byte) (p.getX() + posChanger), (byte) (p.getY() + posChanger)))
                            return true;
                    }
                }
                else // means Rook moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() - posChanger), (byte) (p.getY()))
                                || isMoveLegal(player, p, pieces, (byte) (p.getX() + posChanger), (byte) (p.getY()))
                                || isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() - posChanger))
                                || isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() + posChanger)))
                            return true;
                    }
                }

            }
        }

        return false;
    }

    private ArrayList<Move> listPossibleMoves(boolean player, ArrayList<Piece> pieces)
    {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        Piece p;
        Coord source, target;
        Move adder;
        pieces = copyList(pieces);
        Iterator<Piece> iterator = pieces.iterator();
        while (iterator.hasNext())
        {
            p = iterator.next();
            source = new Coord(p.getX(), p.getY());

            if (p.isColour() != player)
                continue;

            if (p instanceof Pawn)
            {
                byte pawnPassantStartingRank = player ? (byte) 6 : 1;
                byte pawnPassantTargetRank = player ? (byte) 4 : 3;
                if (p.getX() == pawnPassantStartingRank && isMoveLegal(player, p, pieces, pawnPassantTargetRank, p.getY()))
                    possibleMoves.add(new Move(source, new Coord(pawnPassantTargetRank, p.getY())));

                byte direction = player ? (byte) -1 : 1;
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + direction), p.getY()))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + direction, p.getY())));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + direction), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + direction, p.getY() + 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + direction), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + direction, p.getY() - 1)));
            }
            else if (p instanceof Knight)
            {
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() - 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() - 2)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() + 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() + 2)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 2), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 2, p.getY() - 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 2), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 2, p.getY() + 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() - 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() - 2)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() + 2)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() + 2)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 2), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 2, p.getY() - 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 2), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 2, p.getY() + 1)));

            }
            else if (p instanceof King)
            {
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() - 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY())))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY())));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() - 1), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() - 1, p.getY() + 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() - 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() + 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() - 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() - 1)));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY())))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY())));
                if (isMoveLegal(player, p, pieces, (byte) (p.getX() + 1), (byte) (p.getY() + 1)))
                    possibleMoves.add(new Move(source, new Coord(p.getX() + 1, p.getY() + 1)));
            }
            else
            {
                if (p instanceof Bishop || p instanceof Queen) // means Bishop-like moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() - posChanger), (byte) (p.getY() - posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() - posChanger, p.getY() - posChanger)));
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() - posChanger), (byte) (p.getY() + posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() - posChanger, p.getY() + posChanger)));
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() + posChanger), (byte) (p.getY() - posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() + posChanger, p.getY() - posChanger)));
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() + posChanger), (byte) (p.getY() + posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX() + posChanger, p.getY() + posChanger)));
                    }
                }
                if (p instanceof Rook || p instanceof Queen) // means Rook-like moves
                {
                    for (byte posChanger = 1; posChanger < 8; posChanger++)
                    {
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() - posChanger), (byte) (p.getY())))
                            possibleMoves.add(new Move(source, new Coord(p.getX() - posChanger, p.getY())));
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX() + posChanger), (byte) (p.getY())))
                            possibleMoves.add(new Move(source, new Coord(p.getX() + posChanger, p.getY())));
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() - posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() - posChanger)));
                        if (isMoveLegal(player, p, pieces, (byte) (p.getX()), (byte) (p.getY() + posChanger)))
                            possibleMoves.add(new Move(source, new Coord(p.getX(), p.getY() + posChanger)));
                    }
                }

            }
        }

        return possibleMoves;
    }

    private double staticEvaluation(boolean player, ArrayList<Piece> pieces)
    {
        double score = 0.0;
        int white, black;
        Iterator<Piece> iterator = pieces.iterator();
        Piece p;
        ArrayList<Move> moves = listPossibleMoves(player, pieces);
        if (isInCheck(player, pieces) && moves.isEmpty())
        {
            if (player)
                return -1000.0;
            else
                return 1000.0;
        }

        if (moves.isEmpty() && !isInCheck(player, pieces))
            return 0.0;

        while (iterator.hasNext())
        {
            p = iterator.next();
            if (p.getX() != -1 && p.getY() != -1)
            {
                if (p.isColour())
                    score += (double)p.getValue();
                else
                    score -= (double)p.getValue();

            }
        }

        iterator = pieces.iterator();

        for (byte i = 0; i < 8; i++)
        {
            for (byte j = 0; j < 8; j++)
            {
                white = 0;
                black = 0;
                while (iterator.hasNext())
                {
                    p = iterator.next();
                    if (isMoveLegal(player, p, pieces, i,j))
                    {
                        if (p.isColour())
                            white++;
                        else
                            black++;
                    }
                }

                score += (white - black) * boardValues[i][j] * 10.0;
            }
        }

//        if (score != 0.0)
//            System.err.println(score);

        return score;
    }

    private ArrayList<Piece> copyList(ArrayList<Piece> list)
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

    private Move alfaBeta(boolean player, int maxDepth, ArrayList<Piece> pieces, double alfa, double beta)
    { // TODO change so that this method returns value and move

//        Piece bestPiece = new Piece();
//        byte bestX = -1, bestY = -1;
        Move returnValue = new Move();
        returnValue.source = new Coord(-1,-1);
//        double score;
//        pieces = copyList(pieces);
        ArrayList<Move> possibleMoves = listPossibleMoves(player, pieces);
        if (isInCheck(player, pieces) && possibleMoves.isEmpty())
        {
            if (player)
                returnValue.score = -1000;
            else
                returnValue.score = 1000;
            return returnValue;
        }
        else if (possibleMoves.isEmpty() || maxDepth == 0)
        {
            returnValue.score = staticEvaluation(player, pieces);
            return returnValue;
        }
        else if (player)
        {
            returnValue.score = Double.NEGATIVE_INFINITY;
            byte originalX = -1;
            byte originalY = -1;
            Move returned;

            ArrayList<Piece> newList; // new ArrayList<>(pieces);
            ArrayList<Piece> iterationList = copyList(pieces);
            Iterator<Piece> piecesIterator = iterationList.iterator();
            boolean removed = false;
            Piece p = new Piece();

            for (Move move : possibleMoves)
            {
                newList = copyList(pieces);
                while (piecesIterator.hasNext())
                {
                    p = piecesIterator.next();
                    if (p.getX() == move.source.x && p.getY() == move.source.y)
                        break;
                }

                originalX = p.getX();
                originalY = p.getY();

                if (makeMove(newList, p, move.target.x, move.target.y))
                    removed = true;

                returned = alfaBeta(!player, maxDepth - 1, newList, alfa, beta);

                if (returned.score > returnValue.score || (returned.score == returnValue.score && Math.random() > 0.5))
                {
                    returnValue.score = returned.score;
                    returnValue.target = move.target;
                    returnValue.source = move.source;
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
                    toAdd.setX(move.target.x);
                    toAdd.setY(move.target.y);
                    newList.add(toAdd);

                    removed = false;
                }

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
                                if (makeMove(newList, p, i, j))
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

            ArrayList<Piece> newList; // = copyList(pieces);
            ArrayList<Piece> iterationList = copyList(pieces);
            Iterator<Piece> piecesIterator = iterationList.iterator();
            boolean removed = false;
            Piece p = new Piece();

            for (Move move : possibleMoves)
            {
                newList = copyList(pieces);
                while (piecesIterator.hasNext())
                {
                    p = piecesIterator.next();
                    if (p.getX() == move.source.x && p.getY() == move.source.y)
                        break;
                }

                originalX = p.getX();
                originalY = p.getY();

                if (makeMove(newList, p, move.target.x, move.target.y))
                    removed = true;

                returned = alfaBeta(!player, maxDepth - 1, newList, alfa, beta);

                if (returned.score < returnValue.score || (returned.score == returnValue.score && Math.random() > 0.5))
                {
                    returnValue.score = returned.score;
                    returnValue.target = move.target;
                    returnValue.source = move.source;
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
                    toAdd.setX(move.target.x);
                    toAdd.setY(move.target.y);
                    newList.add(toAdd);

                    removed = false;
                }

                if (beta > returned.score)
                    beta = returned.score;

                if (alfa >= beta)
                    break;
            }


            /*
            boolean exit;
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
                if (!p.isColour())
                {
                    for (byte i = 0; i < 8 && !exit; i++)
                    {
                        for (byte j = 0; j < 8 && !exit; j++)
                        {
                            if (isMoveLegal(player, p, newList, i, j))
                            {
                                if (makeMove(newList, p, i, j))
                                    removed = true;

                                returned = alfaBeta(!player, maxDepth - 1, newList, alfa, beta);

                                if (returned < score)
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

                                if (beta > returned)
                                    beta = returned;

                                if (alfa >= beta)
                                    exit = true;
                            }
                        }
                    }
                }
            }
            */
        }



        return returnValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimax);

        // adding Kings to the board
        String tempPos = "e8";
        byte[] coords = toCoord(tempPos);
        pieces.add(new King(coords[0], coords[1], false));
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
            tempPos = ((char) ('a' + i) + "7");
            coords = toCoord(tempPos);
            pieces.add(new Pawn(coords[0], coords[1], false));
        }


        tempPos = "e1";
        coords = toCoord(tempPos);
        pieces.add(new King(coords[0], coords[1], true));
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
            tempPos = ((char) ('a' + i) + "2");
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
                btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (selected != nuller)
                        {
                            if (isMoveLegal(player, selected, pieces, tempX, tempY))
                            {
                                makeMove(pieces, selected, tempX, tempY);

                                if (isInCheck(!player, pieces))
                                {

                                    if (!hasMoves(!player, pieces))
                                    {
                                        CharSequence message;
                                        if (player)
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
                                else if (!hasMoves(!player, pieces))
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

                                player = !player;
                            }


                            selected = nuller;
                        }
                        else
                        {
                            for (Piece p : pieces)
                            {
                                if (player == p.isColour() && p.getX() == tempX && p.getY() == tempY)
                                {
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

        Button moveBtn = findViewById(R.id.moveBtn);

        moveBtn.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(View v)
                                       {

                                           Move answer = alfaBeta(player, 1, pieces, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                                           ArrayList<Move> moves = listPossibleMoves(player, pieces);

                                           if (answer.source.x == -1)
                                           {
                                               Toast popup = Toast.makeText(getApplicationContext(), "No moves can be made!", Toast.LENGTH_SHORT);
                                               popup.show();
                                           }
                                           else
                                           {
                                               Piece movedPiece = nuller;
                                               for (Piece searched : pieces)
                                                   if (searched.getX() == answer.source.x && searched.getY() == answer.source.y)
                                                   {
                                                       movedPiece = searched;
                                                       break;
                                                   }

                                               makeMove(pieces, movedPiece, answer.target.x, answer.target.y);
//                                               bestPiece = nuller;
//                                               bestX = -1;
//                                               bestY = -1;

                                               if (isInCheck(!player, pieces))
                                               {

                                                   if (!hasMoves(!player, pieces))
                                                   {
                                                       CharSequence message;
                                                       if (player)
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
                                               else if (!hasMoves(!player, pieces))
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

                                               removeTaken();
                                               updateTheBoard();
                                               player = !player;
                                           }
                                       }
                                   }
        );

    }


    public static class Move
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
    }
}


