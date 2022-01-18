package com.example.java_proje.cerceve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.java_proje.ProfilDuzenleActivity;
import com.example.java_proje.R;
import com.example.java_proje.SeceneklerActivity;
import com.example.java_proje.TakipcilerActivity;
import com.example.java_proje.adapter.FotografAdapter;
import com.example.java_proje.model.gonderi;
import com.example.java_proje.model.kullanici;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends Fragment {

    ImageView resimSecenekler, profil_resmi;
    TextView txt_gonderiler,txt_takipciler,txt_takipEdilenler,txt_ad,txt_bio,txt_kullaniciadi;
    Button btn_profili_duzenle;

    FirebaseUser mevcutKullanici;
    String profilId;

    RecyclerView recyclerViewFotograflar;
    FotografAdapter fotografAdapter;
    List<gonderi> gonderiList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs=getContext().getSharedPreferences( "PREFS", Context.MODE_PRIVATE);
        profilId=prefs.getString("profileid","none");

        resimSecenekler=view.findViewById(R.id.resimSecenekler_profilCercevesi);
        profil_resmi=view.findViewById(R.id.profil_resmi_profilCerceve);

        txt_gonderiler=view.findViewById(R.id.txt_gonderiler_profilCercevesi);
        txt_takipciler=view.findViewById(R.id.txt_takipciler_profilCercevesi);
        txt_takipEdilenler=view.findViewById(R.id.txt_takipEdilenler_profilCercevesi);
        txt_bio=view.findViewById(R.id.txt_bio_profilCercevesi);
        txt_ad=view.findViewById(R.id.txt_ad_profilCercevesi);
        txt_kullaniciadi=view.findViewById(R.id.txt_kullaniciadi_profilcerceve);

        btn_profili_duzenle=view.findViewById(R.id.btn_profiliDuzenle_profilCercevesi);

        recyclerViewFotograflar=view.findViewById(R.id.recyler_view_profilCercevesi);
        recyclerViewFotograflar.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new GridLayoutManager(getContext(),3);// fotğraflar yan yana 3 sıra olsun
        recyclerViewFotograflar.setLayoutManager(linearLayoutManager);
        gonderiList=new ArrayList<>();
        fotografAdapter= new FotografAdapter(getContext(),gonderiList);
        recyclerViewFotograflar.setAdapter(fotografAdapter);

        //methodları çağır
        kullaniciBilgisi();
        takipcileriAl();
        gonderiSayisiAl();
        fotograflarim();

        if(profilId.equals(mevcutKullanici.getUid()))
        {
            btn_profili_duzenle.setText("Profili Düzenle");
        }
        else
        {
            takipKontrolu();
        }


        btn_profili_duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btn_profili_duzenle.getText().toString();

                if(btn.equals("Profili Düzenle"))
                {
                    startActivity(new Intent(getContext(), ProfilDuzenleActivity.class));
                }

                else if (btn.equals("Takip Et"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip")
                            .child(mevcutKullanici.getUid())
                            .child("TakipEdilenler")
                            .child(profilId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipçiler").child(mevcutKullanici.getUid()).setValue(true);
                }
                else if(btn.equals("Takip Ediliyor"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip")
                            .child(mevcutKullanici.getUid())
                            .child("TakipEdilenler")
                            .child(profilId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipçiler").child(mevcutKullanici.getUid()).removeValue();
                }
            }
        });
        //Seçenekler sayfasına git
        resimSecenekler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SeceneklerActivity.class);
                startActivity(intent);
            }
        });

        txt_takipciler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), TakipcilerActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takipçiler");
                startActivity(intent);
            }
        });

        txt_takipEdilenler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),TakipcilerActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takip edilenler");
                startActivity(intent);
            }
        });

        return view;
    }
    private void kullaniciBilgisi()
    {
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(profilId);

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(getContext() == null)
                {
                    return;
                }

                kullanici Kullanici =snapshot.getValue(kullanici.class);

                Glide.with(getContext()).load(Kullanici.getResimurl()).into(profil_resmi);
                txt_kullaniciadi.setText(Kullanici.getKullaniciadi());
                txt_ad.setText(Kullanici.getAdi());
                txt_bio.setText(Kullanici.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void takipKontrolu()
    {
        DatabaseReference takipYolu=FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(mevcutKullanici.getUid())
                .child("TakipEdilenler");

        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(profilId).exists())
                {
                    btn_profili_duzenle.setText("Takip Ediliyor");
                }
                else
                {
                    btn_profili_duzenle.setText("Takip Et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void takipcileriAl()
    {
        //Takipçi sayısını alır
        DatabaseReference takipciYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profilId)
                .child("takipçiler");

        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                txt_takipciler.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Takip edilen sayısını alır
        DatabaseReference takipEdilenYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profilId)
                .child("TakipEdilenler");

        takipEdilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                txt_takipEdilenler.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gonderiSayisiAl()
    {
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference().child("gonderiler");

        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i =0;
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    gonderi Gonderi = snapshot1.getValue(gonderi.class);
                    if(Gonderi.getGonderen().equals(profilId))
                    {
                        i++;
                    }
                }

                txt_gonderiler.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fotograflarim()
    {
        DatabaseReference fotografYolu=FirebaseDatabase.getInstance().getReference("gonderiler");

        fotografYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gonderiList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    gonderi Gonderi = snapshot1.getValue(gonderi.class);
                    if(Gonderi.getGonderen().equals(profilId))
                    {
                        gonderiList.add(Gonderi);
                    }
                }
                Collections.reverse(gonderiList);
                fotografAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}