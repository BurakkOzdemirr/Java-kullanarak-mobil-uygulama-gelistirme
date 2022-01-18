package com.example.java_proje;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class baslangic extends AppCompatActivity {

    Button btn_giris_girisyap,btn_giris_kayitol;
    FirebaseUser baslangickullanici;

    @Override
    protected void onStart() {
        super.onStart();

        //Eğer kullanici veritabaninda varsa direk anasayfaya yolla
        baslangickullanici= FirebaseAuth.getInstance().getCurrentUser();

        if(baslangickullanici!=null)
        {
            startActivity(new Intent(baslangic.this,anasayfa.class));
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baslangic);

        btn_giris_girisyap=(Button)findViewById(R.id.btngiris);
        btn_giris_kayitol=(Button)findViewById(R.id.btnkayit);

        btn_giris_girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(baslangic.this,girisyap.class));
            }
        });


        btn_giris_kayitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(baslangic.this,kayit.class));
            }
        });
    }
}