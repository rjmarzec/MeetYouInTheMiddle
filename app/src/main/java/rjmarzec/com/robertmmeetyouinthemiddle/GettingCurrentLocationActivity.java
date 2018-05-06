package rjmarzec.com.robertmmeetyouinthemiddle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GettingCurrentLocationActivity extends AppCompatActivity
{
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput = "";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getting_current_location_activity);

        //Shared Preferences Stuff
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mResultReceiver = new AddressResultReceiver(new Handler());

        getLocation();
    }

    @SuppressWarnings("MissingPermission")
    public void getLocation()
    {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                mLastLocation = location;

                //Do stuff if things to wrong
                if (mLastLocation != null && Geocoder.isPresent())
                {
                    startIntentService();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again or manually enter your location.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), AddingLocationActivity.class));
                }
            }
        });
    }

    //Starts an IntentService that handles changing the location information into an address, which it then returns
    protected void startIntentService()
    {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        //Giving the intent the variables that it needs to do its job
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        //Starting the intentService
        startService(intent);
    }

    //Contains the code for what runs when the address gets passed back here
    class AddressResultReceiver extends ResultReceiver
    {
        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            if (resultCode == Constants.SUCCESS_RESULT)
            {
                String favoriteLocations = sharedPref.getString("favoriteLocations", "");
                String currentSavedLocations = sharedPref.getString("currentSavedLocations", "");
                if (getIntent().getBooleanExtra("isFavorite", false))
                {
                    favoriteLocations += ";;" + mAddressOutput;
                    editor.putString("favoriteLocations", favoriteLocations);
                }
                currentSavedLocations += ";;" + mAddressOutput;
                editor.putString("currentSavedLocations", currentSavedLocations);

                editor.commit();
                Intent intent = new Intent(getApplicationContext(), ChoosingLocationsActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), ChoosingLocationsActivity.class));
    }
}
