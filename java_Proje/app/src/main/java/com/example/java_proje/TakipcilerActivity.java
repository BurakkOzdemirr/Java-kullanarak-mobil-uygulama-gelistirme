package com.example.java_proje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.java_proje.adapter.kullaniciAdapter;
import com.example.java_proje.model.kullanici;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TakipcilerActivity extends AppCompatActivity {

    String id;
    String baslik;

    List<String> idListesi;

    RecyclerView recyclerView;
    kullaniciAdapter kullaniciAdap;
    List <kullanici> kullaniciListesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takipciler);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        baslik=intent.getStringExtra("baslik");


        //toolbar ayarları
        Toolbar toolbar=findViewById(R.id.toolbar_takipcilerActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(baslik);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        //reycler view ayarları
        recyclerView=findViewById(R.id.recyler_view_TakipcilerActivity);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        kullaniciListesi=new ArrayList<>();
        kullaniciAdap=new kullaniciAdapter(this,kullaniciListesi,false);
        recyclerView.setAdapter(kullaniciAdap);

        idListesi=new ArrayList<>();

        switch (baslik)
        {
            case "begeniler":
                begenileriAl();
                break;
            case "takip edilenler":
                takipEdilenleriAl();
                break;
            case "takipçiler":
                takipcileriAl();
                break;
        }
    }

    private void begenileriAl() {
        DatabaseReference begeniYolu=FirebaseDatabase.getInstance().getReference("Begeniler")
                .child(id);

        begeniYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idListesi.clear();

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    idListesi.add(snapshot1.getKey());
                }
                kullanicilariGoster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void takipEdilenleriAl() {
        DatabaseReference takipEdilenlerYolu =FirebaseDatabase.getInstance().getReference("Takip")
                .child(id)
                .child("TakipEdilenler");
        takipEdilenlerYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idListesi.clear();

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    idListesi.add(snapshot1.getKey());
                }
                kullanicilariGoster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void takipcileriAl() {

        DatabaseReference takipciYolu= FirebaseDatabase.getInstance().getReference("Takip")
                .child(id)
                .child("takipçiler");
        takipciYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idListesi.clear();

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    idListesi.add(snapshot1.getKey());
                }
                kullanicilariGoster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void  kullanicilariGoster()
    {
        DatabaseReference kullaniciYolu =FirebaseDatabase.getInstance().getReference("Kullanicilar");

        kullaniciYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kullaniciListesi.clear();

                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    kullanici Kullanici=snapshot1.getValue(kullanici.class);
                    for(String id: idListesi)
                    {
                        if(Kullanici.getId().equals(id))
                        {
                            kullaniciListesi.add(Kullanici);
                        }
                    }
                }

                kullaniciAdap.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}