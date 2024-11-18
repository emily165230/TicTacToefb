package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ShapeableImageView imageView;
    TextView name, mail;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                auth = FirebaseAuth.getInstance();
                                Glide.with(MainActivity.this)
                                        .load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl())
                                        .into(imageView);

                                name.setText(auth.getCurrentUser().getDisplayName());
                                mail.setText(auth.getCurrentUser().getEmail());

                                Toast.makeText(MainActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
    }
    });


    String turn;
    String[][] board;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        imageView = findViewById(R.id.profileimage);
        name = findViewById(R.id.nameTV);
        mail = findViewById(R.id.mailTV);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, options);

        auth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }


            private void onNewGame() {
                board = new String[3][3];
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        board[row][col] = "";
                    }
                }

                int[] buttonIds = {R.id.btn_00, R.id.btn_01, R.id.btn_02,
                        R.id.btn_10, R.id.btn_11, R.id.btn_12,
                        R.id.btn_20, R.id.btn_21, R.id.btn_22};

                for (int id : buttonIds) {
                    Button btn = findViewById(id);
                    btn.setText("");
                }

                turn = "X";
                count = 0;
            }

            public void onButtonClick(View view) {
                if (view.getId() == R.id.btn_00)
                    handleClick(0, 0, R.id.btn_00);
                else if (view.getId() == R.id.btn_01)
                    handleClick(0, 1, R.id.btn_01);
                else if (view.getId() == R.id.btn_02)
                    handleClick(0, 2, R.id.btn_02);
                else if (view.getId() == R.id.btn_10)
                    handleClick(1, 0, R.id.btn_10);
                else if (view.getId() == R.id.btn_11)
                    handleClick(1, 1, R.id.btn_11);
                else if (view.getId() == R.id.btn_12)
                    handleClick(1, 2, R.id.btn_12);
                else if (view.getId() == R.id.btn_20)
                    handleClick(2, 0, R.id.btn_20);
                else if (view.getId() == R.id.btn_21)
                    handleClick(2, 1, R.id.btn_21);
                else if (view.getId() == R.id.btn_22)
                    handleClick(2, 2, R.id.btn_22);
            }

            private void handleClick(int row, int col, int id) {
                if (board[row][col].isEmpty()) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Game Over");
                builder.setMessage(message);

                builder.setPositiveButton("EXIT", (dialogInterface, i) -> finish());

                builder.setNegativeButton("Play Again", (dialogInterface, i) -> onNewGame());
                builder.show();
            }

            private boolean isWinner() {
                // בדיקת שורות
                for (int row = 0; row < 3; row++) {
                    if (!board[row][0].isEmpty() && board[row][0].equals(board[row][1]) && board[row][1].equals(board[row][2])) {
                        return true;
                    }
                }

                // בדיקת עמודות
                for (int col = 0; col < 3; col++) {
                    if (!board[0][col].isEmpty() && board[0][col].equals(board[1][col]) && board[1][col].equals(board[2][col])) {
                        return true;
                    }
                }

                // בדיקת אלכסון ראשי
                if (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
                    return true;
                }

                // בדיקת אלכסון משני
                if (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
                    return true;
                }

                return false;
            }

        });
    }
}
