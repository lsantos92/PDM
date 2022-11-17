package pt.ubi.di.pdm.happeningubifinal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class userFragment extends Fragment {

    private static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, alteraNome, alteraPassword, alteraContacto, delPass, delEmail;
    Button changePassword,changeName, changeContact, editPost, deleteAcc;
    EditText oldPassword, newPassword, newContact, newName, confirmPass, confirmEmail;
   public static String compPass;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        changeName = view.findViewById(R.id.mudarNome);
        changePassword = view.findViewById(R.id.mudarPassword);
        changeContact = view.findViewById(R.id.mudarContacto);
        newName = view.findViewById(R.id.alteraNome);
        oldPassword = view.findViewById(R.id.oldPassword);
        newContact = view.findViewById(R.id.newContact);
        newPassword = view.findViewById(R.id.newPassword);
        editPost = view.findViewById(R.id.editPost);
        deleteAcc = view.findViewById(R.id.deleteAcc);
        confirmPass = view.findViewById(R.id.confirmPass);
        confirmEmail = view.findViewById(R.id.confirmEmail);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        final DocumentReference dbRef;

        dbRef = fStore.collection("users").document(userID);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraNome = newName.getText().toString().trim();
                if(TextUtils.isEmpty(alteraNome)){
                    newName.setError("Este campo não pode estar vazio!");
                    return;
                }
                dbRef.update("nome", alteraNome).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "nome mudado com sucesso para: " +alteraNome, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: nome mudado com sucesso!" +userID);
                        newName.setText(null);
                    }
                });
            }


        });

        changeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraContacto = newContact.getText().toString().trim();
                if(TextUtils.isEmpty(alteraContacto)){
                    newName.setError("Este campo não pode estar vazio!");
                    return;
                }
                dbRef.update("contacto", alteraContacto).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "contacto mudado com sucesso para:  " +alteraContacto, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: nome mudado com sucesso!" +userID);
                        newName.setText(null);
                    }
                });
            }


        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                compPass = document.getString("password");
                            }
                            alteraPassword = newPassword.getText().toString().trim();
                            if (TextUtils.isEmpty(alteraPassword)) {
                                newPassword.setError("Este campo não pode estar vazio!");
                                return;
                            }
                            String passAntinga = oldPassword.getText().toString();
                            if (compPass.equals(passAntinga)) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                user.updatePassword(alteraPassword);
                                dbRef.update("password", alteraPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "nome mudado com sucesso para: " + alteraPassword, Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onSuccess: nome mudado com sucesso!" + userID);
                                        newContact.setText(null);
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "A password que introduziu está errada!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



            }


        });

        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), editEvent.class);
                startActivity(intent);
            }
        });

        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delEmail = confirmEmail.getText().toString().trim();
                delPass = confirmPass.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                try {

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(delEmail, delPass);
                    if(user!=null) {
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(),"user apagado com sucesso",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }catch (Exception e)
                {
                    //error
                }

            }


            });
    }}



