package gr.gov.yme.reCharge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import gr.gov.yme.R;

public class DisclaimerActivity extends AppCompatActivity {

    private final String TAG = "DisclaimerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate: ");
        setContentView(R.layout.activity_disclaimer);

        //find toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentWithResult = new Intent();
                intentWithResult.putExtra("FROM_ACTIVITY","DISCLAIMER");
                setResult(1,intentWithResult);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
