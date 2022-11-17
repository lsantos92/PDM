package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText rEmail, rPassword;
    Button loginButton, registerButton;
    ProgressBar progressBar;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rEmail = findViewById(R.id.logEmail);
        rPassword = findViewById(R.id.logPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        fAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(login.this, Register.class);

                login.this.startActivity(myIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = rPassword.getText().toString().trim();
                String email = rEmail.getText().toString().trim();

                if(TextUtils.isEmpty(password)){
                    rEmail.setError("O campo password é obrigatório!");
                    return;
                }

                if(password.length() < 6){
                    rPassword.setError("Password tem de ter 6 caracteres no minimo");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    rEmail.setError("O campo email é obrigatório!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //autenticar o user

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(login.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), dashboard.class));
                        }else{
                            Toast.makeText(login.this, "Could not register user!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }


        });
    }
}