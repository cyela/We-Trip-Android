package com.example.chandrakanth.wetrip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class TripActivity extends AppCompatActivity {
    UserTrip trip;
    CircleImageView tripPic;
    TextView tripTitle;
    ImageButton adPeple,sendMsg,sendPic;
    EditText etMsg;
    int TRIP_PROFPIC=10001;
    int TRIP_POSTPIC=10000;String url;
    ProgressDialog progressDialog;
    StorageReference storageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user;
    UserInfo currentUser;
    ArrayList<UserPosts> msgList;
    ArrayList<UserInfo> userList,frnsList;
    RecyclerView recyclerChat;
    AdapterChat cAdapter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menutrip, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.viewPlace:
                Intent intent=new Intent(TripActivity.this, PlaceActivity.class);
                intent.putExtra("TripId",trip.getTripId());
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_tripchat, null);

        getSupportActionBar().setCustomView(v);
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference  userRef=dbRef.child("RegisteredUsers");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList=new ArrayList<>();
                userList.clear();
                currentUser=new UserInfo();
                currentUser=null;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserInfo info =  postSnapshot.getValue(UserInfo.class);
                    if(user.getEmail().equals(info.getEmail())){

                        currentUser=info;
                    }
                    if(info!=null){
                        userList.add(info);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addValueEventListener(postListener);
        if(getIntent().getExtras()!=null){
            Bundle bundle=getIntent().getExtras().getBundle("intent_Trip");
            trip= (UserTrip) bundle.getSerializable("Current_Trip");
            tripPic=(CircleImageView)findViewById(R.id.circuleChatpic);
            tripTitle=(TextView)findViewById(R.id.tvTripTitle);
            adPeple=(ImageButton)findViewById(R.id.tripAddPeople);
            if(!trip.getTripPic().isEmpty()) {
                Picasso.with(TripActivity.this).load(trip.getTripPic())
                        .into(tripPic);
            }
            tripTitle.setText(trip.getTripName());
            tripTitle.setTextColor(Color.WHITE);
            etMsg=(EditText)findViewById(R.id.etChat);
            sendMsg=(ImageButton)findViewById(R.id.imgSend);
            sendPic=(ImageButton)findViewById(R.id.imgSendPic);
            //code for fetching all the messages under the trip
            DatabaseReference  postRef=dbRef.child("Trips").child(trip.getTripId()).child("Messages");
            ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    msgList=new ArrayList<>();
                    msgList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        UserPosts posts =  postSnapshot.getValue(UserPosts.class);
                        if(posts!=null){
                            msgList.add(posts);
                        }

                    }

                    // code for setting recycler view layout

                    recyclerChat = (RecyclerView) findViewById(R.id.recycleChat);
                    cAdapter = new AdapterChat(TripActivity.this, msgList,userList,currentUser);
                    recyclerChat.setAdapter(cAdapter);
                    recyclerChat.setHasFixedSize(true);
                    recyclerChat.setLayoutManager(new LinearLayoutManager(TripActivity.this,LinearLayoutManager.VERTICAL,false));




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            postRef.addValueEventListener(postListener1);



            tripPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    openGallery(TRIP_PROFPIC);

                }
            });
            DatabaseReference  userRefMem=dbRef.child("RegisteredUsers");
            ValueEventListener postListenerMem = new ValueEventListener() {
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
                    DatabaseReference  frndRef=dbRef.child("RegisteredUsers").child(currentUser.getUserId()).child("Friends");
                    ValueEventListener postListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            frnsList=new ArrayList<>();
                            frnsList.clear();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserRequest info =  postSnapshot.getValue(UserRequest.class);
                                for(int i=0;i<userList.size();i++){
                                    if(userList.get(i).getUserId().equals(info.getId())){
                                        frnsList.add(userList.get(i));
                                    }
                                }

                            }
                            DatabaseReference  tripRef=dbRef.child("Trips").child(trip.getTripId()).child("Members");
                            ValueEventListener postListener3 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final ArrayList<UserRequest> trpList=new ArrayList<>();

                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        UserRequest trpinfo =  postSnapshot.getValue(UserRequest.class);
                                        if(trpinfo!=null){
                                            trpList.add(trpinfo);
                                        }

                                    }
                                    // compare trpList and frnslist
                                    final ArrayList<UserInfo> finalList=compMem(frnsList,trpList);
                                    Log.d("FrndsList", String.valueOf(finalList.size()));
                                    final CharSequence[] items = new CharSequence[finalList.size()];

                                    for(int i=0;i<finalList.size();i++){
                                        items[i]=finalList.get(i).getFname()+" "+finalList.get(i).getLname();
                                    }

                                    adPeple.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // alertmsg displaying list of friends and option for adding them to group


                                            AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);
                                            builder.setTitle("Select to add people");
                                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int item) {
                                                    // Do something with the selection

                                                    UserRequest ur=new UserRequest();
                                                    DatabaseReference newRef=dbRef.child("Trips").child(trip.getTripId()).child("Members");
                                                    final DatabaseReference childref=newRef.push();
                                                    ur.setReqId(childref.getKey());
                                                    ur.setId(finalList.get(item).getUserId());
                                                    ur.setStatus("added");
                                                    childref.setValue(ur);

                                                }
                                            });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            tripRef.addValueEventListener(postListener3);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    frndRef.addValueEventListener(postListener2);
   }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            userRefMem.addValueEventListener(postListenerMem);




            sendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserPosts upost=new UserPosts();
                    upost.setImgDesc(etMsg.getText().toString());
                    upost.setPostBy(currentUser.getUserId());
                    // time and date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                    Date date = new Date();

                    String datetime= sdf.format(date);
                    upost.setImgDate(datetime);
                    DatabaseReference newRef=dbRef.child("Trips").child(trip.getTripId()).child("Messages");
                    final DatabaseReference childref=newRef.push();
                    upost.setPostId(childref.getKey());
                    childref.setValue(upost);
                    etMsg.setText("");

                }
            });
            sendPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGallery(TRIP_POSTPIC);
                }
            });

        }

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
        final String path = "images/TripPic/" + user.getEmail() + ".png";
        final String path1 = "images/TripPostPic/" + UUID.randomUUID()+user.getEmail() + ".png";


        Bitmap bitmap = null;
        Bitmap bitmap1=null;
        if (resultCode == RESULT_OK) {
            if (requestCode == TRIP_PROFPIC) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(requestCode==TRIP_POSTPIC){
                Uri imageUri = data.getData();
                try {
                    bitmap1= BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(imageUri));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }

        }
        if(requestCode==TRIP_PROFPIC){
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
                }).addOnSuccessListener(TripActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(TripActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                        url = downloadUrl.toString();
                        Picasso.with(TripActivity.this).load(url)
                                .into(tripPic);
                        DatabaseReference userRef = dbRef.child("Trips").child(trip.getTripId());
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put("tripPic",url);
                        userRef.updateChildren(hopperUpdates);
                        progressDialog.dismiss();

                    }
                });
            }else{
                progressDialog.dismiss();
            }
        }else if(requestCode==TRIP_POSTPIC){
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
                }).addOnSuccessListener(TripActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(TripActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                        url = downloadUrl.toString();

                        UserPosts upost=new UserPosts();
                        upost.setImgUrl(url);
                        upost.setPostBy(currentUser.getUserId());
                        // time and date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        Date date = new Date();

                        String datetime= sdf.format(date);
                        upost.setImgDate(datetime);
                        DatabaseReference newRef=dbRef.child("Trips").child(trip.getTripId()).child("Messages");
                        final DatabaseReference childref=newRef.push();
                        upost.setPostId(childref.getKey());
                        childref.setValue(upost);



                        progressDialog.dismiss();

                    }
                });
            }else{
                progressDialog.dismiss();
            }


        }


    }

    public ArrayList<UserInfo> compMem(ArrayList<UserInfo> frns,ArrayList<UserRequest> mem){
        ArrayList<UserInfo> result=new ArrayList<>();
        if(!mem.isEmpty()){
            for(int i=0;i<frns.size();i++){
                for(int j=0;j<mem.size();j++){
                    if(frns.get(i).getUserId().equals(mem.get(j).getId())){
                        result.add(frns.get(i));
                    }
                }
            }
            if(!result.isEmpty()){

                for(int i=0;i<result.size();i++){
                    frns.remove(result.get(i));
                }
                return frns;
            }
        }
      return frns;
    }


}
