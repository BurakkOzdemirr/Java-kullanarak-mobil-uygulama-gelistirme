package com.example.java_proje.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.java_proje.R;
import com.example.java_proje.anasayfa;
import com.example.java_proje.cerceve.ProfilFragment;
import com.example.java_proje.model.kullanici;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class kullaniciAdapter extends RecyclerView.Adapter<kullaniciAdapter.ViewHolder> {

    private Context mContext;
    private List<kullanici> mKullanicilar;
    private boolean isfragment;
    private FirebaseUser firebaseUserKullanici;

    public kullaniciAdapter(Context mContext, List<kullanici> mKullanicilar, boolean isfragment) {
        this.mContext = mContext;
        this.mKullanicilar = mKullanicilar;
        this.isfragment=isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.kullanici_ogesi,parent,false);

        return new kullaniciAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUserKullanici= FirebaseAuth.getInstance().getCurrentUser();

        final kullanici kullanici= mKullanicilar.get(position);

        holder.kbtntakipet.setVisibility(View.VISIBLE);

        holder.kullaniciadi.setText(kullanici.getKullaniciadi());

        holder.ad.setText(kullanici.getAdi());
        Glide.with(mContext).load(kullanici.getResimurl()).into(holder.profilresmi);
        takipEdiliyor(kullanici.getId(),holder.kbtntakipet);

        if(kullanici.getId().equals(firebaseUserKullanici.getUid()))
        {
            holder.kbtntakipet.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isfragment)
                {

                    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",kullanici.getId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.cerceve_kapsayici,new ProfilFragment()).commit();
                }

                else
                {
                    Intent intent=new Intent(mContext, anasayfa.class);
                    intent.putExtra("gonderenId",kullanici.getId());
                    mContext.startActivity(intent);
                }

            }
        });

        holder.kbtntakipet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.kbtntakipet.getText().toString().equals("Takip Et"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip")
                            .child(firebaseUserKullanici.getUid()).child("TakipEdilenler").child(kullanici.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Takip")
                            .child(kullanici.getId()).child("takipçiler").child(firebaseUserKullanici.getUid()).setValue(true);

                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip")
                            .child(firebaseUserKullanici.getUid()).child("TakipEdilenler").child(kullanici.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Takip")
                            .child(kullanici.getId()).child("takipçiler").child(firebaseUserKullanici.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return mKullanicilar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView kullaniciadi;
        public TextView ad;
        public CircleImageView profilresmi;
        public Button kbtntakipet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciadi=itemView.findViewById(R.id.txt_kulaniciadi_oge);
            ad=itemView.findViewById(R.id.txt_ad_oge);
            profilresmi=itemView.findViewById(R.id.profil_resmi_oge);
            kbtntakipet=itemView.findViewById(R.id.btn_takipet_oge);

        }
    }

    private void takipEdiliyor(String kullaniciId,Button button)
    {
        DatabaseReference takipyolu= FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(firebaseUserKullanici.getUid()).child("TakipEdilenler");
        takipyolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(kullaniciId).exists())
                {
                    button.setText("Takip Ediliyor");
                }
                else
                {
                    button.setText("Takip Et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
