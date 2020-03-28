package com.starlord.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    private EditText email, password;
    private Button signInBtn;
    private TextView signInText, resetText;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Context context;
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_singIn);
        password = findViewById(R.id.password_signIn);
        signInBtn = findViewById(R.id.signIn);
        signInText = findViewById(R.id.signUp_tv);
        resetText = findViewById(R.id.reset_tv);

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();
                 if (TextUtils.isEmpty(emailValue)){
                     email.setError("Required field");
                     return;
                 }
                 if (TextUtils.isEmpty(passwordValue)){
                     password.setError("Required field");
                     return;
                 }
                progressDialog.setMessage("Processing...");
                progressDialog.show();

                 mAuth.signInWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()){
                             Toast.makeText(SignIn.this, "You signed in successfully", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                             finish();
                             progressDialog.dismiss();
                         }
                         else {
                             Toast.makeText(SignIn.this, "Login failed", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });

            }
        });

        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDialog();
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void resetDialog(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.password_reset_layout,null);
        final AlertDialog dialog = myDialog.create();
        dialog.setView(view);
        dialog.setCancelable(false);

        final EditText email = view.findViewById(R.id.email_reset);
        Button cancel = view.findViewById(R.id.cancel_reset);
        Button send = view.findViewById(R.id.send_reset);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailValue = email.getText().toString().trim();
                if (TextUtils.isEmpty(emailValue)){
                    email.setError("Required");
                    return;
                }
                mAuth.sendPasswordResetEmail(emailValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignIn.this, "Password reset link has been sent to your registered email id",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
