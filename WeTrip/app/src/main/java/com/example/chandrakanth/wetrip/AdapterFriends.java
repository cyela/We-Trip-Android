package com.example.chandrakanth.wetrip;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chandrakanth on 12/27/2017.
 */

public class AdapterFriends extends RecyclerView.Adapter<AdapterFriends.myViewHolder>  {

    private final LayoutInflater inflater;
    private Context mContext;
    List<UserInfo> mData;
    String mAction;
    private DatabaseReference dbRef;
    String currentEmail;
    UserInfo currentUser;
    private FirebaseAuth mAuth;
    ArrayList<UserRequest> frndsList,reqList;
    UserRequest sendUser,reqUser;

    public AdapterFriends(Context context, List<UserInfo> Data ,String action){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mData=Data;
        mAction=action;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public AdapterFriends.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.row_layout_reqst,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AdapterFriends.myViewHolder holder, final int position) {
        final UserInfo uinfo=mData.get(position);

        holder.title.setText(uinfo.getFname());
        if(!uinfo.getImageUrl().equals("null")){
            Picasso.with(getContext()).load(uinfo.getImageUrl())
                    .into(holder.proPic);
        }else{
            holder.proPic.setImageResource(R.drawable.noimage);
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentEmail=user.getEmail();

        }

        if(mAction.equals("Request")){

            //code for accepting or rejecting request from the other users
            Log.d("cameAdapter","request");
            holder.add.setEnabled(true);
            holder.remove.setVisibility(View.INVISIBLE);
            if(currentEmail!=null) {
                dbRef = FirebaseDatabase.getInstance().getReference().child("RegisteredUsers");
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = new UserInfo();
                        currentUser = null;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            UserInfo info = postSnapshot.getValue(UserInfo.class);
                            if (currentEmail.equals(info.getEmail())) {
                                currentUser = info;
                            }

                        }
                        holder.add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("cameAdapter", "requestClickedButton");
                                addAsFriend(uinfo);
                            }
                        });
                        holder.remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delRequest(uinfo);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                dbRef.addValueEventListener(postListener);
            }

        }else if(mAction.equals("Users")){
            Log.d("cameAdapter","Users");
            holder.remove.setVisibility(View.INVISIBLE);
            if(currentEmail!=null) {
                dbRef = FirebaseDatabase.getInstance().getReference().child("RegisteredUsers");
                final ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = new UserInfo();
                        currentUser = null;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            UserInfo info = postSnapshot.getValue(UserInfo.class);
                            if (currentEmail.equals(info.getEmail())) {
                                currentUser = info;
                            }

                        }
                        DatabaseReference frndRef = dbRef.child(currentUser.getUserId()).child("Friends");
                        ValueEventListener postListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                frndsList = new ArrayList<UserRequest>();
                                frndsList.clear();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    UserRequest info = postSnapshot.getValue(UserRequest.class);
                                    frndsList.add(info);

                                }
                                //frndList contains current user freinds
                                final DatabaseReference reqRef = dbRef.child(currentUser.getUserId()).child("Request_Send");
                                ValueEventListener postListener2 = new ValueEventListener() {


                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        reqList = new ArrayList<UserRequest>();
                                        reqList.clear();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            UserRequest info = postSnapshot.getValue(UserRequest.class);
                                            reqList.add(info);

                                        }
                                        // reqlist contains all the request sent by the current user
                                        if (compareList(frndsList, uinfo)) {
                                            holder.add.setImageResource(R.drawable.unfriend);
                                            holder.add.setEnabled(true);
                                            holder.add.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                                    dialog.setCancelable(true);
                                                    dialog.setMessage("Do you want to unfriend");
                                                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            DatabaseReference frndDel = dbRef.child(currentUser.getUserId()).child("Friends");
                                                            for (int i = 0; i < frndsList.size(); i++) {
                                                                if (frndsList.get(i).getId().equals(uinfo.getUserId())) {
                                                                    frndDel.child(frndsList.get(i).getReqId()).removeValue();
                                                                }
                                                            }
                                                            final DatabaseReference otherDel = dbRef.child(uinfo.getUserId()).child("Friends");
                                                            ValueEventListener delListener = new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                        UserRequest info = postSnapshot.getValue(UserRequest.class);
                                                                        if (info.getId().equals(currentUser.getUserId())) {
                                                                            otherDel.child(info.getReqId()).removeValue();
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            };
                                                            otherDel.addValueEventListener(delListener);

                                                        }
                                                    });
                                                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    final AlertDialog alert = dialog.create();
                                                    alert.show();
                                                }
                                            });
                                        } else if (compareList(reqList, uinfo)) {
                                            holder.add.setImageResource(R.drawable.waiting);
                                            holder.add.setEnabled(false);

                                        } else {
                                            holder.add.setImageResource(R.drawable.addfriend);
                                            holder.add.setEnabled(true);
                                            holder.add.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {


                                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                                    dialog.setCancelable(true);
                                                    dialog.setMessage("Do you want to send request");
                                                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            sendRequest(uinfo);
                                                            Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    final AlertDialog alert = dialog.create();
                                                    alert.show();

                                                }
                                            });

                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                reqRef.addValueEventListener(postListener2);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        frndRef.addValueEventListener(postListener1);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                dbRef.addValueEventListener(postListener);
            }
        }

    }
    public boolean compareList(ArrayList<UserRequest> Frnds,UserInfo user){
        for(int i=0;i<Frnds.size();i++)
        {
            if(Frnds.get(i).getId().equals(user.getUserId())){
                return true;
            }
        }

        return false;
    }
    public void sendRequest(UserInfo u){

        UserRequest uR=new UserRequest();
        uR.setStatus("Request sent");
        uR.setId(u.getUserId());
        DatabaseReference newRef=dbRef.child(currentUser.getUserId()).child("Request_Send");
        final DatabaseReference childRef=newRef.push();
        uR.setReqId(childRef.getKey());
        childRef.setValue(uR);

        UserRequest uR1=new UserRequest();
        uR1.setId(currentUser.getUserId());
        uR1.setStatus("Pending");
        DatabaseReference new1Ref = dbRef.child(u.getUserId()).child("Request_Received");
        final DatabaseReference childRef1=new1Ref.push();
        uR1.setReqId(childRef1.getKey());
        childRef1.setValue(uR1);

    }
    public void addAsFriend(final UserInfo reqU){

        DatabaseReference frndRecRef = dbRef.child(currentUser.getUserId()).child("Request_Received");
        ValueEventListener postListenerRec = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reqUser = new UserRequest();
                reqUser = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserRequest reqInfo = postSnapshot.getValue(UserRequest.class);
                    if(reqInfo.getId().equals(reqU.getUserId())){
                        reqUser=reqInfo;
                    }
                }


                if(reqUser!=null) {
                   DatabaseReference frndSenRef = dbRef.child(reqUser.getId()).child("Request_Send");
                    ValueEventListener postListenerSen = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            sendUser = new UserRequest();
                            sendUser = null;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                UserRequest senInfo = postSnapshot.getValue(UserRequest.class);
                                if (senInfo.getId().equals(currentUser.getUserId())) {
                                    sendUser = senInfo;
                                }
                            }
                            if(sendUser!=null) {




                                UserRequest frndSen = new UserRequest();
                                frndSen.setId(currentUser.getUserId());
                                frndSen.setStatus("Approved");
                                DatabaseReference new1Ref = dbRef.child(reqUser.getId()).child("Friends");
                                final DatabaseReference child1Ref = new1Ref.push();
                                frndSen.setReqId(child1Ref.getKey());
                                child1Ref.setValue(frndSen);



                                UserRequest frndRec = new UserRequest();
                                frndRec.setId(reqU.getUserId());
                                frndRec.setStatus("Approved");
                                DatabaseReference new2Ref = dbRef.child(currentUser.getUserId()).child("Friends");
                                final DatabaseReference child2Ref = new2Ref.push();
                                frndRec.setReqId(child2Ref.getKey());
                                child2Ref.setValue(frndRec);

                                DatabaseReference delRef = dbRef.child(reqUser.getId()).child("Request_Send");
                                delRef.child(sendUser.getReqId()).removeValue();

                                DatabaseReference del2Ref = dbRef.child(currentUser.getUserId()).child("Request_Received");
                                del2Ref.child(reqUser.getReqId()).removeValue();

                                Toast.makeText(getContext(),"added to your friends",Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    frndSenRef.addValueEventListener(postListenerSen);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        frndRecRef.addValueEventListener(postListenerRec);

    }
    public void delRequest(final UserInfo reqU){
        DatabaseReference frndRecRef = dbRef.child(currentUser.getUserId()).child("Request_Received");
        ValueEventListener postListenerRec = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reqUser = new UserRequest();
                reqUser = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserRequest reqInfo = postSnapshot.getValue(UserRequest.class);
                    if(reqInfo.getId().equals(reqU.getUserId())){
                        reqUser=reqInfo;
                    }
                }


                if(reqUser!=null) {
                    DatabaseReference frndSenRef = dbRef.child(reqUser.getId()).child("Request_Send");
                    ValueEventListener postListenerSen = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            sendUser = new UserRequest();
                            sendUser = null;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                UserRequest senInfo = postSnapshot.getValue(UserRequest.class);
                                if (senInfo.getId().equals(currentUser.getUserId())) {
                                    sendUser = senInfo;
                                }
                            }
                            if(sendUser!=null) {
                                DatabaseReference delRef = dbRef.child(reqUser.getId()).child("Request_Send");
                                delRef.child(sendUser.getReqId()).removeValue();
                                DatabaseReference del2Ref = dbRef.child(currentUser.getUserId()).child("Request_Received");
                                del2Ref.child(reqUser.getReqId()).removeValue();
                                Toast.makeText(getContext(),"request has been deleted successfully",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    frndSenRef.addValueEventListener(postListenerSen);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        frndRecRef.addValueEventListener(postListenerRec);

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }




    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        CircleImageView proPic;
        ImageButton add,remove;

        public myViewHolder(View itemView) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.tvName);
            proPic=(CircleImageView)itemView.findViewById(R.id.circulepeople);
            add=(ImageButton)itemView.findViewById(R.id.imgBtnAccpt);
            remove=(ImageButton)itemView.findViewById(R.id.imgBtnRejct);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


        }
    }

}

