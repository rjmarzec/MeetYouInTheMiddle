package rjmarzec.com.robertmmeetyouinthemiddle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private SharedPreferences sharedPref;
    private Geocoder coder;
    private ArrayList<String> currentLocations;
    private ArrayList<LatLng> latLngs;
    private double middleLat = 0;
    private double middleLng = 0;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        //Shared Pref. Stuff
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        coder = new Geocoder(getApplicationContext());

        //Creating Values for the location arrayLists
        currentLocations = new ArrayList<String>(Arrays.asList(sharedPref.getString("currentSavedLocations", "").split(";;")));
        removeEmptyArraySpots(currentLocations);

        LatLng tempLatLng;
        latLngs = new ArrayList<LatLng>();

        for (String address : currentLocations)
        {
            tempLatLng = getLocationFromAddress(coder, getApplicationContext(), address);
            middleLat += tempLatLng.latitude;
            middleLng += tempLatLng.longitude;
            latLngs.add(tempLatLng);
            mMap.addMarker(new MarkerOptions().position(tempLatLng).title(address));
        }

        middleLat /= currentLocations.size();
        middleLng /= currentLocations.size();
        final LatLng bestLocationLatLng = new LatLng(middleLat, middleLng);

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(bestLocationLatLng)
                .zoom(17).build()));

        mMap.addMarker(new MarkerOptions()
                .position(bestLocationLatLng)
                .title("This is the midpoint.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        Toast.makeText(this, "This is the best spot! Tap the marker for more info.", Toast.LENGTH_LONG).show();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                Toast.makeText(MapsActivity.this, "Tap one of the icons in the bottom right to open the location in Google Maps.", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    public LatLng getLocationFromAddress(Geocoder geocoder, Context context, String inputtedAddress)
    {
        List<Address> address;
        LatLng resLatLng = null;

        try
        {
            // May throw an IOException
            address = geocoder.getFromLocationName(inputtedAddress, 5);
            if (address == null || address.size() == 0)
            {
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            resLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex)
        {

            ex.printStackTrace();
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        return resLatLng;
    }

    private void removeEmptyArraySpots(ArrayList<String> array)
    {
        for (int i = 0; i < array.size(); i++)
        {
            array.remove("");
        }
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), ChoosingLocationsActivity.class));
    }
}
