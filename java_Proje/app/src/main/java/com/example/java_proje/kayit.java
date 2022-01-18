package com.example.java_proje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class kayit extends AppCompatActivity {
    EditText ka,adi,email,sifre;
    Button btnkaydol;
    TextView girisegit;
    FirebaseAuth yetki;
    DatabaseReference yol;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);

        ka=(EditText)findViewById(R.id.editka);
        adi=(EditText)findViewById(R.id.editad);
        email=(EditText)findViewById(R.id.editemail);
        sifre=(EditText)findViewById(R.id.editsifre);

        btnkaydol=(Button)findViewById(R.id.btnkaydol);

        girisegit=(TextView)findViewById(R.id.txtgirisyapsayfasi);

        yetki=FirebaseAuth.getInstance();

        girisegit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(kayit.this,girisyap.class));
            }
        });

        btnkaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(kayit.this);
                pd.setMessage("Lütfen Bekleyin");
                pd.show();

                String str_ka,str_ad,str_email,str_sifre;
                str_ka=ka.getText().toString();
                str_ad=adi.getText().toString();
                str_email=email.getText().toString();
                str_sifre=sifre.getText().toString();
                if(TextUtils.isEmpty(str_ka)||TextUtils.isEmpty(str_ad)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_sifre))
                {
                    Toast.makeText(kayit.this, "Lütfen bütün alanları doldurun", Toast.LENGTH_SHORT).show();
                }
                else if(str_sifre.length()<6)
                {
                    Toast.makeText(kayit.this, "Şifreniz minimum 6 karakter olmalı", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    kaydet(str_ka,str_ad,str_email,str_sifre);
                }
            }
        });


    }
    private void kaydet(String kullaniciadi,String adi,String email,String sifre)
    {
        yetki.createUserWithEmailAndPassword(email,sifre)
                .addOnCompleteListener(kayit.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseKullanici=yetki.getCurrentUser();
                            String kullaniciID=firebaseKullanici.getUid();
                            yol= FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(kullaniciID);
                            HashMap<String,Object> hashmap=new HashMap<>();

                            hashmap.put("id",kullaniciID);
                            hashmap.put("adi",adi);
                            hashmap.put("kullaniciadi",kullaniciadi.toLowerCase());
                            hashmap.put("bio","");
                            hashmap.put("resimurl","https://firebasestorage.googleapis.com/v0/b/java-proje-af29c.appspot.com/o/placeholder.jpg?alt=media&token=c865ae9f-4f2a-4f70-a220-f294325157f6");



                            yol.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        pd.dismiss();
                                        Intent intent=new Intent(kayit.this,anasayfa.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                }
                            });
                        }
                        else
                        {
                            pd.dismiss();
                            Toast.makeText(kayit.this, "Bu mail veya şifre ile kayıt başarısız!!", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }
}