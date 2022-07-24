package com.example.collegeconnect.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    TextView alreadyHaveanAccount;
    ImageView ProfileImage;
    EditText inputPersonName, inputRegisterEmail, inputRegisterPassword, inputConfirmPassword,settings_status;
    Button btnRegister;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseStorage storage;
    FirebaseDatabase database;
    String imageURI;
    //ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Please Wait....");
        //progressDialog.setCancelable(false);


        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();

        alreadyHaveanAccount = findViewById(R.id.alreadyHaveanAccount);
        ProfileImage = findViewById(R.id.ProfileImage);
        inputPersonName = findViewById(R.id.inputPersonName);
        inputRegisterEmail = findViewById(R.id.inputRegisterEmail);
        inputRegisterPassword = findViewById(R.id.inputRegisterPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        settings_status = findViewById(R.id.setting_status);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progressDialog.show();

                String name = inputPersonName.getText().toString();
                String email = inputRegisterEmail.getText().toString();
                String password = inputRegisterPassword.getText().toString();
                String ConfirmPassword = inputConfirmPassword.getText().toString();
                String status = settings_status.getText().toString();




                if (TextUtils.isEmpty(email)) {
                    inputRegisterEmail.setError("Enter Correct Email");
                    inputRegisterEmail.requestFocus();
                    // progressDialog.dismiss();
                } else if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(ConfirmPassword)) {
                    //progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(email)) {
                    inputRegisterEmail.setError("Please Enter Valid Email");
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password At Least 6 Character", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                } else if (!password.equals(ConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                } else if (TextUtils.isEmpty(status)) {
                    Toast.makeText(RegisterActivity.this, "Please Specify Whether You Are Student Or Faculty", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                }
                else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                FirebaseUser fuser = auth.getCurrentUser();
                                assert fuser != null;
                                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Verification Email has been Sent" , Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                    }
                                });




                                DatabaseReference reference=database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());
                                Toast.makeText(RegisterActivity.this,"User Created Successfully",Toast.LENGTH_SHORT).show();

                                if(imageUri!=null)
                                {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        {
                                                            imageURI = uri.toString();
                                                            Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        //progressDialog.dismiss();
                                                                        startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                                                        finish();
                                                                    }else
                                                                    {
                                                                        Toast.makeText(RegisterActivity.this, "Error In Creating User", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }


                                                });
                                            }
                                        }
                                    });
                                }else
                                {

                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/college-connect-9d15d.appspot.com/o/profile.png?alt=media&token=eb8dcc2e-34e4-4f6e-ba5d-aedb33c637f5";
                                    Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                                finish();
                                            }else
                                            {
                                                Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                //progressDialog.dismiss();

                                Toast.makeText(RegisterActivity.this, "Something Went Wrong" , Toast.LENGTH_SHORT).show();


                            }
                        }


                    });


                }



            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });


        alreadyHaveanAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.getData();
                ProfileImage.setImageURI(imageUri);
            }
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

