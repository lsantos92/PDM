package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class visualizarEvento extends AppCompatActivity {
    private String messageReceived;
    TextView teste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_evento);

        String newNome;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newNome= null;
            } else {
                newNome= extras.getString("nome");
            }
        } else {
            newNome= (String) savedInstanceState.getSerializable("nome");
        }
        Toast.makeText(getBaseContext(), newNome,Toast.LENGTH_SHORT).show();
        DataHolder.getInstance().setItem(newNome);





        BottomNavigationView bottomNav;
        bottomNav = findViewById(R.id.bottom_nav2);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch (item.getItemId())
                {
                    case R.id.vEvent: selectedFragment = new eventBriefFragment();
                        break;

                    case R.id.vPhoto: selectedFragment = new imgGalleryFragment();
                        break;

                    case R.id.vVideo: selectedFragment = new videoGalleryFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }
}