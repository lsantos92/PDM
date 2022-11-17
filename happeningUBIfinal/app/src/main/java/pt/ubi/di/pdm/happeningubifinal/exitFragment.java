package pt.ubi.di.pdm.happeningubifinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class exitFragment extends Fragment {
    FirebaseAuth fAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent newIntent;
        newIntent = new Intent(getContext(),login.class);
        startActivity(newIntent);
        View view = inflater.inflate(R.layout.fragment_exit,container,false);


        return view;
    }
}
