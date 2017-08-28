package com.example.angee.tictactoe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //mButtonMenu = (Button) findViewById(R.id.menu);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mHumanCount = (TextView) findViewById(R.id.humanCount);
        mAndroidCount = (TextView) findViewById(R.id.androidCount);
        mTieCount = (TextView) findViewById(R.id.tiesCount);

        mHumanCount.setText(Integer.toString(mHumanCounter));
        mAndroidCount.setText(Integer.toString(mAndroidCounter));
        mTieCount.setText(Integer.toString(mTieCounter));


        mGame = new TicTacToeGame();

        //mButtonMenu.setOnClickListener(new ButtonClickListener());
        startNewGame();
    }

    private void startNewGame() {

        mGame.clearBoard();

        // Reset buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
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
            mHumanFirst = true;
        }

        mGameOver = false;

    }

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
            }*/
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

    //	It	updates	the	board	model,	disables	the	button,	sets	the	text
    // of the	button	to	X	or	O, and	makes	the	X	green	and	the	O	red.
    private void setMove(char player, int location) {

        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(247, 34, 87));
            //mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(26, 190, 238));
            //mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

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
        startNewGame();
        return true;
    }

}
