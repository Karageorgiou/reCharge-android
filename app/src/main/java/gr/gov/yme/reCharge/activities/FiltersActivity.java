package gr.gov.yme.reCharge.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import gr.gov.yme.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class FiltersActivity extends AppCompatActivity {
   public String TAG = "FiltersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate: ");

        setContentView(R.layout.activity_filters);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.filters, new SettingsFragment())
                    .commit();
        }
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);



        }*/


        //find toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentWithResult = new Intent();
                intentWithResult.putExtra("FROM_ACTIVITY","FILTERS");
                setResult(1,intentWithResult);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intentWithResult = new Intent();
        intentWithResult.putExtra("FROM_ACTIVITY","FILTERS");
        setResult(1,intentWithResult);
        super.onBackPressed();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private String TAG = "FiltersActivity -> SettingsFragment extends PreferenceFragmentCompat";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Log.i(TAG,"onCreatePreferences: ");

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

            view.setBackgroundColor(getResources().getColor(R.color.gray_800));
            super.onViewCreated(view, savedInstanceState);
        }
    }


}