package pt.ubi.di.pdm.happeningubifinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class dashboard extends AppCompatActivity {
    Button logOutButton;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView bottomNav;
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch (item.getItemId())
                {
                    case R.id.home: selectedFragment = new homeFragment();
                    break;

                    case R.id.user: selectedFragment = new userFragment();
                        break;

                    case R.id.listEvent: selectedFragment = new listFragment();
                    break;

                    case R.id.exit: selectedFragment = new exitFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, selectedFragment).commit();
                return true;
            }
        });





    }
}