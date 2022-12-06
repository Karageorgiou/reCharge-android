package gr.gov.yme.reCharge.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.splashscreen.SplashScreen;
import gr.gov.yme.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashScreen.setKeepOnScreenCondition(() -> true );
        launchMap();
    }


    private void launchMap() {
        Intent intent = new Intent(MainActivity.this,MapActivity.class);
        startActivity(intent);
        finish();
    }
}