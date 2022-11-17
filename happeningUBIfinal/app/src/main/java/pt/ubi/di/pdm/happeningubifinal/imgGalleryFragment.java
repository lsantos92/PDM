package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class imgGalleryFragment extends Fragment  {
    private static final int PICK_IMAGE = 1;
    String imgURL;
    String evN;
    Button upload, choose;
    ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Uri imgUri;
    ProgressBar progressBar;
    private int uploadCount = 0;
    private FirebaseFirestore fStore;
    FirestoreRecyclerAdapter adapter;
    private RecyclerView listImages;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_img_gallery_fragment, container, false);
        upload = view.findViewById(R.id.add);
        choose = view.findViewById(R.id.choose);
        progressBar = view.findViewById(R.id.progressBarUpload);
        progressBar.setVisibility(View.GONE);
        listImages = view.findViewById(R.id.listImages);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();

        //eventList.setHasFixedSize(true);
        listImages.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public void onStart() {
        super.onStart();
        evN = DataHolder.getInstance().getItem().trim();
        Toast.makeText(getActivity(), evN, Toast.LENGTH_SHORT).show();

        Toast.makeText(getActivity(), evN, Toast.LENGTH_SHORT).show();
        final Query query1 = fStore.collection(evN);
        FirestoreRecyclerOptions<getImages> options =
                new FirestoreRecyclerOptions.Builder<getImages>().setQuery(query1 , getImages.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<getImages, eventViewHolder2>(options) {


            @NonNull
            @Override
            public eventViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagegallery_show, parent,false);
                eventViewHolder2 viewHolder = new eventViewHolder2(v);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull eventViewHolder2 holder, int position, @NonNull final getImages getImages) {
                if(holder != null) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round);


                    Glide.with(getView()).load(getImages.getImgLink()).apply(options).into(holder.img);

                }
            }


        };
        adapter.startListening();
        listImages.setAdapter(adapter);


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageGallery");

                for(uploadCount = 0; uploadCount < ImageList.size(); uploadCount++){
                    Uri IndividualImage = ImageList.get(uploadCount);
                    final StorageReference ImageName = ImageFolder.child("Image" + IndividualImage.getLastPathSegment());
                    ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);
                                    saveLink(url);
                                }
                            });
                        }
                    });
                }
            }
        });



    }
    static class eventViewHolder2 extends RecyclerView.ViewHolder {


        private ImageView img;

        public eventViewHolder2(@NonNull final View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imagem);


        }


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null){
                    int countClipData = data.getClipData().getItemCount();

                    int currentImageSelect = 0;

                    while(currentImageSelect < countClipData){
                        imgUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        ImageList.add(imgUri);
                        currentImageSelect = currentImageSelect + 1;
                    }
                    Toast.makeText(getActivity(), "Selecionou " +ImageList.size()+ " imagens!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "selecione multiplas imagens!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void saveLink(String url){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference ref = fStore.collection(evN).document();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("ImgLink", url);

        ref.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "upload com sucesso!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


    }


    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }

}