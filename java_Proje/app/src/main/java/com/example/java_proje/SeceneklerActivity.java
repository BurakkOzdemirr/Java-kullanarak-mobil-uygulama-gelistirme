package com.example.java_proje;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;

public class SeceneklerActivity extends AppCompatActivity {
    TextView txt_cikisYap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secenekler);


        txt_cikisYap=findViewById(R.id.txt_cikisyap_seceneklerActivity);

        //Toolbar ayarları
        Toolbar poolbar=findViewById(R.id.toolbar_seceneklerActivity);
        setSupportActionBar(poolbar);
        setSupportActionBar(poolbar);
        getSupportActionBar().setTitle("Seçenekler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        poolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_cikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SeceneklerActivity.this,baslangic.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }
}