package com.example.onlinequizexpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onlinequizexpo.BroadcastReceiver.AlarmReceiver;
import com.example.onlinequizexpo.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity {
    MaterialEditText edtNewUser, edtNewEmail, edtNewPassword; //for sign up
    Spinner spinnerAge;
    String userAge;
    MaterialEditText edtUser, edtPassword; //for sign in

    String[] listAge;

    Button btnSignUp, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        registerAlarm();

        //Fireabase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtUser = findViewById(R.id.et_user_name);
        edtPassword = findViewById(R.id.et_password);

        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUser.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void registerAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,8); //9th hour
        calendar.set(Calendar.MINUTE,1); //40th minute
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        assert am != null;
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void signIn(final String user, final String pwd) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user).exists()){
                    if (!user.isEmpty()){
                        User login = dataSnapshot.child(user).getValue(User.class);
                        if (login.getPassword().equals(pwd)) {
                            Intent homeActivity = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = login;
                            startActivity(homeActivity);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.activity_sign_up, null);

        edtNewUser = sign_up_layout.findViewById(R.id.et_new_user_name);
        spinnerAge = sign_up_layout.findViewById(R.id.spinner_age);
//        edtNewEmail = sign_up_layout.findViewById(R.id.et_new_email);
        edtNewPassword = sign_up_layout.findViewById(R.id.et_new_password);





        alertDialog.setView(sign_up_layout);
        //using paper plane icon for FAB
        FontDrawable drawable = new FontDrawable(this, R.string.fa_user_circle, true,false);
        alertDialog.setIcon(drawable);

        listAge = getResources().getStringArray(R.array.age);

        if (spinnerAge != null){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listAge);
        spinnerAge.setAdapter(adapter);

            spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0){
                        userAge = String.valueOf(listAge[position]);
                        Toast.makeText(MainActivity.this, userAge, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }


        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                               final User user = new User(edtNewUser.getText().toString(),
                        userAge,
                        edtNewPassword.getText().toString());

                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUserName()).exists())
                            Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    else {
                        users.child(user.getUserName()).setValue(user);
                            Toast.makeText(MainActivity.this, "User Registration Successful", Toast.LENGTH_SHORT).show();
                        }
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
}
