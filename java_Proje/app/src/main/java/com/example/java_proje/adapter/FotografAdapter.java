package com.example.java_proje.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.java_proje.R;
import com.example.java_proje.cerceve.GonderiDetayiFragment;
import com.example.java_proje.model.gonderi;

import java.util.List;


public class FotografAdapter extends RecyclerView.Adapter<FotografAdapter.ViewHolder>{

    private Context context;
    private List <gonderi> mGonderiler;


    public FotografAdapter(Context context, List mGonderiler) {
        this.context = context;
        this.mGonderiler = mGonderiler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.fotograflar_ogesi,parent,false);

        return new FotografAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       final gonderi Gonderi = mGonderiler.get(position);

        Glide.with(context).load(Gonderi.getGonderiResmi()).into(holder.gonderi_resmi);
        holder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",Gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGonderiler.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView gonderi_resmi;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gonderi_resmi=itemView.findViewById(R.id.gonderi_resmi_fotograflarOgesi);

        }
    }
}
