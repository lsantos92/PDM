package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class eventBriefFragment extends Fragment {
    TextView nomeEvento, nomeUser, descricao, data;
    ImageView img;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID, userNome, nomeEv, getData, getDesc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_event_brief_fragment,container,false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getUid();
        nomeEvento = view.findViewById(R.id.nome);
        nomeUser = view.findViewById(R.id.nomepessoa);
        img = view.findViewById(R.id.image);
        data = view.findViewById(R.id.data);
        descricao = view.findViewById(R.id.descricao);

        nomeEv = DataHolder.getInstance().getItem();

        Toast.makeText(getActivity(), nomeEv, Toast.LENGTH_SHORT).show();
       // setData(fStore.collection("posts"));


        CollectionReference ref = fStore.collection("posts");
        setData(ref);
        nomeEvento.setText(nomeEv);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DocumentReference reference = fStore.collection("users").document(userID);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        userNome = document.getString("nome");
                        Toast.makeText(getContext(), userNome, Toast.LENGTH_SHORT).show();
                    }

                    nomeUser.setText("Evento Criado por: " +userNome);

                }
            }
        });








    }
    void setData(CollectionReference ref) {

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nome = document.getString("nome");
                        if(nome.equals(nomeEv)){
                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.mipmap.ic_launcher_round)
                                    .error(R.mipmap.ic_launcher_round);
                            Glide.with(getActivity()).load(document.getString("imagem")).apply(options).into(img);

                            data.setText("criado em: " + document.getString("data"));
                            descricao.setText("Descrição: " + document.getString("descricao"));
                        }
                    }
                }
            }
        });
    }
}
