package com.akif.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private TextView backButton;

    private TextInputEditText loginEmail,loginPasword;
    private TextView forgotPasword;
    private Button loginButton;

    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull  FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                        if(user!=null){
                            Intent intent=new Intent(loginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

            }
        };


        backButton=findViewById(R.id.backButton);
        loginEmail=findViewById(R.id.loginEmail);
        loginPasword=findViewById(R.id.loginPasword);
        forgotPasword=findViewById(R.id.forgotPasword);
        loginButton=findViewById(R.id.loginButton);

        loader=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(loginActivity.this,selectRegistrationActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=loginEmail.getText().toString().trim();
                final String password=loginPasword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Email adresinizi giriniz");
                }
                if(TextUtils.isEmpty(password)){
                    loginPasword.setError("Şifrenizi giriniz");
                }
                else {
                    loader.setMessage("Giriş Yapılıyor");
                    loader.setCanceledOnTouchOutside(false);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(loginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(loginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(loginActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();

                        }
                    });

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}























