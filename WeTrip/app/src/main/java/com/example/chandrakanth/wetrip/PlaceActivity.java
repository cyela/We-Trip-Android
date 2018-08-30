package com.example.chandrakanth.wetrip;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PlaceActivity extends AppCompatActivity {
int PLACE_PICKER_REQUEST = 1;
    String tripId;
    RecyclerView recyclePlace;
    AdapterPlaces pAdapter;
    ArrayList<UserTripPlaces> placeList;
    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        setTitle("Your Places");
        if(getIntent().getExtras()!=null){
            tripId= (String) getIntent().getExtras().get("TripId");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(android.R.drawable.ic_menu_mylocation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Please wait for a while", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                addPlaceFromMaps(PLACE_PICKER_REQUEST);




            }
        });



        DatabaseReference  userRef=dbRef.child("Trips").child(tripId).child("Places");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placeList=new ArrayList<>();
                placeList.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserTripPlaces info =  postSnapshot.getValue(UserTripPlaces.class);
                    if(info!=null){
                        placeList.add(info);
                    }


                }
                // change list based on position
                Collections.sort(placeList, new Comparator<UserTripPlaces>() {
                    @Override
                    public int compare(UserTripPlaces z1, UserTripPlaces z2) {
                        if (z1.getPosition() > z2.getPosition())
                            return 1;
                        if (z1.getPosition() < z2.getPosition())
                            return -1;
                        return 0;
                    }
                });
                recyclePlace = (RecyclerView) findViewById(R.id.recycleplace);
                pAdapter = new AdapterPlaces(PlaceActivity.this, placeList);

                recyclePlace.setAdapter(pAdapter);
                recyclePlace.setHasFixedSize(true);
                recyclePlace.setLayoutManager(new LinearLayoutManager(PlaceActivity.this,LinearLayoutManager.VERTICAL,false));



                ItemTouchHelper.SimpleCallback simpleTouch=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                        ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT ){

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {



                            Collections.swap(placeList, viewHolder.getAdapterPosition(), target.getAdapterPosition());

                            // and notify the adapter that its dataset has changed
                            pAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                            for(int i=0;i<placeList.size();i++){

                                DatabaseReference userRef = dbRef.child("Trips").child(tripId).child("Places").child(placeList.get(i).getPlaceRefId());
                                Map<String, Object> hopperUpdates = new HashMap<>();
                                hopperUpdates.put("position",i);
                                userRef.updateChildren(hopperUpdates);
                            }

                            return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.RIGHT) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlaceActivity.this);
                            alertDialog.setMessage("Do you want to delete?");
                            alertDialog.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {


                                            dbRef.child("Trips").child(tripId).child("Places").child(placeList.get(viewHolder.getAdapterPosition()).getPlaceRefId()).removeValue();

                                        }
                                    });

                            alertDialog.setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            pAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                            dialog.cancel();
                                        }
                                    });

                            alertDialog.show();
                        }
                    }
                };

                ItemTouchHelper ith = new ItemTouchHelper(simpleTouch);
                ith.attachToRecyclerView(recyclePlace);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(postListener);
   }
    public void addPlaceFromMaps(int request){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(PlaceActivity.this), request);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode==PLACE_PICKER_REQUEST){
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                DatabaseReference placeRef=dbRef.child("Trips").child(tripId).child("Places");
                UserTripPlaces selPlace=new UserTripPlaces();
                selPlace.setPlaceName(String.valueOf(place.getName()));
                selPlace.setLatitude(place.getLatLng().latitude);
                selPlace.setLongitude(place.getLatLng().longitude);
                selPlace.setPlaceId(place.getId());
                selPlace.setTripId(tripId);
                if(!placeList.isEmpty()){
                    selPlace.setPosition(placeList.size());
                }else{
                    selPlace.setPosition(0);
                }


                DatabaseReference childRef=placeRef.push();
                selPlace.setPlaceRefId(childRef.getKey());
                childRef.setValue(selPlace);


            }


        }
    }

}
