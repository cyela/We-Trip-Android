package com.example.chandrakanth.wetrip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Chandrakanth on 12/27/2017.
 */

public class AdapterTrip extends RecyclerView.Adapter<AdapterTrip.myViewHolder>  {

    private final LayoutInflater inflater;
    private Context mContext;
    List<UserTrip> mData;
    UserTrip trip;
    private DatabaseReference dbRef;


    public AdapterTrip(Context context, List<UserTrip> Data){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mData=Data;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public AdapterTrip.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.row_layout_addtrip,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AdapterTrip.myViewHolder holder, final int position) {
        trip=mData.get(position);
        RelativeLayout lay=(RelativeLayout) holder.itemView.findViewById(R.id.relative_addtrip);
        // assign light background color for the trip row????
        holder.title.setText(trip.getTripName());
        PrettyTime p  = new PrettyTime();

        try {
            Date date1=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(trip.getCreatDate());
            holder.tDate.setText(p.format(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }




    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView title,tDate;

        public myViewHolder(View itemView) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.tvTrpName);


            tDate=(TextView)itemView.findViewById(R.id.tvTrpDate);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

                Intent intent=new Intent(mContext,TripActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Current_Trip",mData.get(getAdapterPosition()));
                intent.putExtra("intent_Trip",bundle);
                mContext.startActivity(intent);

        }


        @Override
        public boolean onLongClick(View view) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setMessage("Do you want to delete the group");
            alertDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dbRef=FirebaseDatabase.getInstance().getReference();
                            DatabaseReference delRef=dbRef.child("Trips");
                            delRef.child(mData.get(getAdapterPosition()).getTripId()).removeValue();
                            Toast.makeText(mContext,"Removed successfully",Toast.LENGTH_SHORT).show();

                        }
                    });

            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();


            return true;
        }
    }


}


