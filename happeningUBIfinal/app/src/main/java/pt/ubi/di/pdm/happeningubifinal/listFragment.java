package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class listFragment extends Fragment {
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private RecyclerView eventList;
    FirestoreRecyclerAdapter adapter;
    private String imgURL;
    private String passName;
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    final CollectionReference posts = FirebaseFirestore.getInstance().collection("posts");



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_list_fragment,container,false);
        eventList = view.findViewById(R.id.eventList);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();

        //eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();

        final Query query = fStore.collection("posts");
        FirestoreRecyclerOptions<eventModel> options =
                new FirestoreRecyclerOptions.Builder<eventModel>().setQuery(query,eventModel.class)
                        .build();
        adapter = new FirestoreRecyclerAdapter<eventModel, eventViewHolder>(options) {


            @NonNull
            @Override
            public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_event, parent,false);
                eventViewHolder viewHolder = new eventViewHolder(v);
                return viewHolder;
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onBindViewHolder(@NonNull eventViewHolder holder, int position, @NonNull final eventModel model) {
                if(holder != null) {
                    /*String data = model.getData();
                    LocalDateTime localDateTime = LocalDateTime.parse(data,
                            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a"));
                    long millis = localDateTime
                            .atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli();
                    long currentTime = System.currentTimeMillis();
                    if(currentTime < millis+24 * 60 * 60 * 1000 ){*/
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round);
                    passName = model.getNome();
                    imgURL = model.getImagem();
                    Glide.with(getActivity()).load(imgURL).apply(options).into(holder.img);
                    holder.name.setText(model.getNome());
                    holder.localizacao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String lat = model.getLatitude();
                            String lon = model.getLongitude();

                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+", "+lon);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                            Intent showEvent = new Intent(getContext(), visualizarEvento.class);
                            showEvent.putExtra("nome",passName);


                            startActivity(showEvent);
                        }
                    });
                }
            //}

        };




        eventList.setAdapter(adapter);
        adapter.startListening();

    }

    static class eventViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private Button localizacao;
        private ImageView img;

        public eventViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nome);
            localizacao = itemView.findViewById(R.id.localizacao);
            img = itemView.findViewById(R.id.image);


        }
    }









    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}