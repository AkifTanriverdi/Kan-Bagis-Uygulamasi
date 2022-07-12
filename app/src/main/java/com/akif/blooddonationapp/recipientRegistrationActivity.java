package com.akif.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class recipientRegistrationActivity extends AppCompatActivity {

    private TextView backButton;;
    private CircleImageView profile_image;
    private TextInputEditText registerFullName,registerEmail,registerPassword;
    private Spinner bloodGroupsSpinner,citiesSpinner;
    private Button registerButton;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_registration);

        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(recipientRegistrationActivity.this,loginActivity.class);
                startActivity(intent);
            }
        });
        profile_image=findViewById(R.id.profile_image);
        registerFullName=findViewById(R.id.registerFullName);
        registerEmail=findViewById(R.id.registerEmail);
        registerPassword=findViewById(R.id.registerPasword);
        bloodGroupsSpinner=findViewById(R.id.bloodGroupsSpinner);
        citiesSpinner=findViewById(R.id.citiesSpinner);
        registerButton=findViewById(R.id.registerButton);
        loader=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email =registerEmail.getText().toString().trim();
                final String password=registerPassword.getText().toString().trim();
                final String fullname=registerFullName.getText().toString().trim();
                final String bloodgroup=bloodGroupsSpinner.getSelectedItem().toString().trim();
                final String citiesgroup=citiesSpinner.getSelectedItem().toString().trim();

                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Email bigisi gereklidir !");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Şifre bigisi gereklidir !");
                    return;
                }
                if(TextUtils.isEmpty(fullname)){
                    registerFullName.setError("Tam adınız gereklidir !");
                    return;
                }
                if(bloodgroup.equals("Kan Grubunuzu Seçiniz")){
                    Toast.makeText(recipientRegistrationActivity.this,"Kan Grubunuzu Seçiniz",Toast.LENGTH_LONG).show();
                    return;
                }
                if(citiesgroup.equals("Bulunduğunuz İli Seçiniz")){
                    Toast.makeText(recipientRegistrationActivity.this," Bulunduğunuz İli Seçiniz",Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    loader.setMessage("Kaydediliyor...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(!task.isSuccessful()){
                                String eror=task.getException().toString();
                                Toast.makeText(recipientRegistrationActivity.this,"Hata !"+eror,Toast.LENGTH_LONG).show();
                            }
                            else{
                                String currentUserId=mAuth.getCurrentUser().getUid();
                                userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
                                HashMap userInfo=new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("name",fullname);
                                userInfo.put("email",email);
                                userInfo.put("password",password);
                                userInfo.put("bloodgroup",bloodgroup);
                                userInfo.put("citiesgroup",citiesgroup);
                                userInfo.put("type","recipient");
                                //güncelleme yapacağın zaman ki 144.satır: userInfo.put("search","donor"+bloodgroup+citiesgroup); olacak
                                userInfo.put("search","recipient"+bloodgroup);
                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(recipientRegistrationActivity.this,"Veri başarıyla kaydedildi",Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Toast.makeText(recipientRegistrationActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                        }
                                        finish();
                                        //    loader.dismiss();
                                    }
                                });
                                if(resultUri!=null){
                                    final StorageReference filePath= FirebaseStorage.getInstance().getReference()
                                            .child("profile images").child(currentUserId);
                                    Bitmap bitmap=null;
                                    try {
                                        bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);

                                    }
                                    catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte[] data=byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask=filePath.putBytes(data);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(recipientRegistrationActivity.this,"Resim dosyası arızalı !",Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if(taskSnapshot.getMetadata() !=null && taskSnapshot.getMetadata().getReference()!=null){
                                                Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUri=uri.toString();
                                                        Map newImageMap=new HashMap();
                                                        newImageMap.put("profilepictureurl",imageUri);
                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(recipientRegistrationActivity.this,"Resim başarıyla eklendi",Toast.LENGTH_LONG).show();
                                                                }
                                                                else{
                                                                    Toast.makeText(recipientRegistrationActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    Intent intent=new Intent(recipientRegistrationActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            resultUri=data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}