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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class girisyap extends AppCompatActivity {
    EditText email_giris,sifre_giris;
    Button btngirisyap;
    TextView txtkayitsayfasinagit;
    FirebaseAuth girisyetkisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girisyap);

        email_giris=(EditText)findViewById(R.id.editemail_giris);
        sifre_giris=(EditText)findViewById(R.id.editsifre_giris);

        btngirisyap=(Button)findViewById(R.id.btngirisyap);

        txtkayitsayfasinagit=(TextView)findViewById(R.id.txtkayitsayfasi);

        girisyetkisi=FirebaseAuth.getInstance();

        txtkayitsayfasinagit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(girisyap.this,kayit.class));
            }
        });

        btngirisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pdgiris=new ProgressDialog(girisyap.this);
                pdgiris.setMessage("Giris Yapılıyor");
                pdgiris.show();

                String str_email_girisyap,str_sifre_girisyap;
                str_email_girisyap=email_giris.getText().toString();
                str_sifre_girisyap=sifre_giris.getText().toString();
                if(TextUtils.isEmpty(str_email_girisyap)||TextUtils.isEmpty(str_sifre_girisyap))
                {
                    Toast.makeText(girisyap.this, "Bütün alanları doldurunuz!!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    girisyetkisi.signInWithEmailAndPassword(str_email_girisyap,str_sifre_girisyap)
                            .addOnCompleteListener(girisyap.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        DatabaseReference yolgirisyap= FirebaseDatabase.getInstance().getReference().
                                                child("Kullanicilar").child(girisyetkisi.getCurrentUser().getUid());
                                        yolgirisyap.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pdgiris.dismiss();

                                                Intent intent=new Intent(girisyap.this,anasayfa.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                pdgiris.dismiss();


                                            }
                                        });
                                    }
                                    else
                                    {
                                        pdgiris.dismiss();
                                        Toast.makeText(girisyap.this, "Giriş başarısız oldu", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            }
        });
    }
}