package com.example.chandrakanth.wetrip;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Chandrakanth on 12/29/2017.
 */

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.myViewHolder>  {

    private final LayoutInflater inflater;
    private Context mContext;
    List<UserPosts> mMsgData;
    List<UserInfo> mUserData;
    UserInfo mCrntUser;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference();
    FirebaseUser user;
    UserPosts msgPost;
    UserInfo  msgOwner,currntOwner;


    public AdapterChat(Context context, List<UserPosts> msgData,List<UserInfo> userData,UserInfo currentUser){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mMsgData=msgData;
        mUserData=userData;
        mCrntUser=currentUser;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public AdapterChat.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.row_layout_tripchat,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final AdapterChat.myViewHolder holder, final int position) {
        msgPost=mMsgData.get(position);
        for(int i=0;i<mUserData.size();i++){
            if(msgPost.getPostBy().equals(mUserData.get(i).getUserId())){
                msgOwner=mUserData.get(i);
            }
        }
        currntOwner=mCrntUser;
        // now i have msg and user details,curnt user details as well and i need to populate it in the recycler view
        // if its normal msg
        RelativeLayout lay=(RelativeLayout) holder.itemView.findViewById(R.id.relativeChat);
        if(msgPost.getImgUrl()==null){
            // picmsg and pic time
            holder.picMsg.setVisibility(View.GONE);
            holder.picTime.setVisibility(View.GONE);

            if(msgOwner.getUserId().equals(currntOwner.getUserId())){

                lay.setBackgroundColor(Color.LTGRAY);
                lay.setGravity(Gravity.RIGHT);
                lay.setBackgroundResource(R.drawable.bubble2);


                holder.UserName.setText(msgOwner.getFname());
                holder.UserName.setTextColor(Color.BLACK);
                holder.UserName.setPaintFlags(holder.UserName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.msg.setText(msgPost.getImgDesc());
                holder.msg.setTextColor(Color.BLACK);
                PrettyTime p  = new PrettyTime();

                try {
                    Date date1=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(msgPost.getImgDate());
                    holder.msgTime.setText(p.format(date1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{

                lay.setBackgroundColor(Color.GRAY);
                lay.setGravity(Gravity.LEFT);
                lay.setBackgroundResource(R.drawable.bubble1);

                holder.UserName.setText(msgOwner.getFname());
                holder.UserName.setTextColor(Color.BLACK);
                holder.UserName.setPaintFlags(holder.UserName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.msg.setText(msgPost.getImgDesc());
                holder.msg.setTextColor(Color.BLACK);
                PrettyTime p  = new PrettyTime();

                try {
                    Date date1=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(msgPost.getImgDate());
                    holder.msgTime.setText(p.format(date1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


        }

        // if its a image msg
        else if(msgPost.getImgDesc()==null){
            //msgtime and msg
            holder.msgTime.setVisibility(View.GONE);
            holder.msg.setVisibility(View.GONE);
            if(msgOwner.getUserId().equals(currntOwner.getUserId())){
                lay.setBackgroundColor(Color.LTGRAY);
                lay.setGravity(Gravity.RIGHT);
                lay.setBackgroundResource(R.drawable.bubble2);
                holder.UserName.setText(msgOwner.getFname());
                holder.UserName.setTextColor(Color.BLACK);
                holder.UserName.setPaintFlags(holder.UserName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                Picasso.with(mContext).load(msgPost.getImgUrl())
                        .into(holder.picMsg);
                holder.picMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                PrettyTime p  = new PrettyTime();

                try {
                    Date date1=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(msgPost.getImgDate());
                    holder.picTime.setText(p.format(date1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }




            }else{
                lay.setBackgroundColor(Color.GRAY);
                lay.setGravity(Gravity.LEFT);
                lay.setBackgroundResource(R.drawable.bubble1);
                holder.UserName.setText(msgOwner.getFname());
                holder.UserName.setTextColor(Color.BLACK);
                holder.UserName.setPaintFlags(holder.UserName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                Picasso.with(mContext).load(msgPost.getImgUrl())
                        .into(holder.picMsg);
                holder.picMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                     return false;
                    }
                });
                PrettyTime p  = new PrettyTime();

                try {
                    Date date1=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(msgPost.getImgDate());
                    holder.picTime.setText(p.format(date1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }




            }
        }

    }

    @Override
    public int getItemCount() {
        return mMsgData.size();
    }




    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView UserName,msgTime,picTime,msg;
        ImageView picMsg;

        public myViewHolder(View itemView) {



            super(itemView);
            UserName=(TextView)itemView.findViewById(R.id.tvCUser);
            msgTime=(TextView)itemView.findViewById(R.id.tvCtime);
            picTime=(TextView)itemView.findViewById(R.id.tvCPtime);
            msg=(TextView)itemView.findViewById(R.id.tvCMsg);
            picMsg=(ImageView) itemView.findViewById(R.id.imgCPic);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

        }

    }








}



