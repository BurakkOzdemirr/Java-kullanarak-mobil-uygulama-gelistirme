package com.example.java_proje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class GonderiActivity extends AppCompatActivity {

    Uri resimUri;
    String myUri = "";

    StorageTask yuklemeGorevi;
    StorageReference resimYukleYolu;

    ImageView image_kapat,image_eklendi;
    TextView txt_gonder;
    EditText edit_sehir,edit_yorum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonderi);

        image_kapat=findViewById(R.id.close_gonderi);
        image_eklendi=findViewById(R.id.eklenen_resim_gonderi);
        txt_gonder=findViewById(R.id.txt_gonder);
        edit_sehir=findViewById(R.id.edit_gonderi_sehir);
        edit_yorum=findViewById(R.id.edit_gonderi_yorum);

        resimYukleYolu= FirebaseStorage.getInstance().getReference("gonderiler");

        image_kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GonderiActivity.this,anasayfa.class));
                finish();
            }
        });

        txt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(GonderiActivity.this);

    }

    private String dosyaUzantisiAl(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void resimYukle() {
        ProgressDialog pd =new ProgressDialog(this);
        pd.setMessage("Paylaşılıyor");
        pd.show();

        if(resimUri != null)
        {
            StorageReference dosyaYolu=resimYukleYolu.child(System.currentTimeMillis()
                    +"."+dosyaUzantisiAl(resimUri));

            yuklemeGorevi = dosyaYolu.putFile(resimUri);

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
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri indirmeUrisi = task.getResult();
                        myUri = indirmeUrisi.toString();

                        DatabaseReference veriYolu= FirebaseDatabase.getInstance().getReference("gonderiler");

                        String gonderiId = veriYolu.push().getKey();

                        HashMap<String,Object> hashMap = new HashMap<>();

                        hashMap.put("gonderiId",gonderiId);
                        hashMap.put("gonderiResmi",myUri);
                        hashMap.put("gonderiSehir",edit_sehir.getText().toString());
                        hashMap.put("gonderiYorum",edit_yorum.getText().toString());
                        hashMap.put("gonderen", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        veriYolu.child(gonderiId).setValue(hashMap);

                        pd.dismiss();

                        startActivity(new Intent(GonderiActivity.this,anasayfa.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(GonderiActivity.this, "Paylaşım başarısız", Toast.LENGTH_SHORT).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GonderiActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Seçilen resim yok", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            resimUri = result.getUri();

            image_eklendi.setImageURI(resimUri);

        }
        else
        {
            Toast.makeText(this, "Resim Seçilemedi", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GonderiActivity.this,anasayfa.class));
            finish();
        }

    }


}