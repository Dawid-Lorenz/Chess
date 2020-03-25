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
import com.example.chess.Minimax;

import java.util.ArrayList;

import static com.example.chess.Minimax.hasMoves;
import static com.example.chess.Minimax.isInCheck;
import static com.example.chess.Minimax.isMoveLegal;
import static com.example.chess.Minimax.listPossibleMoves;
import static com.example.chess.Minimax.makeMove;
import static com.example.chess.Minimax.removeTaken;
import static com.example.chess.Minimax.staticEvaluation;
import static com.example.chess.Minimax.toCoord;
import static com.example.chess.Minimax.updateTheBoard;

public class Editor extends AppCompatActivity
{

    Button moveBtn, evalBtn, pieceBtn, colourBtn, modeBtn, removeBtn;

    ImageButton[][] boardButtons = new ImageButton[8][8];

    Minimax.Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        moveBtn = (Button) findViewById(R.id.moveBtn);
        evalBtn = (Button) findViewById(R.id.evalBtn);
        pieceBtn = (Button) findViewById(R.id.pieceBtn);
        colourBtn = (Button) findViewById(R.id.colourBtn);
        removeBtn = (Button) findViewById(R.id.removeBtn);
        modeBtn = (Button) findViewById(R.id.modeBtn);

        board = new Minimax.Board();
        String tempPos = "e8";
        byte[] coords = toCoord(tempPos);
        board.pieces.add(new King(coords[0], coords[1], false));
        tempPos = "e1";
        coords = toCoord(tempPos);
        board.pieces.add(new King(coords[0], coords[1], true));

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

        for (byte i = 0; i < 8; i++)
        {
            for (byte j = 0; j < 8; j++)
            {
                final byte x = i;
                final byte y = j;
                boardButtons[i][j].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Minimax.Move m = null;
                        if (modeBtn.getText().equals("Editing"))
                            if (board.selected == board.nuller)
                            {
                                boolean occupied = false;
                                for (Piece p : board.pieces)
                                {
                                    if (p.getX() == x && p.getY() == y)
                                    {
                                        occupied = true;
                                        board.selected = p;
                                    }
                                }

                                if (!occupied)
                                {
                                    String newPieceName = (String)pieceBtn.getText();
                                    Piece newPiece;

                                    if (newPieceName.equals("Pawn"))
                                    {
                                        newPiece = new Pawn(x, y, colourBtn.getText().equals("White"));
                                    }
                                    else if (newPieceName.equals("Knight"))
                                    {
                                        newPiece = new Knight(x, y, colourBtn.getText().equals("White"));
                                    }
                                    else if (newPieceName.equals("Bishop"))
                                    {
                                        newPiece = new Bishop(x, y, colourBtn.getText().equals("White"));
                                    }
                                    else if (newPieceName.equals("Rook"))
                                    {
                                        newPiece = new Rook(x, y, colourBtn.getText().equals("White"));
                                    }
                                    else
                                        newPiece = new Queen(x, y, colourBtn.getText().equals("White"));

                                    board.pieces.add(newPiece);
                                }
                            }
                            else
                            {
                                boolean occupied = false;
                                for (Piece p : board.pieces)
                                {
                                    if (p.getX() == x && p.getY() == y)
                                    {
                                        occupied = true;
                                        board.selected = p;
                                    }
                                }

                                if (!occupied)
                                {
                                    board.selected.setX(x);
                                    board.selected.setY(y);
                                    board.selected = board.nuller;
                                }

                            }
                        else
                        {
                            if (board.selected != board.nuller)
                            {
                                Piece selected = board.selected;
                                m = new Minimax.Move(new Minimax.Coord(selected.getX(), selected.getY()),
                                        new Minimax.Coord(x, y));
                                if (isMoveLegal(board, selected, x, y))
                                {
                                    makeMove(board, selected, x, y);

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
                                            new AlertDialog.Builder(Editor.this)
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
                                                            startActivity(new Intent(Editor.this, MainActivity.class));
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                    else if (!hasMoves(board))
                                        new AlertDialog.Builder(Editor.this)
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
                                                        startActivity(new Intent(Editor.this, MainActivity.class));
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
                                    if (board.player == p.isColour() && p.getX() == x && p.getY() == y)
                                    {
                                        board.selected = p;
                                        break;
                                    }
                                }
                            }
                        }
                        removeTaken(board);
                        updateTheBoard(board, boardButtons, m);
                    }
                });
            }
        }


        colourBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (colourBtn.getText().equals("White"))
                    colourBtn.setText("Black");
                else
                    colourBtn.setText("White");
            }
        });

        pieceBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String newPieceName = (String)pieceBtn.getText();

                if (newPieceName.equals("Pawn"))
                {
                    pieceBtn.setText("Knight");
                }
                else if (newPieceName.equals("Knight"))
                {
                    pieceBtn.setText("Bishop");
                }
                else if (newPieceName.equals("Bishop"))
                {
                    pieceBtn.setText("Rook");
                }
                else if (newPieceName.equals("Rook"))
                {
                    pieceBtn.setText("Queen");
                }
                else
                    pieceBtn.setText("Pawn");
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!(board.selected instanceof King))
                    board.pieces.remove(board.selected);
                board.selected = board.nuller;
                updateTheBoard(board, boardButtons, null);
            }
        });

        modeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (modeBtn.getText().equals("Editing"))
                    modeBtn.setText("Playing");
                else
                    modeBtn.setText("Editing");
            }
        });

        moveBtn.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(View v)
                                       {

                   Minimax.Move answer = Minimax.alfaBeta(board, 2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                   ArrayList<Minimax.Move> moves = listPossibleMoves(board);

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
                               new AlertDialog.Builder(Editor.this)
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
                                               startActivity(new Intent(Editor.this, MainActivity.class));
                                           }
                                       })
                                       .show();
                           }
                       }
                       else if (!hasMoves(board))
                           new AlertDialog.Builder(Editor.this)
                                   .setTitle("Game over")
                                   .setMessage("Stalemate!")
                                   .setNeutralButton("Show the boardButtons", new DialogInterface.OnClickListener()
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
                                           startActivity(new Intent(Editor.this, MainActivity.class));
                                       }
                                   })
                                   .show();

                       removeTaken(board);
                       updateTheBoard(board, boardButtons, answer);
                   }
               }
           }
        );

        evalBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                double score = staticEvaluation(board);
                Toast popup = Toast.makeText(getApplicationContext(), "Current value is: " + score, Toast.LENGTH_SHORT);
                popup.show();
            }
        });


        updateTheBoard(board, boardButtons, null);
    }

}
