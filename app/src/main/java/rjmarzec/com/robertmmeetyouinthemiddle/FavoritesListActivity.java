package rjmarzec.com.robertmmeetyouinthemiddle;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Arrays;

public class FavoritesListActivity extends AppCompatActivity
{
    private Spinner locationSpinner;
    private Button addLocationButton, removeLocationButton;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ArrayList<String> favoriteLocations, currentLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_list_activity);

        //Creating Widgets
        locationSpinner = (Spinner) findViewById(R.id.favoritesListActivityLocationSpinner);
        addLocationButton = (Button) findViewById(R.id.favoritesListActivityAddLocationButton);
        removeLocationButton = (Button) findViewById(R.id.favoritesListActivityRemoveLocationButton);

        //Shared Pref. Stuff
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        //Creating Values for the location arrayLists
        currentLocations = new ArrayList<String>(Arrays.asList(sharedPref.getString("currentSavedLocations", "").split(";;")));
        favoriteLocations = new ArrayList<String>(Arrays.asList(sharedPref.getString("favoriteLocations", "").split(";;")));
        removeEmptyArraySpots(currentLocations);
        removeEmptyArraySpots(favoriteLocations);
        createTableLayout(favoriteLocations);

        //The stuff that handles making the spinner show the list properly.
        //is connected to the locationArrayList
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, favoriteLocations);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(arrayAdapter);

        //Listener for the button that removes a location from favorites
        removeLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Removing the selected location from both location arrays
                favoriteLocations.remove(locationSpinner.getSelectedItemPosition());
                String temp = "";
                if(!favoriteLocations.isEmpty())
                {
                    for(String s : favoriteLocations)
                    {
                        temp += s + ";;";
                    }
                }
                editor.putString("favoriteLocations", temp);
                editor.commit();

                locationSpinner.setAdapter(arrayAdapter);

                if (favoriteLocations.size() == 0)
                {
                    Intent intent = new Intent(getApplicationContext(), ChoosingLocationsActivity.class);
                    startActivity(intent);
                }
                createTableLayout(favoriteLocations);
            }
        });

        addLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String temp = "";
                for(String s : currentLocations)
                {
                    temp += s + ";;";
                }
                temp = temp + favoriteLocations.get(locationSpinner.getSelectedItemPosition()) + ";;";

                editor.putString("currentSavedLocations", temp);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), ChoosingLocationsActivity.class);
                startActivity(intent);
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
        TableLayout tl = findViewById(R.id.favoritesListActivityTableLayout);
        tl.removeAllViews();

        TextView lable = new TextView(this);
        lable.setText("Saved Addresses:\n");
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
        startActivity(new Intent(getApplicationContext(), ChoosingLocationsActivity.class));
    }
}
