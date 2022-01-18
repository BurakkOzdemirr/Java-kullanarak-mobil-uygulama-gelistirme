package com.example.java_proje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.java_proje.cerceve.AramaFragment;
import com.example.java_proje.cerceve.HomeFragment;
import com.example.java_proje.cerceve.ProfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class anasayfa extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment seciliCerceve=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent=getIntent().getExtras();

        if(intent !=null)
        {
            String gonderen =intent.getString("gonderenId");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",gonderen);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,new ProfilFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,new HomeFragment()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.nav_home:
                    seciliCerceve=new HomeFragment();
                    break;
                case R.id.nav_arama:
                    seciliCerceve=new AramaFragment();
                    break;
                case R.id.nav_ekle:
                    seciliCerceve=null;
                    startActivity(new Intent(anasayfa.this,GonderiActivity.class));
                    break;
                case R.id.nav_profil:
                    SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    seciliCerceve=new ProfilFragment();
                    break;

            }

            if(seciliCerceve!=null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,seciliCerceve).commit();
            }

            return true;
        }
    };

}