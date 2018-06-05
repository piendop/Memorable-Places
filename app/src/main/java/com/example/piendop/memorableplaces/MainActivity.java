package com.example.piendop.memorableplaces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //create static places, locations and array adapter to update when click

    static ArrayAdapter arrayAdapter;
    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<>();
    static ArrayList<String> latitudes = new ArrayList<>();
    static ArrayList<String> longitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences
                ("com.example.piendop.memorableplaces", Context.MODE_PRIVATE);

        //Hash set to store set of places, latitudes, longitudes
        HashSet<String> setPlaces= (HashSet<String>)sharedPreferences.getStringSet("places",null);
        HashSet<String> setLatitudes =(HashSet<String>)  sharedPreferences.getStringSet("latitudes",null);
        HashSet<String> setLongitudes =(HashSet<String>)  sharedPreferences.getStringSet("longitudes",null);

        //update places, latitudes and longitudes by hash set
        if(places.size()==0){
            places.add("Add a new place");
            locations.add(new LatLng(0,0));
        }else{
            places = new ArrayList<>(setPlaces);
        }

        if(setLatitudes!=null)
            latitudes = new ArrayList<>(setLatitudes);
        if(setLongitudes!=null)
            longitudes = new ArrayList<>(setLongitudes);



        //if places' size is 0 that means no place added else we add places to location
        if (setPlaces!=null && setPlaces.size()>0) {

            if (places.size() == latitudes.size() && latitudes.size() == longitudes.size()) {

                for (int i = 0; i < latitudes.size(); i++) {

                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));

                }
            }

        }

        //list view
        ListView listView = findViewById(R.id.memorablePlacesList);
        //set array adapter for list view
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.black_text_color,R.id.textView,places);
        listView.setAdapter(arrayAdapter);
        //set action when click on add a new place
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //when click to add a new place, switch to map activity
                //set up intent to switch to maps activity
                Intent intent= new Intent(getApplicationContext(),MapsSecondActivity.class);
                intent.putExtra("placeNumber",i);
                startActivity(intent);
            }
        });

        /**DELETE PLACES*/
        deletePlaces(listView);

    }

    private void deletePlaces(ListView listView) {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int indexDeleted = i;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure!")
                        .setMessage("Do you want to delete this place?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //remove places and locations
                                places.remove(indexDeleted);
                                locations.remove(indexDeleted);
                                latitudes.remove(indexDeleted);
                                longitudes.remove(indexDeleted);
                                //update changes in data in array adapter
                                arrayAdapter.notifyDataSetChanged();

                                //create hashset to update data in permanent storage
                                HashSet<String> setPlaces = new HashSet<>(places);
                                HashSet<String> setLatitudes = new HashSet<>(latitudes);
                                HashSet<String> setLongitudes = new HashSet<>(longitudes);

                                //update data in permanent storage
                                SharedPreferences sharedPreferences = getApplicationContext().
                                        getSharedPreferences("com.example.piendop.memorableplaces",MODE_PRIVATE);
                                sharedPreferences.edit().putStringSet("places",setPlaces).apply();
                                sharedPreferences.edit().putStringSet("latitudes",setLatitudes).apply();
                                sharedPreferences.edit().putStringSet("longitudes",setLongitudes).apply();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }
        });

    }
}
