package com.example.java_proje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.java_proje.cerceve.ProfilFragment;
import com.example.java_proje.model.kullanici;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfilDuzenleActivity extends AppCompatActivity {
    ImageView resim_kapatma,resim_profil;
    TextView txt_kaydet,txt_fotograf_degistir;
    MaterialEditText mEdit_ad,mEdit_kullaniciadi,mEdit_biyografi;

    FirebaseUser mevcutKullanici;
    private StorageTask yuklemeGorevi;
    private Uri mResimUri;
    StorageReference depolamaYolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        resim_kapatma=findViewById(R.id.kapat_resmi_profilDuzenleActivity);
        resim_profil=findViewById(R.id.profil_resmi_profilDuzenleActivity);

        txt_kaydet=findViewById(R.id.txt_kaydet_profilDuzenleActivity);
        txt_fotograf_degistir=findViewById(R.id.txt_degistir);

        mEdit_ad=findViewById(R.id.material_edt_txt_ad_profilDuzenleActivity);
        mEdit_biyografi=findViewById(R.id.material_edt_txt_biyografi_profilDuzenleActivity);
        mEdit_kullaniciadi=findViewById(R.id.material_edt_txt_kullaniciadi_profilDuzenleActivity);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        depolamaYolu= FirebaseStorage.getInstance().getReference("yuklemeler");

        DatabaseReference kullaniciYolu= FirebaseDatabase.getInstance().getReference("Kullanicilar")
                .child(mevcutKullanici.getUid());

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kullanici Kullanici = snapshot.getValue(kullanici.class);

                mEdit_ad.setText(Kullanici.getAdi());
                mEdit_kullaniciadi.setText(Kullanici.getKullaniciadi());
                mEdit_biyografi.setText(Kullanici.getBio());
                Glide.with(getApplicationContext()).load(Kullanici.getResimurl()).into(resim_profil);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        resim_kapatma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_fotograf_degistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        .start(ProfilDuzenleActivity.this);
            }
        });

        resim_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfilDuzenleActivity.this);
            }
        });
        
        txt_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiliGuncelle(mEdit_ad.getText().toString(),mEdit_kullaniciadi.getText().toString(),mEdit_biyografi.getText().toString());
            }
        });

    }

    private void profiliGuncelle(String ad, String kullaniciadi, String biyografi) {

        DatabaseReference guncellemeYolu=FirebaseDatabase.getInstance().getReference("Kullanicilar")
                .child(mevcutKullanici.getUid());

        HashMap<String,Object> kullaniciGuncelleHashMap=new HashMap<>();
        kullaniciGuncelleHashMap.put("adi",ad);
        kullaniciGuncelleHashMap.put("kullaniciadi",kullaniciadi);
        kullaniciGuncelleHashMap.put("bio",biyografi);

        guncellemeYolu.updateChildren(kullaniciGuncelleHashMap);


        


    }

    private String dosyaUzantisiAl(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void resimYukle()
    {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Yükleniyor..");
        pd.show();

        if(mResimUri !=null)
        {
            StorageReference dosyaYolu = depolamaYolu.child(System.currentTimeMillis()
                    +"."+dosyaUzantisiAl(mResimUri));

            yuklemeGorevi =dosyaYolu.putFile(mResimUri);
            yuklemeGorevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return dosyaYolu.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri indirmeUrisi = task.getResult();
                        String benimUrim= indirmeUrisi.toString();

                        DatabaseReference kullaniciYolu=FirebaseDatabase.getInstance().getReference("Kullanicilar")
                                .child(mevcutKullanici.getUid());

                        HashMap<String,Object> resimHashMap = new HashMap<>();
                        resimHashMap.put("resimurl",""+benimUrim);

                        kullaniciYolu.updateChildren(resimHashMap);
                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(ProfilDuzenleActivity.this, "Yükleme Başarısız", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilDuzenleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Resim Seçilemedi!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mResimUri = result.getUri();
            
            resimYukle();
        }
        else
        {
            Toast.makeText(this, "Birşeyler yanlış gitti", Toast.LENGTH_SHORT).show();
        }
        
    }
}