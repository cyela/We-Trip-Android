package com.example.chandrakanth.wetrip;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    private ImageButton sigout,edit,upload;
    CircleImageView profPic;
    TextView userName;
    EditText postMsg;
    ProgressDialog progressDialog;
    private Boolean exit = false;
    int USER_PROFPIC=10001;
    int USER_POSTPIC=10000;String url;
    StorageReference storageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user;
    UserInfo currentUser;
    RecyclerView recyclerPost;
    AdapterPosts pAdapter;AdapterTrip tAdapter;AdapterFriends fAdapter;
    int search=0,reqst=0;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (exit) {
            finish(); // finish activity
        }
        else {

            exit = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
            }, 1000);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuuser, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.findFriends:
                Intent intent=new Intent(UserActivity.this,SearchEditActivity.class);
                intent.putExtra("USER_REQUEST","FIND");
                intent.putExtra("Curent",currentUser);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    progressDialog=new ProgressDialog(UserActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    setRecylcer("Home");
                    return true;
                case R.id.navigation_dashboard:
                    setRecylcer("Trips");
                    return true;
                case R.id.navigation_notifications:
                    setRecylcer("Requests");
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        setTitle("Welcome");
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

            FirebaseAuth.getInstance().signOut();
            finish();
        }
        edit=(ImageButton)findViewById(R.id.imgBtnEditProf);
        upload=(ImageButton)findViewById(R.id.imgPost);
        sigout=(ImageButton)findViewById(R.id.imgBtnSignOut);
        userName=(TextView)findViewById(R.id.tvUserTitle);
        postMsg=(EditText)findViewById(R.id.desPost);
        profPic=(CircleImageView)findViewById(R.id.circleUser);
        DatabaseReference  userRef=dbRef.child("RegisteredUsers");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=new UserInfo();
                currentUser=null;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserInfo info =  postSnapshot.getValue(UserInfo.class);
                    if(user.getEmail().equals(info.getEmail())){

                        currentUser=info;
                    }

                }
                if(!currentUser.getImageUrl().equals("null")) {
                    Picasso.with(UserActivity.this).load(currentUser.getImageUrl())
                            .into(profPic);
                }else{

                    profPic.setImageResource(R.drawable.noimage);
                }
                userName.setText(currentUser.getLname()+" "+currentUser.getFname());
                userName.setTextColor(Color.BLACK);
                progressDialog=new ProgressDialog(UserActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                setRecylcer("Home");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addValueEventListener(postListener);



        sigout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserActivity.this);
                alertDialog.setMessage("Do you want to signout");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                finish();
                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(USER_PROFPIC);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserActivity.this,SearchEditActivity.class);
                intent.putExtra("USER_REQUEST","EDIT");
                intent.putExtra("Curent",currentUser);
                startActivity(intent);
            }
        });

    }
    public void setRecylcer(String action){
        if(action.equals("Home")){
        // recycler for post data
            postMsg.setEnabled(true);
            postMsg.setText("");
            upload.setImageResource(R.drawable.upload);
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGallery(USER_POSTPIC);
                }
            });

            DatabaseReference  userRef=dbRef.child("RegisteredUsers").child(currentUser.getUserId()).child("Posts");
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<UserPosts> postList=new ArrayList<UserPosts>();
                    postList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        UserPosts info =  postSnapshot.getValue(UserPosts.class);
                        if(info!=null){
                            postList.add(info);
                        }

                    }
                    upload.setVisibility(View.VISIBLE);

                        recyclerPost = (RecyclerView) findViewById(R.id.recyler_post);
                        recyclerPost.removeAllViews();
                        pAdapter = new AdapterPosts(UserActivity.this, postList);
                        recyclerPost.setAdapter(pAdapter);
                        recyclerPost.setHasFixedSize(true);
                        recyclerPost.setLayoutManager(new LinearLayoutManager(UserActivity.this,LinearLayoutManager.VERTICAL,false));
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            userRef.addValueEventListener(postListener);



        }else if(action.equals("Trips")){
        // recycler for trips data
            postMsg.setEnabled(false);
            postMsg.setText("Click button to create Trip");
            postMsg.setTextColor(Color.BLACK);
            upload.setImageResource(R.drawable.addtrip);
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserActivity.this);
                    alertDialog.setTitle("Add Trip");
                    alertDialog.setMessage("Please enter Trip name");

                    final EditText input = new EditText(UserActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setPositiveButton("Add",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!input.getText().toString().isEmpty()) {
                                        UserTrip uTrip = new UserTrip();

                                        uTrip.setTripName(input.getText().toString());

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                        Date date = new Date();
                                        String datetime= sdf.format(date);
                                        uTrip.setCreatDate(datetime);

                                        DatabaseReference newRef=dbRef.child("Trips");
                                        final DatabaseReference childref=newRef.push();
                                        uTrip.setTripId(childref.getKey());
                                        uTrip.setTripPic("");
                                        uTrip.setTripBy(currentUser.getUserId());
                                        childref.setValue(uTrip);


                                    }

                                }
                            });

                    alertDialog.setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                }

            });

            DatabaseReference  userRef=dbRef.child("Trips");
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayList<UserTrip> tripList=new ArrayList<UserTrip>();
                    tripList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        final UserTrip infoTrip =  postSnapshot.getValue(UserTrip.class);
                        if(infoTrip!=null){
                            if(infoTrip.getTripBy().equals(currentUser.getUserId())) {
                                tripList.add(infoTrip);
                            }
                        }
                        final DatabaseReference  userRefMem=dbRef.child("Trips").child(infoTrip.getTripId()).child("Members");
                        final ValueEventListener postListenerMem =new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    UserRequest infoMem =  postSnapshot.getValue(UserRequest.class);
                                    if(infoMem.getId().equals(currentUser.getUserId())){
                                        tripList.add(infoTrip);
                                    }

                                }
                                upload.setVisibility(View.VISIBLE);
                                recyclerPost = (RecyclerView) findViewById(R.id.recyler_post);
                                recyclerPost.removeAllViews();
                                tAdapter = new AdapterTrip(UserActivity.this, tripList);
                                recyclerPost.setAdapter(tAdapter);
                                recyclerPost.setHasFixedSize(true);
                                recyclerPost.setLayoutManager(new LinearLayoutManager(UserActivity.this,LinearLayoutManager.VERTICAL,false));



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };

                        userRefMem.addValueEventListener(postListenerMem);


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            userRef.addValueEventListener(postListener);
       }
        else if(action.equals("Requests")){
        // recycler for friends data
            getRequestList();

        }
    }

    public void getRequestList(){
        DatabaseReference  userRef=dbRef.child("RegisteredUsers").child(currentUser.getUserId()).child("Request_Received");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<UserRequest> reqList=new ArrayList<UserRequest>();
                reqList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserRequest req =  postSnapshot.getValue(UserRequest.class);

                    if(req!=null){
                        reqList.add(req);
                    }

                }

                    DatabaseReference userRef = dbRef.child("RegisteredUsers");
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<UserInfo> fltrUserList = new ArrayList<UserInfo>();
                            fltrUserList.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                UserInfo info = postSnapshot.getValue(UserInfo.class);
                                for (int i = 0; i < reqList.size(); i++) {
                                    if (reqList.get(i).getId().equals(info.getUserId())) {
                                        fltrUserList.add(info);
                                    }
                                }

                            }
                                postMsg.setEnabled(false);
                            if(!fltrUserList.isEmpty()) {
                                postMsg.setText("Your Requests");
                            }else{
                                postMsg.setText("you have no requests");
                            }
                                postMsg.setTextColor(Color.BLACK);
                                upload.setVisibility(View.INVISIBLE);

                                recyclerPost = (RecyclerView) findViewById(R.id.recyler_post);
                                recyclerPost.removeAllViews();
                                fAdapter = new AdapterFriends(UserActivity.this, fltrUserList, "Request");
                                recyclerPost.setAdapter(fAdapter);
                                recyclerPost.setHasFixedSize(true);
                                recyclerPost.setLayoutManager(new LinearLayoutManager(UserActivity.this, LinearLayoutManager.VERTICAL, false));


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
    public void openGallery(int code) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), code);
    }
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        final StorageReference sreference = storage.getReference();
        final String path = "images/ProfPic/" + user.getEmail() + ".png";
        final String path1 = "images/PostPic/" + UUID.randomUUID()+user.getEmail() + ".png";


        Bitmap bitmap = null;
        Bitmap bitmap1=null;
        if (resultCode == RESULT_OK) {
            if (requestCode == USER_PROFPIC) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(requestCode==USER_POSTPIC){
                Uri imageUri = data.getData();
                try {
                    bitmap1= BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(imageUri));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }

        }
        if(requestCode==USER_PROFPIC){
            storageRef = sreference.child(path);
                if(bitmap!=null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] dataBytes = baos.toByteArray();


                    UploadTask upload = storageRef.putBytes(dataBytes);

                    upload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        @SuppressWarnings("VisibleForTests")
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        }
                    }).addOnSuccessListener(UserActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        @SuppressWarnings("VisibleForTests")
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads on complete
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(UserActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            url = downloadUrl.toString();

                            Picasso.with(UserActivity.this).load(url)
                                    .into(profPic);


                            DatabaseReference userRef = dbRef.child("RegisteredUsers").child(currentUser.getUserId());
                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("imageUrl",url);

                            userRef.updateChildren(hopperUpdates);


                            progressDialog.dismiss();

                        }
                    });
                }else{

                    progressDialog.dismiss();
                }
        }else if(requestCode==USER_POSTPIC){
            storageRef = sreference.child(path1);
            if(bitmap1!=null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos);
               byte[] dataBytes = baos.toByteArray();
               UploadTask upload = storageRef.putBytes(dataBytes);

                upload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    }
                }).addOnSuccessListener(UserActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(UserActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                        url = downloadUrl.toString();

                        UserPosts upost=new UserPosts();
                        upost.setImgUrl(url);
                        upost.setImgDesc(postMsg.getText().toString());
                        upost.setPostBy(currentUser.getFname()+" "+currentUser.getLname());
                        // time and date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        Date date = new Date();

                        String datetime= sdf.format(date);
                        upost.setImgDate(datetime);
                        DatabaseReference newRef=dbRef.child("RegisteredUsers").child(currentUser.getUserId()).child("Posts");
                        final DatabaseReference childref=newRef.push();
                        upost.setPostId(childref.getKey());
                        childref.setValue(upost);
                        //setRecylcer("Home");


                        progressDialog.dismiss();

                    }
                });
            }else{
                progressDialog.dismiss();
            }


        }


    }

}
