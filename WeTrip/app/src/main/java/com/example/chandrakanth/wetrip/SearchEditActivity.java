package com.example.chandrakanth.wetrip;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchEditActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference();
    UserInfo currentUser;
    AdapterFriends fAdapter;
    RecyclerView recyclerPost;
    EditText fname,lname,email,pass,repass,searchItem;
    Button can,sign;
    RadioGroup rg;
    String gender;
    ImageButton imgSearch;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         if(getIntent().getExtras()!=null){
             if(getIntent().getExtras().get("USER_REQUEST").equals("FIND")){
                 setContentView(R.layout.activity_search_edit);
                 currentUser= (UserInfo) getIntent().getExtras().get("Curent");
                 searchItem=(EditText)findViewById(R.id.etSearch);
                 searchItem.addTextChangedListener(new TextWatcher() {
                     @Override
                     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                     }

                     @Override
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         if(charSequence.length()==0){
                             getUsersList();
                         }

                     }

                     @Override
                     public void afterTextChanged(Editable editable) {

                     }
                 });
                 imgSearch=(ImageButton)findViewById(R.id.imgBtnSearch);
                 imgSearch.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         DatabaseReference  userRef=dbRef.child("RegisteredUsers");
                         ValueEventListener postListener = new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 final ArrayList<UserInfo> peopelList=new ArrayList<UserInfo>();
                                 peopelList.clear();
                                 for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                     UserInfo info =  postSnapshot.getValue(UserInfo.class);

                                     if(info!=null){
                                         if(!info.getUserId().equals(currentUser.getUserId())) {
                                             peopelList.add(info);
                                         }
                                     }

                                 }
                                 // filter userlist by checking requests we got if present remove it from list and send it to adapter
                                 DatabaseReference  userRef=dbRef.child("RegisteredUsers").child(currentUser.getUserId()).child("Request_Received");
                                 ValueEventListener postListener = new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                         ArrayList<UserRequest> reqList=new ArrayList<UserRequest>();
                                         reqList.clear();

                                         for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                             UserRequest reqInfo =  postSnapshot.getValue(UserRequest.class);
                                             if(reqInfo!=null){

                                                 reqList.add(reqInfo);

                                             }
                                         }

                                         recyclerPost = (RecyclerView) findViewById(R.id.recyler_post);
                                         recyclerPost.removeAllViews();
                                         ArrayList<UserInfo> searchItemList=compareList(peopelList,reqList);
                                         ArrayList<UserInfo> searched=new ArrayList<UserInfo>();
                                         for(int i=0;i<searchItemList.size();i++){
                                             if(searchItemList.get(i).getEmail().equals(searchItem.getText().toString())){
                                                 searched.add(searchItemList.get(i));
                                             }
                                         }
                                         if(!searched.isEmpty()) {
                                             fAdapter = new AdapterFriends(SearchEditActivity.this,searched, "Users");
                                             recyclerPost.setAdapter(fAdapter);
                                             recyclerPost.setHasFixedSize(true);
                                             recyclerPost.setLayoutManager(new LinearLayoutManager(SearchEditActivity.this, LinearLayoutManager.VERTICAL, false));
                                         }else{
                                             Toast.makeText(SearchEditActivity.this,searchItem.getText().toString()+" doesnot exists",Toast.LENGTH_SHORT).show();
                                         }


                                     }

                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {

                                     }
                                 };


                                 userRef.addValueEventListener(postListener);

                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         };


                         userRef.addValueEventListener(postListener);

                     }
                 });
                 getUsersList();

             }else if(getIntent().getExtras().get("USER_REQUEST").equals("EDIT")){
                 setContentView(R.layout.activity_register);
                 currentUser= (UserInfo) getIntent().getExtras().get("Curent");
                 fname=(EditText)findViewById(R.id.editText_SignupFirstname);
                 lname=(EditText)findViewById(R.id.editText_SignupLastname);
                 email=(EditText)findViewById(R.id.editText_SignupEmail);
                 pass=(EditText)findViewById(R.id.editText_SignupPasswordChoose);
                 repass=(EditText)findViewById(R.id.editText_SignupPasswordRepeat);
                 sign=(Button)findViewById(R.id.button_Signup_SIgnup);
                 can=(Button)findViewById(R.id.button_SignupCancel);
                 rg=(RadioGroup)findViewById(R.id.radioGroup);
                 RadioButton rMale=(RadioButton)findViewById(R.id.radioButton);
                 RadioButton rFeMale=(RadioButton)findViewById(R.id.radioButton2);


                 sign.setText("Update");
                 fname.setText(currentUser.getFname());
                 lname.setText(currentUser.getLname());
                 email.setText(currentUser.getEmail());
                 email.setEnabled(false);
                 if(currentUser.getGender().equals("Male")){
                     rg.check(R.id.radioButton);
                 }else if(currentUser.getGender().equals("Female")){
                     rg.check(R.id.radioButton2);
                 }
                 rMale.setEnabled(false);
                 rFeMale.setEnabled(false);
                 rg.setEnabled(false);
                 pass.setText(currentUser.getPass());

                 can.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         finish();
                     }
                 });
                 sign.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                        String pswrd,repswrd;
                         pswrd=pass.getText().toString();
                         repswrd=repass.getText().toString();
                         if(pswrd.isEmpty()|| repswrd.isEmpty()){
                             Toast.makeText(SearchEditActivity.this,"Password or Repassword cannot be empty",Toast.LENGTH_SHORT).show();
                         }else if (pswrd.equals(repswrd)) {


                             final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                             AuthCredential credential = EmailAuthProvider
                                     .getCredential(currentUser.getEmail(),currentUser.getPass() );

                             user.reauthenticate(credential)
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if (task.isSuccessful()) {
                                                 user.updatePassword(pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task) {
                                                         if (task.isSuccessful()) {
                                                             Toast.makeText(SearchEditActivity.this,"Updated succssfully",Toast.LENGTH_SHORT).show();
                                                         } else {
                                                             Toast.makeText(SearchEditActivity.this,"Updated was 12 not succssful",Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 });
                                             } else {
                                                 Toast.makeText(SearchEditActivity.this,"Updated was not succssful",Toast.LENGTH_SHORT).show();
                                             }
                                         }
                                     });



                             Map<String, Object> hopperUpdates = new HashMap<>();
                             hopperUpdates.put("fname",fname.getText().toString());
                             hopperUpdates.put("lname",lname.getText().toString());
                             hopperUpdates.put("pass",pass.getText().toString());

                             dbRef.child("RegisteredUsers").child(currentUser.getUserId()).updateChildren(hopperUpdates);

                             finish();

                         } else {
                             Toast.makeText(view.getContext(), "Password missmatch", Toast.LENGTH_SHORT).show();
                         }

                     }
                 });


             }
         }



    }



    public void getUsersList(){
        DatabaseReference  userRef=dbRef.child("RegisteredUsers");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<UserInfo> peopelList=new ArrayList<UserInfo>();
                peopelList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserInfo info =  postSnapshot.getValue(UserInfo.class);

                    if(info!=null){
                        if(!info.getUserId().equals(currentUser.getUserId())) {
                            peopelList.add(info);
                        }
                    }

                }
                // filter userlist by checking requests we got if present remove it from list and send it to adapter
                DatabaseReference  userRef=dbRef.child("RegisteredUsers").child(currentUser.getUserId()).child("Request_Received");
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<UserRequest> reqList=new ArrayList<UserRequest>();
                        reqList.clear();

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            UserRequest reqInfo =  postSnapshot.getValue(UserRequest.class);
                            if(reqInfo!=null){

                                reqList.add(reqInfo);

                            }
                        }

                        recyclerPost = (RecyclerView) findViewById(R.id.recyler_post);
                        recyclerPost.removeAllViews();
                        fAdapter = new AdapterFriends(SearchEditActivity.this, compareList(peopelList,reqList),"Users");
                        recyclerPost.setAdapter(fAdapter);
                        recyclerPost.setHasFixedSize(true);
                        recyclerPost.setLayoutManager(new LinearLayoutManager(SearchEditActivity.this,LinearLayoutManager.VERTICAL,false));



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };


                userRef.addValueEventListener(postListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        userRef.addValueEventListener(postListener);
    }

    public  ArrayList<UserInfo> compareList(ArrayList<UserInfo> people,ArrayList<UserRequest> reqs){
        ArrayList<UserInfo> resPeo=new ArrayList<>();
        for(int i=0;i<people.size();i++){
            for(int j=0;j<reqs.size();j++){
                if(people.get(i).getUserId().equals(reqs.get(j).getId())){
                    resPeo.add(people.get(i));
                }
            }
        }
        if(!resPeo.isEmpty()) {
            for (int k = 0; k < resPeo.size(); k++) {
                people.remove(resPeo.get(k));
            }
            return people;
        }else{
            return people;
        }

    }
}
