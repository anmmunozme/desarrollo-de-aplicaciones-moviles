package com.example.angee.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;


    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private Button mButtonMenu;

    private TextView mHumanCount;
    private TextView mAndroidCount;
    private TextView mTieCount;

    private int mHumanCounter = 0;
    private int mAndroidCounter = 0;
    private int mTieCounter = 0;

    private boolean mHumanFirst = true;

    private boolean mGameOver = false;

    private BoardView mBoardView;
    private char mGoFirst = TicTacToeGame.HUMAN_PLAYER;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
/*
    private SoundPool mSounds;
    private int mHumanMoveSoundID;
    private int mComputerMoveSoundID;
*/
    private SharedPreferences mPrefs;

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.x_so);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.o_so);
       // mHumanMoveSoundID = mSounds.load(this, R.raw.x_so, 1);
        //mComputerMoveSoundID = mSounds.load(this, R.raw.o_so, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //para el tablero con imagenes
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        //guardar
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores
        mHumanCounter = mPrefs.getInt("mHumanWins", 0);
        mAndroidCounter = mPrefs.getInt("mComputerWins", 0);
        mTieCounter = mPrefs.getInt("mTies", 0);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

/*
        mBoardButtons = new Button[TicTacToeGame.getBOARD_SIZE()];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
*/
        //mButtonMenu = (Button) findViewById(R.id.menu);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mHumanCount = (TextView) findViewById(R.id.humanCount);
        mAndroidCount = (TextView) findViewById(R.id.androidCount);
        mTieCount = (TextView) findViewById(R.id.tiesCount);

        mHumanCount.setText(Integer.toString(mHumanCounter));
        mAndroidCount.setText(Integer.toString(mAndroidCounter));
        mTieCount.setText(Integer.toString(mTieCounter));


       // mGame = new TicTacToeGame();

        //mButtonMenu.setOnClickListener(new ButtonClickListener());
        //startNewGame();

        //saved
        if (savedInstanceState == null) {
            startNewGame();
        }

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mHumanCounter = savedInstanceState.getInt("mHumanWins");
        mAndroidCounter = savedInstanceState.getInt("mComputerWins");
        mTieCounter = savedInstanceState.getInt("mTies");
        mGoFirst = savedInstanceState.getChar("mGoFirst");
        mHumanFirst = savedInstanceState.getBoolean("mHumanFirst");
        displayScores();
    }
    private void displayScores() {
        mHumanCount.setText(Integer.toString(mHumanCounter));
        mAndroidCount.setText(Integer.toString(mAndroidCounter));
        mTieCount.setText(Integer.toString(mTieCounter));
    }


    private void startNewGame() {
        mBoardView.invalidate();   // Redraw the board
        mGame.clearBoard();
        //mHumanFirst = !mHumanFirst;

        // Human start
        if(mHumanFirst) {
            mInfoTextView.setText(R.string.first_human);
            //and android start
            mHumanFirst = false;
        }
        else{
            mInfoTextView.setText(R.string.turn_computer);
            int move = mGame.getComputerMove();
            setMove(mGame.COMPUTER_PLAYER, move);
            mComputerMediaPlayer.start();
            mHumanFirst = true;
        }
        //mBoardView.invalidate();   // Redraw the board

        mGameOver = false;
        mInfoTextView.setText(R.string.first_human);

    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched	    	
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            //if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos) )	{
            if (!mGameOver){
                setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                mHumanMediaPlayer.start();

                // If no winner yet, let the computer make a move
                 int winner = mGame.checkForWinner();
                 if (winner == 0) {
                     mInfoTextView.setText(R.string.turn_computer);
                     turnComputer();
                     winner = mGame.checkForWinner();
                    }
                    //winner = mGame.checkForWinner();

                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        mTieCounter++;
                        mTieCount.setText(Integer.toString(mTieCounter));
                        mGameOver = true;
                    } else if (winner == 2) {
                        mInfoTextView.setText(R.string.result_human_wins);
                        mHumanCounter++;
                        mHumanCount.setText(Integer.toString(mHumanCounter));
                        mGameOver = true;
                    } else {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mAndroidCounter++;
                        mAndroidCount.setText(Integer.toString(mAndroidCounter));
                        mGameOver = true;
                    }

                mBoardView.invalidate();
            }
                return false;
        }
    };
    /*
    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(){ }
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
           /* if(mButtonMenu.isPressed()){
                mGameOver=true;
                mGameOver = false;
                startNewGame();
                mGame.clearBoard();
                mBoardButtons[0].setText("");
            }//
            if(!mGameOver) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);

                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = mGame.getComputerMove();
                        //setMove(mGame.COMPUTER_PLAYER, move);
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }

                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        mTieCounter++;
                        mTieCount.setText(Integer.toString(mTieCounter));
                        mGameOver = true;
                    } else if (winner == 2) {
                        mInfoTextView.setText(R.string.result_human_wins);
                        mHumanCounter++;
                        mHumanCount.setText(Integer.toString(mHumanCounter));
                        mGameOver = true;
                    } else {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mAndroidCounter++;
                        mAndroidCount.setText(Integer.toString(mAndroidCounter));
                        mGameOver = true;
                    }
                }
            }
        }
    }
    */

    //	It	updates	the	board	model,	disables	the	button,	sets	the	text
    // of the	button	to	X	or	O, and	makes	the	X	green	and	the	O	red.
    private boolean setMove(char player, int location) {
            if (mGame.setMove(player, location)) {
                mBoardView.invalidate(); // Redraw the board

                return true;
            }
            return false;
    }

        /*mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(247, 34, 87));
            //mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(26, 190, 238));
            //mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add("New Game");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.resetScores:
                mHumanCounter=0;
                mAndroidCounter=0;
                mTieCounter=0;
                displayScores();
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout)
                        .setPositiveButton("OK", null)
                        .create().show();
                //Dialog dialog = builder.create();

                return true;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // Set selected, an integer (0 to n-1), for the Difficulty dialog.
                int selected = mGame.getDifficultyLevel().ordinal();

                // selected is the radio button that should be selected.
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                // TODO: Set the diff level of mGame based on which item was selected.
                                TicTacToeGame.DifficultyLevel dl = TicTacToeGame.DifficultyLevel.values()[item];
                                mGame.setDifficultyLevel(dl);


                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    private void turnComputer() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                int move = mGame.getComputerMove();
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                mComputerMediaPlayer.start();
                mBoardView.invalidate();
            }
        }, 1000);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanCounter));
        outState.putInt("mComputerWins", Integer.valueOf(mAndroidCounter));
        outState.putInt("mTies", Integer.valueOf(mTieCounter));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putBoolean("mHumanFirst", mHumanFirst);
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanCounter);
        ed.putInt("mComputerWins", mAndroidCounter);
        ed.putInt("mTies", mTieCounter);
        ed.commit();
    }

}
