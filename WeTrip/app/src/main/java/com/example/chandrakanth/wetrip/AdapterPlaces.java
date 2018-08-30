package com.example.chandrakanth.wetrip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chandrakanth on 1/2/2018.
 */

public class AdapterPlaces extends RecyclerView.Adapter<AdapterPlaces.myViewHolder>  {


    private final LayoutInflater inflater;
    private Context mContext;
    List<UserTripPlaces> mData;
    UserTripPlaces uTPlace;

    public AdapterPlaces(Context context, List<UserTripPlaces> Data){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mData=Data;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public AdapterPlaces.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.row_layout_place,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AdapterPlaces.myViewHolder holder, final int position) {
       uTPlace=mData.get(position);
        holder.title.setText(uTPlace.getPlaceName());



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }




    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnDragListener{
        ImageView scroll,nav;
        TextView title;

        public myViewHolder(View itemView) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.tvPlace);

            nav=(ImageButton)itemView.findViewById(R.id.imgDirctn);
            nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setCancelable(false);
                    dialog.setTitle("Navigation");
                    dialog.setMessage("Please click yes for Navigation");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+uTPlace.getLatitude() +",+"+uTPlace.getLongitude()+"&mode=d");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            mContext.startActivity(mapIntent);

                        }
                    });
                    dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
            });
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


        }

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {



            return false;
        }
    }



}




