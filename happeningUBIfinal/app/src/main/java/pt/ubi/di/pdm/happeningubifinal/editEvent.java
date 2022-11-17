package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;

public class editEvent extends AppCompatActivity {
    EditText newNome, newDescricao;
    Button addImg, upImg, editEvent;
    Spinner spinner;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CollectionReference ref;
    private static String userID;
    private static String selectedName;
    Uri mImageUri;
    FirebaseStorage fStorage;
    StorageReference storageReference;
    private static StorageTask UploadTask;
    String imageURL;
    private static final int PICK_IMAGE_REQUEST = 1;
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        newNome = findViewById(R.id.novoNome);
        newDescricao = findViewById(R.id.novaDescricao);
        addImg = findViewById(R.id.selectImage);
        upImg = findViewById(R.id.uploadImagem);
        editEvent = findViewById(R.id.editarEvento);
        spinner = findViewById(R.id.spinnerEvent);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getUid();
        ref = fStore.collection("posts");
        setSpinnerChoice(ref);

         storageReference = FirebaseStorage.getInstance().getReference("images");

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novoNome = newNome.getText().toString().trim();
                String novaDesc = newDescricao.getText().toString().trim();
                if(TextUtils.isEmpty(novoNome)){
                    newNome.setError("Este campo não pode estar vazio!");
                    return;
                }
                if(TextUtils.isEmpty(novaDesc)){
                    newDescricao.setError("Este campo não pode estar vazio!");
                }
                ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("id").equals(userID) && document.getString("nome").matches(selectedName)){
                                    String docRef = document.getId();
                                    ref.document(docRef).update("nome", newNome);
                                    ref.document(docRef).update("descricao", newDescricao);
                                    ref.document(docRef).update("imagem", imageURL);
                                }
                            }

                        }
                    }
                });

            }});
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }

        });


        upImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UploadTask != null && UploadTask.isInProgress()) {
                    Toast.makeText(getBaseContext(), "upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
    }

    public void setSpinnerChoice (CollectionReference choice){
        final List<String> posts = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_selectable_list_item,posts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        choice.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("id").equals(userID)){
                        String nomeList = document.getString("nome");
                        posts.add(nomeList);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(posts.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Não existe eventos guardados", Toast.LENGTH_SHORT).show();

                }else{
                    selectedName = posts.get(position);
                    Toast.makeText(getApplicationContext(), "Evento selecionado: " +selectedName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }
    private void uploadFile(){
        if(mImageUri != null) {
            StorageReference fileReference = storageReference.child("images");
            UploadTask = fileReference.putFile(mImageUri);
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageURL = uri.toString();
                    Toast.makeText(getApplicationContext(),imageURL,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
