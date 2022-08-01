package gr.gov.yme.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.gov.yme.R;
import gr.gov.yme.models.locations.Feature;
import gr.gov.yme.models.locations.FeatureCollection;
import gr.gov.yme.models.point.Locat;
import gr.gov.yme.models.point.Root;
import gr.gov.yme.network.ChargePointApiService;
import gr.gov.yme.network.Token;


import gr.gov.yme.network.TokenReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 44;
    private MapView map = null;
    FusedLocationProviderClient mFusedLocationClient;
    Double longitude, latitude;
    IMapController mapController;
    Context ctx;
    List<Feature> nodesList;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_map);
        map = (MapView) findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        //get user's location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        getLastLocation();
        mapController = map.getController();
        mapController.setZoom(9.5);

        //server communication
        final String BASE_URL = "https://electrokinisi.yme.gov.gr/myfah-api/openApi/";
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        ChargePointApiService chargePointApiService = retrofit.create(ChargePointApiService.class);

        token = new Token("QzM0NDQyQzUyQjc4M0E5MDQ3Q0IyMjBEQzU2OUM4Qjk4RDREMjlENjYwOEQzMTY3N0Q3NTY5MkEzODQ4QjM1MjQxN0EwODVGMDg1NEM4NzQ3ODA3RDIxRTU5NkI1QzI4MDgwMzgyNEU5NDY5Mzg3MzIxNDVEMjAwOEY3OUFFNUQ2MDU0MzQwNTBFN0YyMTE5MTkwMzUwNzA1NTNFNEM2MDZFN0M3NDQzOTY0OEVDNEE5NTdFNDc1QzI2N0UzNjY2ODc2M0E4NEY3NzY0Njk3NzA1NDA0QjVDMDA1Q0UwNDMyMzdCNjI1QjczNTgxODdENTcwNkEwMTAzNzM0MDdGOUE2QjQ1NDkyMjhCQw==");

        //GET TOKEN

        //Call<TokenReceiver> getTokenCall = chargePointApiService.getToken();


        Call<FeatureCollection> featureCollectionCall = chargePointApiService.createFeatureCollection(token);
        featureCollectionCall.enqueue(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                Log.e("FEATURE COLLECTION CALL", "SUCCESS");
                FeatureCollection featureCollection = response.body();
                if (null != featureCollection) {
                    nodesList = featureCollection.features;
                    setPointsOnMap(nodesList, chargePointApiService, token);
                }
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable t) {
                Log.e("FEATURE COLLECTION CALL", "FAILURE");
            }
        });


    }


    @SuppressLint("NewApi")
    public void setPointsOnMap(List<Feature> features, ChargePointApiService chargePointApiService, Token token) {

        features.forEach(feature -> {
            Marker marker = getMarker(feature);


            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView map) {
                    Map<String, Object> locRequestMap = new HashMap<>();
                    locRequestMap.put("location_id", feature.properties.locationId);
                    locRequestMap.put("token", token.token);

                    Call<Root> locationInfoCall = chargePointApiService.getLocation(locRequestMap);

                    locationInfoCall.enqueue(new Callback<Root>() {
                        @Override
                        public void onResponse(Call<Root> call, Response<Root> response) {
                            Root location = response.body();
                            if (response.isSuccessful()) {
                                Log.e("RESPONSE", response.message());
                                Log.e("status", location.status);
                                Log.e("statusDesc", location.statusDesc);


                                if (null != location.loc) {
                                    setMarkerInfo(marker, location.loc);
                                    try {
                                        if (marker.isInfoWindowShown()) {
                                            marker.closeInfoWindow();
                                        } else {
                                            GeoPoint center = new GeoPoint(feature.geometry.coordinates.get(1), feature.geometry.coordinates.get(0));
                                            mapController.animateTo(center);
                                            marker.showInfoWindow();
                                        }
                                    } catch (NullPointerException e) {
                                        Log.e("EXCEPTION", "onClick: NullPointerException: " + e.getMessage());
                                    }
                                }
                            }
                            Log.e("markerInfoCall", "COMPLETE");
                        }

                        @Override
                        public void onFailure(Call<Root> call, Throwable t) {
                            //ResponseBody error = response.errorBody();
                            //Log.e("errorBody",error.toString());
                            Log.e("markerInfoCall", t.getMessage());

                        }


                    });
                    return true;
                }
            });
        });
        map.invalidate();
    }

    @NonNull
    private Marker getMarker(Feature feature) {
        //Get coordinates
        double pointLat = feature.geometry.coordinates.get(1);
        double pointLong = feature.geometry.coordinates.get(0);
        //Make marker
        Marker marker = new Marker(map);
        GeoPoint startPoint = new GeoPoint(pointLat, pointLong);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER);
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_default, null);
        marker.setIcon(d);
        marker.setTextLabelFontSize(44);
        marker.setTitle("Charging station");
        marker.setId(feature.properties.locationId);
        map.getOverlays().add(marker);
        return marker;
    }

    public void setMarkerInfo(Marker marker, Locat location) {
        /*InfoWindow infoWindow = new InfoWindow(R.layout.marker_info, map) {
            @Override
            public void onOpen(Object item) {

            }

            @Override
            public void onClose() {

            }
        };
        marker.setInfoWindow(infoWindow);*/

        marker.setTitle("[Κωδ.Σταθμου: " + location.id +"]");
        marker.setImage(getDrawable(R.drawable.ev_station_icon));



        marker.setTextLabelForegroundColor(R.color.design_default_color_secondary);


        marker.setSnippet("Όνομα σταθμού: " + location.name + "\n" +
                "Συντεταγμένες: [" + location.coordinates.latitude + " , " + location.coordinates.longitude + "]\n" +
                "Διεύθυνση: "+ location.address +"\n"+
                "Πόλη: " + location.city +"\n"+
                "Τ.Κ.: " + location.postalCode +"\n"+
                "Χώρα: " + location.state + " " + location.country);

        if(location.operator!=null&&location.owner!=null)
        {
            marker.setSubDescription("Διαχειριστής: " + location.operator.name + "\n" +
                    "Συντεταγμένες: [" + location.owner.name + "]\n");
        }



        //todo


    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            updateLocationOnMap(location);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }

    }

    private void updateLocationOnMap(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            updateLocationOnMap(mLastLocation);
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }


}