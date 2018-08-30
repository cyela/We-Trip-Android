package com.example.chandrakanth.wetrip;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText fname,lname,email,pass,repass;
    Button can,sign;
    RadioGroup rg;
    String gender;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        fname=(EditText)findViewById(R.id.editText_SignupFirstname);
        lname=(EditText)findViewById(R.id.editText_SignupLastname);
        email=(EditText)findViewById(R.id.editText_SignupEmail);
        pass=(EditText)findViewById(R.id.editText_SignupPasswordChoose);
        repass=(EditText)findViewById(R.id.editText_SignupPasswordRepeat);
        sign=(Button)findViewById(R.id.button_Signup_SIgnup);
        can=(Button)findViewById(R.id.button_SignupCancel);
        rg=(RadioGroup)findViewById(R.id.radioGroup);
        gender="Male";
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.radioButton){
                    gender="Male";
                }else if(i==R.id.radioButton2){
                    gender="Female";
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(view.getContext());
                progressDialog.setMessage("Registering...");
                progressDialog.show();
                if(TextUtils.isEmpty(fname.getText())&& TextUtils.isEmpty(lname.getText())&& TextUtils.isEmpty(email.getText())
                        && TextUtils.isEmpty(pass.getText())&& TextUtils.isEmpty(repass.getText())){
                    Toast.makeText(view.getContext(),"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }else if(isEmailValid(email.getText().toString())){
                    String Pass=pass.getText().toString();
                    String Repass=repass.getText().toString();
                    if (Pass.equals(Repass)) {
                        UserInfo user=new UserInfo();
                        user.setEmail(email.getText().toString());
                        user.setFname(fname.getText().toString());
                        user.setGender(gender);
                        user.setLname(lname.getText().toString());
                        user.setPass(pass.getText().toString());
                        SignUP(user);
                    } else {
                        Toast.makeText(view.getContext(), "Password missmatch", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(view.getContext(),"Please enter valid email",Toast.LENGTH_SHORT).show();
                }
            }
        });
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }});


    }
    public void SignUP(final UserInfo user){

        DatabaseReference newRef = DBRef.child("RegisteredUsers");
        final DatabaseReference childref=newRef.push();
        user.setUserId(childref.getKey());
        user.setImageUrl("null");
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPass())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            childref.setValue(user);
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                            email.setText("");
                            pass.setText("");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fname.getText().toString() + " " + lname.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseAuth.getInstance().signOut();
                                                finish();
                                            }
                                        }
                                    });
                            progressDialog.dismiss();
                        }
                        else {


                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });






    }
    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
