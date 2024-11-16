package com.example.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    String turn;
    String[][] board;
    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }
    private void onNewGame() {
        board = new String[3][3];
        for (int row=0; row < 3; row++)
            for (int col=0; col < 3; col++)
                board[row][col] = new String();

        turn = "X";
        count = 0;
    }

    public void onButtonClick(View view) {
        if(view.getId() == R.id.btn_00)
            handleClick(0, 0 , R.id.btn_00);

        if(view.getId() == R.id.btn_01)
            handleClick(0, 1, R.id.btn_01);

        if(view.getId() == R.id.btn_02)
            handleClick(0, 2 , R.id.btn_02);

        if(view.getId() == R.id.btn_10)
            handleClick(0, 10, R.id.btn_10);

        if(view.getId() == R.id.btn_11)
            handleClick(0, 11, R.id.btn_11);

        if(view.getId() == R.id.btn_12)
            handleClick(0, 12, R.id.btn_12);

        if(view.getId() == R.id.btn_20)
            handleClick(0, 20, R.id.btn_20);

        if(view.getId() == R.id.btn_21)
            handleClick(0, 21, R.id.btn_21);

        if(view.getId() == R.id.btn_22)
            handleClick(0, 22, R.id.btn_22);
    }
    private void handleClick(int row, int col, int id) {
        if (board[row][col].equals("")) {
            board[row][col] = turn;
            Button btn = findViewById(id);
            btn.setText(turn);
            onTurnEnd();
        }
    }
    private void onTurnEnd() {
        if (isWinner())
            endGame(turn + " won!");
        else {
            count++;
            if (count == 9)
                endGame("Tie");
            else {
                turn = (turn.equals("X") ? "O" : "X");
            }
        }
    }

    private void endGame(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage(message);
        builder.setPositiveButton("EXIT", (dialogInterface, i) -> {
            finish();
        });
        builder.setNegativeButton("Play Again", (dialogInterface, i) -> {
            onNewGame();
        });
        builder.show();
    }

    private boolean isWinner() {

        for (int i = 0; i < 3; i++) {
            if (!board[i][0].equals("") && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (!board[0][i].equals("") && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) {
                return true;
            }
        }
        if (!board[0][0].equals("") && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            return true;
        }
        if (!board[0][2].equals("") && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            return true;
        }
        return false;
    }


}