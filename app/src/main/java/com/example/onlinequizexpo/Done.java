package com.example.onlinequizexpo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.onlinequizexpo.Model.QuestionScore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Done extends AppCompatActivity {

    Button btnTryAgain;
    TextView txtResultScore,getTextResultQuestion;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference question_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");

        txtResultScore = findViewById(R.id.txtTotalScore);
        getTextResultQuestion = findViewById(R.id.txtTotalQuestion);
        progressBar = findViewById(R.id.doneProgressBar);
        btnTryAgain = findViewById(R.id.btn_try_again);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Done.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        //Get data from bundle and set to view
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            int score = extras.getInt("SCORE");
            int totalQuestion = extras.getInt("TOTAL");
            int correctAsnwer = extras.getInt("CORRECT");

            txtResultScore.setText(String.format("SCORE : %d", score));
            getTextResultQuestion.setText(String.format("PASSED : %d / %d",correctAsnwer,totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAsnwer);

            //Upload score to DB
            question_score.child(String.format("%s_%s", Common.currentUser.getUserName(),
                    Common.categoryId))
                    .setValue(new QuestionScore(String.format("%s_%s", Common.currentUser.getUserName(),
                            Common.categoryId),
                            Common.currentUser.getUserName(),
                            String.valueOf(score),
                            Common.categoryId,
                            Common.categoryName));
        }

    }
}
