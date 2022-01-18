package com.example.java_proje.cerceve;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.java_proje.R;
import com.example.java_proje.adapter.gonderiAdapter;
import com.example.java_proje.model.gonderi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView ;
    private gonderiAdapter GonderiAdapter;
    private List<gonderi> gonderiListeleri;

    private List<String> takiplistesi;

    ProgressBar progressBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView=v.findViewById(R.id.recyler_view_HomeFragment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        gonderiListeleri=new ArrayList<>();
        GonderiAdapter=new gonderiAdapter(getContext(),gonderiListeleri);
        recyclerView.setAdapter(GonderiAdapter);

        progressBar=v.findViewById(R.id.progress_homeFragment);


        takipKontrolu();
        return v;
    }
    private void takipKontrolu()
    {
        takiplistesi=new ArrayList<>();
        DatabaseReference takipYolu=FirebaseDatabase.getInstance().getReference("Takip")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("TakipEdilenler");

        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                takiplistesi.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    takiplistesi.add(snapshot.getKey());
                }
                gonderileriOku();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gonderileriOku()
    {
        DatabaseReference gonderiYolu= FirebaseDatabase.getInstance().getReference("gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiListeleri.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    gonderi Gonderi=snapshot.getValue(gonderi.class);
                    for(String id: takiplistesi)
                    {
                        if(Gonderi.getGonderen().equals(id))
                        {
                            gonderiListeleri.add(Gonderi);
                        }
                    }
                }
                GonderiAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}