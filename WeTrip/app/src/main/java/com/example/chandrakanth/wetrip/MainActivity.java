package com.example.chandrakanth.wetrip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText etEmail,etPass;
    CheckBox cb;
    Button sign,login;
    int count;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         count=0;
        ActionBar actionBar=getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        setTitle("Trips");

        etEmail=(EditText)findViewById(R.id.etMainLemail);
        etPass=(EditText)findViewById(R.id.etMainLpassword);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("InitialIntent", "Came1");
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);
                    etEmail.setText("");
                    etPass.setText("");

                }
            }
        };


        etPass.setTransformationMethod(new PasswordTransformationMethod());
        cb=(CheckBox)findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    etPass.setTransformationMethod(new PasswordTransformationMethod());
                }else{
                    etPass.setTransformationMethod(null);
                }
            }
        });

        sign=(Button)findViewById(R.id.btn_Signup);
        login=(Button)findViewById(R.id.btn_Login);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=1;
                progressDialog=new ProgressDialog(view.getContext());
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                if(TextUtils.isEmpty(etEmail.getText().toString())){
                    Toast.makeText(view.getContext(),"Incorrect Email Id",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if(TextUtils.isEmpty(etPass.getText().toString())){
                    Toast.makeText(view.getContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                String email=etEmail.getText().toString();
                final String passwords=etPass.getText().toString();
                mAuth.signInWithEmailAndPassword(email, passwords)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {

                                    Toast.makeText(MainActivity.this,"Something went wrong",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                                else{

                                    Toast.makeText(MainActivity.this, "Login Successful",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("InitialIntent","Came2");
                                    Intent intent = new Intent(MainActivity.this,UserActivity.class);
                                    startActivity(intent);

                                    progressDialog.dismiss();
                                }

                                // ...
                            }
                        });

            }
        });

    }


}
