package com.example.java_proje.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.java_proje.R;
import com.example.java_proje.TakipcilerActivity;
import com.example.java_proje.cerceve.GonderiDetayiFragment;
import com.example.java_proje.cerceve.ProfilFragment;
import com.example.java_proje.model.gonderi;
import com.example.java_proje.model.kullanici;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class gonderiAdapter extends RecyclerView.Adapter<gonderiAdapter.ViewHolder> {

    public Context mContext ;
    public List<gonderi> mGonderi;

    private FirebaseUser mevcutFirebaseUser;

    public gonderiAdapter(Context mContext, List<gonderi> mGonderi) {
        this.mContext = mContext;
        this.mGonderi = mGonderi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.gonderi_ogesi,viewGroup,false);

        return new gonderiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        mevcutFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        gonderi Gonderi=mGonderi.get(i);

        Glide.with(mContext).load(Gonderi.getGonderiResmi()).into(holder.gonderi_resmi);
        holder.txt_sehir.setText(Gonderi.getGonderiSehir());

        if(Gonderi.getGonderiYorum().equals(" "))
        {
            holder.txt_gonderi_hakkinda.setVisibility(View.GONE);
        }
        else
        {
            holder.txt_gonderi_hakkinda.setVisibility(View.VISIBLE);
            holder.txt_gonderi_hakkinda.setText(Gonderi.getGonderiYorum());
        }


        gonderenBilgileri(holder.profil_resmi,holder.txt_kullanici_adi,holder.txt_gonderen,Gonderi.getGonderen());
        begenildi(Gonderi.getGonderiId(),holder.begeni);
        begeniSayisi(holder.txt_begeni,Gonderi.getGonderiId());

        //profil resmine tıklama olayı
        holder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",Gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
            }
        });
        // txt kullanıcı adına tıkladığında profile git
        holder.txt_kullanici_adi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",Gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
            }
        });
        //gonderene tıkladığında yollasın profile
        holder.txt_gonderen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",Gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();
            }
        });
        //gönderi resmine tıklandığında napsın
        holder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",Gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()).commit();
            }
        });

        holder.begeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.begeni.getTag().equals("Beğen"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(Gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(Gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).removeValue();
                }
            }
        });
        //beğeniler txt sine tıkladığında yapacakalrı
        holder.txt_begeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, TakipcilerActivity.class);
                intent.putExtra("id",Gonderi.getGonderiId());
                intent.putExtra("baslik","begeniler");
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGonderi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView profil_resmi,gonderi_resmi,begeni;
        public TextView txt_kullanici_adi,txt_begeni,txt_gonderi_hakkinda,txt_sehir,txt_gonderen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profil_resmi=itemView.findViewById(R.id.profil_resmi_gonderi_ogesi);
            gonderi_resmi=itemView.findViewById(R.id.gonderi_resmi_gonderi_ogesi);
            begeni=itemView.findViewById(R.id.begeni_gonderi_ogesi);

            txt_kullanici_adi=itemView.findViewById(R.id.txt_kullaniciadi_gonderi_ogesi);
            txt_begeni=itemView.findViewById(R.id.txt_begeniler_gonderi_ogesi);
            txt_gonderi_hakkinda=itemView.findViewById(R.id.txt_gonderiYorum_gonderi_ogesi);
            txt_sehir=itemView.findViewById(R.id.txt_sehir_gonderi_ogesi);
            txt_gonderen=itemView.findViewById(R.id.txt_gonderen_gonderi_ogesi);



        }
    }
    private void begenildi(String gonderiId,final ImageView imageview)
    {
        FirebaseUser mevcutKullanici=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference begeniVeriTabaniYolu=FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);

        begeniVeriTabaniYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(mevcutKullanici.getUid()).exists())
                {
                    imageview.setImageResource(R.drawable.ic_begenildi);
                    imageview.setTag("Beğenildi");
                }
                else
                {
                    imageview.setImageResource(R.drawable.ic_begeni);
                    imageview.setTag("Beğen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void begeniSayisi(final TextView begeniler,String gonderiId)
    {
        DatabaseReference begeniSayisiVeriTabaniYolu=FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);

        begeniSayisiVeriTabaniYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                begeniler.setText(snapshot.getChildrenCount()+" beğeni");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gonderenBilgileri(final ImageView profil_resmi,final TextView kullanici_adi,final TextView gonderen,String kullaniciId)
    {
        DatabaseReference veriYolu= FirebaseDatabase.getInstance().getReference("Kullanicilar").child(kullaniciId);

        veriYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kullanici Kullanici = snapshot.getValue(kullanici.class);

                Glide.with(mContext).load(Kullanici.getResimurl()).into(profil_resmi);
                kullanici_adi.setText(Kullanici.getKullaniciadi());
                gonderen.setText(Kullanici.getKullaniciadi());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
