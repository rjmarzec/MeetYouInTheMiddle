package rjmarzec.com.robertmmeetyouinthemiddle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.HashSet;

public class LandingActivity extends AppCompatActivity
{
    Button startButton, settingsButton;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        startButton = (Button)findViewById(R.id.landingActivityStartButton);
        settingsButton = (Button)findViewById(R.id.landingActivityAboutButton);

        //Shared Preferences Stuff
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Setting the values of the current saved locations back to the default
                editor.putStringSet("currentSavedLocations", new HashSet<String>(20));
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), ChoosingLocationsActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        System.exit(0);
    }
}
