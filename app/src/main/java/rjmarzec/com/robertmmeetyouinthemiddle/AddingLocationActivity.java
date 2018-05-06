package rjmarzec.com.robertmmeetyouinthemiddle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class AddingLocationActivity extends AppCompatActivity
{
    String selectedAddressString;
    Button currentLocationButton, enterLocationButton;
    CheckBox isFavoriteCheckBox;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private Boolean placeIsSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_location_activity);

        //Requests user permissions for location stuff
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        //Connecting Widgets
        currentLocationButton = (Button) findViewById(R.id.addingLocationActivityUserCurrentLocationButton);
        enterLocationButton = (Button) findViewById(R.id.addingLocationActivityEnterLocationButton);
        isFavoriteCheckBox = (CheckBox) findViewById(R.id.addingLocationActivityIsFavoriteCheckBox);

        //Shared Preferences Stuff
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        //Fragment that lets people use autocomplete to find addresses
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //Listener for when the user chooses a location from the autocomplete
        //This should save the location in the activity, but not start anything else
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {
                //Saves the address passed in
                selectedAddressString = place.getAddress().toString();

                TextView textViewbleh = (TextView) findViewById(R.id.testingplace);
                textViewbleh.setText(selectedAddressString);

                Log.i("TAG", "Place: " + place.getName());
                placeIsSelected = true;
            }

            @Override
            public void onError(Status status)
            {
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        currentLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity((new Intent(getApplicationContext(), GettingCurrentLocationActivity.class)).putExtra("isFavorite", isFavoriteCheckBox.isChecked()));
                //Toast.makeText(AddingLocationActivity.this, "Sorry, this function is currently unavailable. Please search for a nearby location instead.", Toast.LENGTH_SHORT).show();
            }
        });

        enterLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (placeIsSelected)
                {
                    String favoriteLocations = sharedPref.getString("favoriteLocations", "");
                    String currentSavedLocations = sharedPref.getString("currentSavedLocations", "");
                    if (isFavoriteCheckBox.isChecked())
                    {
                        favoriteLocations += ";;" + selectedAddressString;
                        editor.putString("favoriteLocations", favoriteLocations);
                    }
                    currentSavedLocations += ";;" + selectedAddressString;
                    editor.putString("currentSavedLocations", currentSavedLocations);

                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), ChoosingLocationsActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No location has been found. Please search and find one.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), ChoosingLocationsActivity.class));
    }
}
