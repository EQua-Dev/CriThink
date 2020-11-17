package com.example.onlinequizexpo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Playing extends AppCompatActivity implements View.OnClickListener {

    final static long INTERVAL = 1000; //1 sec
    final static long TIMEOUT = 15000; //15 secs
    int progressValue = 0;

    CountDownTimer mCountDown;

    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference questions;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestionNum, questionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //Firebase
        database = FirebaseDatabase.getInstance();
        questions = database.getReference("Questions");

        //Views
        txtScore = findViewById(R.id.txt_score);
        txtQuestionNum = findViewById(R.id.txt_total_question);
        questionText = findViewById(R.id.question_text);
        question_image = findViewById(R.id.question_image);

        progressBar = findViewById(R.id.progress_bar);

        btnA = findViewById(R.id.btn_answerA);
        btnB = findViewById(R.id.btn_answerB);
        btnC = findViewById(R.id.btn_answerC);
        btnD = findViewById(R.id.btn_answerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mCountDown.cancel();
        if (index < totalQuestion) // questions have not been exhausted
        {
            Button clickedbutton = (Button)v;
            if (clickedbutton.getText().equals(Common.questionList.get(index).getCorrectAnswer())){
                //choose correct answer
                score+=10;
                correctAnswer++;
                showQuestion(++index); // next question
            }else {
                // choose wrong question
                Intent intent = new Intent(this, Done.class );
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE",score);
                dataSend.putInt("TOTAL",totalQuestion);
                dataSend.putInt("CORRECT",correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
            txtScore.setText(String.format("%d", score));
        }
    }

    private void showQuestion(int i) {
        if (index < totalQuestion){
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d/ %d", thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            if (Common.questionList.get(index).getIsImageQuestion().equals("true")){
                //if it is an image question
                Picasso.get().load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                questionText.setVisibility(View.INVISIBLE);
            }
            else {
                //if it is a text question
                questionText.setText(Common.questionList.get(index).getQuestion());
                question_image.setVisibility(View.INVISIBLE);
                questionText.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            mCountDown.start(); //start timer
        }else {
            //if it is the last question
            Intent intent = new Intent(this, Done.class );
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
        }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        mCountDown = new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}
