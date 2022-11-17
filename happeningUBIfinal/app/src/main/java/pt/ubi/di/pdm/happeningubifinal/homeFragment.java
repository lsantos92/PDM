package pt.ubi.di.pdm.happeningubifinal;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;

public class homeFragment extends Fragment {
    private final int REQUEST_LOCATION_PERMISSION = 1;
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    Button newPost, chooseImg, criarPost, uploadIMG, viewEvent;
    EditText postName, postDescription;
    String postN, postD;
    FirebaseAuth fAuth;
    private StorageReference mStorageRef;
    FirebaseFirestore fStore;
    Button data, hora;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private static String imageURL;
    private static StorageTask mUploadTask;
    private FusedLocationProviderClient fusedLocationClient;

    double latitude, longitude;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
        }

    }

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference("images");

        newPost = view.findViewById(R.id.newPost);
        postName = view.findViewById(R.id.nomeEvento);
        postDescription = view.findViewById(R.id.descricaoEvento);
        chooseImg = view.findViewById(R.id.chooseImage);
        criarPost = view.findViewById(R.id.Criarpost);
        uploadIMG = view.findViewById(R.id.uploadIMG);
        newPost.setVisibility(View.INVISIBLE);
        postName.setVisibility(View.INVISIBLE);
        postDescription.setVisibility(View.INVISIBLE);
        chooseImg.setVisibility(View.INVISIBLE);
        uploadIMG.setVisibility(View.INVISIBLE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        requestLocationPermission();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fStore = FirebaseFirestore.getInstance();
        CollectionReference choice = fStore.collection("posts");

        //setSpinnerChoice(choice);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        } else {
                            Toast.makeText(getActivity(), "impossible to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        criarPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPost.setVisibility(View.VISIBLE);
                postName.setVisibility(View.VISIBLE);
                postDescription.setVisibility(View.VISIBLE);
                chooseImg.setVisibility(View.VISIBLE);
                uploadIMG.setVisibility(View.VISIBLE);



            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }

        });


        uploadIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });


        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
                String getCurrentDateTime = sdf.format(c.getTime());
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                DocumentReference docRef = fStore.collection("posts").document();
                String userID = fAuth.getCurrentUser().getUid();
                postN = postName.getText().toString().trim();
                postD = postDescription.getText().toString().trim();
                Map<Object, String> post = new HashMap<>();
                post.put("nome", postN);
                post.put("descricao", postD);
                post.put("id", userID);
                post.put("data", getCurrentDateTime);
                post.put("imagem", imageURL);
                post.put("latitude", String.valueOf(latitude));
                post.put("longitude", String.valueOf(longitude));

                docRef.set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot written");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }

                        });

                CollectionReference choice = fStore.collection("posts");
                //setSpinnerChoice(choice);
                postName.setText(null);
                postDescription.setText(null);

            }


        });



    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child("images");
            mUploadTask = fileReference.putFile(mImageUri);
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageURL = uri.toString();
                    Toast.makeText(getContext(), imageURL, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(getActivity(), "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }





    }











