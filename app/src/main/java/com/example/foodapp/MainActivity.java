package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.Common.Common;
import com.example.foodapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp,btnSignIn;
    TextView txtSlogan;

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);


        btnSignIn=(Button) findViewById(R.id.btnSignIn);
        btnSignUp=(Button) findViewById(R.id.btnSignUp);

        txtSlogan=(TextView) findViewById(R.id.txtSlogan);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);
        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp=new Intent(MainActivity.this,SignUp.class);
                startActivity(signUp);

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn=new Intent(MainActivity.this,SignIn.class);
                startActivity(signIn);
            }
        });

        String user=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);
        if (user !=null && pwd !=null){
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }

    }

    private void login(String phone, String pwd) {
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        ProgressDialog mDialog=new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(phone).exists()) {

                    mDialog.dismiss();
                    User user = snapshot.child(phone).getValue(User.class);
                    user.setPhone(phone);//set Phone
                    if (user.getPassword().equals(pwd)) {
                        Intent homeIntent=new Intent(MainActivity.this,Home.class);
                        Common.currentUser=user;
                        startActivity(homeIntent);
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Wrong password!!!!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this,"User do not exist in Database",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}