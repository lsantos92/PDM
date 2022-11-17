package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import io.perfmark.Tag;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    EditText rNome, rEmail, rContacto, rPassword;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        rNome = findViewById(R.id.regNome);
        rPassword = findViewById(R.id.regPassword);
        rEmail = findViewById(R.id.regEmail);
        rContacto = findViewById(R.id.regContacto);
        Button rButton = (Button) findViewById(R.id.regButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        rButton.setOnClickListener(new View.OnClickListener(){
            public void onClick ( View v ){
                final  String nome = rNome.getText().toString().trim();
                final String password = rPassword.getText().toString().trim();
                final String email = rEmail.getText().toString();
                final String contacto = rContacto.getText().toString();

                if(TextUtils.isEmpty(nome)){
                    rNome.setError("O campo nome é obrigatório!");
                    return;
                }

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

                //REGISTO NA FIREBASE

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Registered!", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference docReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("nome",nome);
                            user.put("email",email);
                            user.put("password",password);
                            user.put("contacto",contacto);
                            docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user profile is created for" +userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"OnFailure: " +e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Could not register user!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

}