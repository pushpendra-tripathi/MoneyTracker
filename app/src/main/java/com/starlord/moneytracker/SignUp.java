package com.starlord.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {
    private EditText userName, email, password;
    private Button signUpBtn;
    private TextView signUpText;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        Context context;
        progressDialog = new ProgressDialog(this);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email_singUp);
        password = findViewById(R.id.password_signUp);
        signUpBtn = findViewById(R.id.signUp);
        signUpText = findViewById(R.id.signIn_tv);

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userNameValue = userName.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();

                if (TextUtils.isEmpty(userNameValue)){
                    userName.setError("Required field");
                    return;
                }
                if (TextUtils.isEmpty(emailValue)){
                    email.setError("Required field");
                    return;
                }
                if (TextUtils.isEmpty(passwordValue)){
                    password.setError("Required field");
                    return;
                }
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //update DisplayName of the user
                            final FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userNameValue).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "User profile updated.");
                                                //Toast.makeText(SignUp.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            //After user registration
                            final Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Sign Up failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
