package com.example.chandrakanth.wetrip;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Chandrakanth on 12/20/2017.
 */

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.myViewHolder>  {

    private final LayoutInflater inflater;
    private Context mContext;
    List<UserPosts> mData;


    public AdapterPosts(Context context, List<UserPosts> Data){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mData=Data;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public AdapterPosts.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.row_layout_posts,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AdapterPosts.myViewHolder holder, final int position) {
        final UserPosts post=mData.get(position);

        holder.title.setText(post.getPostBy());
        holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.Desc.setText(post.getImgDesc());
        PrettyTime p  = new PrettyTime();

        try {
            Date date1=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(post.getImgDate());
            holder.tDate.setText(p.format(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Picasso.with(getContext()).load(post.getImgUrl())
                .into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }




    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView photo;
        TextView title,Desc,tDate;

        public myViewHolder(View itemView) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.tvPostBy);
            Desc=(TextView)itemView.findViewById(R.id.tvDesc);
            photo=(ImageView)itemView.findViewById(R.id.imageView);
            tDate=(TextView)itemView.findViewById(R.id.tvDate);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


        }
    }

}


