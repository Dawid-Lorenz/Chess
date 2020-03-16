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

import java.util.ArrayList;

import static com.example.chess.Minimax.hasMoves;
import static com.example.chess.Minimax.isInCheck;
import static com.example.chess.Minimax.isMoveLegal;
import static com.example.chess.Minimax.makeMove;
import static com.example.chess.Minimax.removeTaken;
import static com.example.chess.Minimax.toCoord;
import static com.example.chess.Minimax.updateTheBoard;

public class Library extends AppCompatActivity
{
    ArrayList<Minimax.Move> moves;
    Minimax.Tree tree;

    boolean player = true;

    King kingW, kingB;

    ImageButton[][] boardButtons = new ImageButton[8][8];

    Minimax.Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setBoardToStartingPositioon();

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
                                moves.add(new Minimax.Move(new Minimax.Coord(selected.getX(), selected.getY()),
                                        new Minimax.Coord(tempX, tempY)));

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
                                        new AlertDialog.Builder(Library.this)
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
                                                        startActivity(new Intent(Library.this, MainActivity.class));
                                                    }
                                                })
                                                .show();
                                    }
                                }
                                else if (!hasMoves(board))
                                    new AlertDialog.Builder(Library.this)
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
                                                    startActivity(new Intent(Library.this, MainActivity.class));
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

        tree = new Minimax.Tree(Library.this);

        Button doneBtn = findViewById(R.id.Done);
        Button resetBtn = findViewById(R.id.Reset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tree.writeLibraryFile(moves);
                setBoardToStartingPositioon();
                updateTheBoard(board, boardButtons);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setBoardToStartingPositioon();
                updateTheBoard(board, boardButtons);
            }
        });


    }

    private void setBoardToStartingPositioon()
    {
        board = new Minimax.Board();
        moves = new ArrayList<>();
        // adding Kings to the boardButtons
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
    }
}
