package rjmarzec.com.robertmmeetyouinthemiddle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChoosingLocationsActivity extends AppCompatActivity
{
    private Button addLocationButton, addFavoriteLocationButton, removeSelectedLocationButton, removeAllLocationsButton, startSearchButton;
    private Spinner locationSpinner;
    private ArrayList<String> favoriteLocations, currentLocations;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosing_locations_activity);

        //Connecting View from the layout
        locationSpinner = (Spinner) findViewById(R.id.choosingLocationsActivityLocationSpinner);
        addLocationButton = (Button) findViewById(R.id.choosingLocationsActivityAddLocationButton);
        addFavoriteLocationButton = (Button) findViewById(R.id.choosingLocationsActivityAddFavoriteLocationButton);
        removeSelectedLocationButton = (Button) findViewById(R.id.choosingLocationsActivityRemoveSelectedLocationButton);
        removeAllLocationsButton = (Button) findViewById(R.id.choosingLocationsActivityRemoveAllLocationsButton);
        startSearchButton = (Button) findViewById(R.id.choosingLocationsActivityStartSearch);

        //Arrays containing the saved location values
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        currentLocations = new ArrayList<String>(Arrays.asList(sharedPref.getString("currentSavedLocations", "").split(";;")));
        favoriteLocations = new ArrayList<String>(Arrays.asList(sharedPref.getString("favoriteLocations", "").split(";;")));
        removeEmptyArraySpots(currentLocations);
        removeEmptyArraySpots(favoriteLocations);

        //Table Layout and Spinner Stuff
        createTableLayout(currentLocations);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currentLocations);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(arrayAdapter);

        //Listener for the button to add more locations
        addLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), AddingLocationActivity.class);
                startActivity(intent);
            }
        });

        //Listener for the button to add a location from saved favorites
        addFavoriteLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //if the user has no saved favorites, don't let them access the page
                if (favoriteLocations.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "No favorite locations found. Please add at least 1 favorite locations to continue.", Toast.LENGTH_LONG).show();
                }
                //otherwise, move them to the activity that lets them add locations from their favorites
                else
                {
                    Intent intent = new Intent(getApplicationContext(), FavoritesListActivity.class);
                    startActivity(intent);
                }
            }
        });

        //Listener for the button to remove the last value
        removeSelectedLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(currentLocations.size() > 0)
                {
                    currentLocations.remove(locationSpinner.getSelectedItem());
                    createTableLayout(currentLocations);
                    String temp = "";
                    if(!currentLocations.isEmpty())
                    {
                        for (String s : currentLocations)
                        {
                            temp += s + ";;";
                        }
                    }
                    editor.putString("currentSavedLocations", temp);
                    editor.commit();
                    locationSpinner.setAdapter(arrayAdapter);
                }
            }
        });

        removeAllLocationsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currentLocations.clear();
                createTableLayout(currentLocations);
                editor.putString("currentSavedLocations", "");
                editor.commit();
                locationSpinner.setAdapter(arrayAdapter);
            }
        });

        //Listener for the button that takes you to the google map
        startSearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Don't allow the user to start searching if they have <2 values
                if (currentLocations.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "No locations found. Please add at least 2 locations before continuing.", Toast.LENGTH_LONG).show();
                } else if (currentLocations.size() == 1)
                {
                    Toast.makeText(getApplicationContext(), "Only 1 location found. Please add at least 1 more location to continue.", Toast.LENGTH_LONG).show();
                }
                //otherwise, move them to starting the search on the google map
                else
                {
                    //Creating an intent to move gmap stuff and move to the gmap
                    Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);

                    startActivity(intent);
                }
            }
        });
    }

    private void removeEmptyArraySpots(ArrayList<String> array)
    {
        for(int i = 0; i < array.size(); i ++)
        {
            array.remove("");
        }
    }

    private void createTableLayout(ArrayList<String> array)
    {
        TableLayout tl = findViewById(R.id.choosingLocationsActivityTableLayout);
        tl.removeAllViews();

        TextView lable = new TextView(this);
        lable.setText("Current Addresses:\n");
        tl.addView(lable);

        for (String address : array) {
            TableRow tr = new TableRow(this);

            TextView temp = new TextView(this);
            temp.setText(address + "\n");
            tr.addView(temp);

            tl.addView(tr);
        }
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), LandingActivity.class));
    }
}

