package com.example.piendop.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //create static places, locations and array adapter to update when click

    static ArrayAdapter arrayAdapter;
    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("com.example.piendop.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        //clear because when we add we add from the beginning
        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        //get array list from string
        try {

            places = (ArrayList<String>) ObjectSerializer.
                    deserialize(sharedPreferences.getString
                            ("places", ObjectSerializer.serialize(new ArrayList<String>())));

            latitudes = (ArrayList<String>) ObjectSerializer.
                    deserialize(sharedPreferences.getString
                            ("latitudes", ObjectSerializer.serialize(new ArrayList<String>())));

            longitudes = (ArrayList<String>) ObjectSerializer.
                    deserialize(sharedPreferences.getString
                            ("longitudes", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //if we add a new place update to location, places no need to update because it has already
        //updated in try catch above
        if (places.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {

            if (places.size() == latitudes.size() && latitudes.size() == longitudes.size()) {

                for (int i = 0; i < latitudes.size(); i++) {

                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));

                }


            }


        } else {//not thing added

            places.add("Add a new place...");
            locations.add(new LatLng(0, 0));

        }

        //list view
        ListView listView = findViewById(R.id.memorablePlacesList);
        //add a first row of places
        places.add("Add a new place ...");
        //add dummy location for first row
        locations.add(new LatLng(0,0));
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

    }
}
