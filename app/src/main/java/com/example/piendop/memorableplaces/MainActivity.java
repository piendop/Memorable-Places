package com.example.piendop.memorableplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

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
