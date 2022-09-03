package gr.gov.yme.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.gov.yme.R;
import gr.gov.yme.activities.util.ListAdapter;
import gr.gov.yme.models.locations.Feature;
import gr.gov.yme.models.locations.FeatureCollection;
import gr.gov.yme.models.point.Connector;
import gr.gov.yme.models.point.Locat;
import gr.gov.yme.models.point.Root;
import gr.gov.yme.network.BasicAuthInterceptor;
import gr.gov.yme.network.ChargePointApiService;
import gr.gov.yme.network.Token;


import gr.gov.yme.network.TokenReceiver;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapActivity extends AppCompatActivity {
    private String TAG = "MapActivity ";

    Context ctx;

    final String TOKEN_URL = "http://dev.e-research.gr/myfah/openApi/";
    final String BASE_URL = "https://electrokinisi.yme.gov.gr/myfah-api/openApi/";
    private String USERNAME = "user1234";
    private String PASSWORD = "2372a5d0-e2c4-4a66-a102-719f7843e57b";
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 44;

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;


    private TextView tv_title;
    private TextView tv_type;
    private TextView tv_id;
    private TextView tv_address;
    private TextView tv_postal;
    private TextView tv_city;
    private TextView tv_country;
    private TextView tv_long;
    private TextView tv_lat;

    private ImageView ic_drag;

    ListView listView;
    ListAdapter listAdapter;
    ArrayList<Connector> connectorList;


    private MapView map = null;

    private FloatingActionButton gps_button;
    private FloatingActionButton navigate_button;
    private FloatingActionButton filter_button;

    //private Button gps_button;

    private Button info_button;

    private boolean wasOpen = false;
    private boolean actionDownConfirmed = false;

    ConstraintLayout info_layout;
    RelativeLayout sheet_top;

    IMapController mapController;
    FusedLocationProviderClient mFusedLocationClient;

    //map overlays
    MyLocationNewOverlay myLocationNewOverlay;
    CompassOverlay compassOverlay;
    InternalCompassOrientationProvider internalCompassOrientationProvider;
    RotationGestureOverlay rotationGestureOverlay;
    ScaleBarOverlay scaleBarOverlay;

    List<Feature> nodesList;
    Token token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();

        //load initialize the osmdroid configuration
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_map);
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMinZoomLevel(5d);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        //find views
        gps_button = findViewById(R.id.btn_gps);
        filter_button = findViewById(R.id.btn_filter);
        //info_button = findViewById(R.id.btn_info);
        info_layout = findViewById(R.id.info_layout);

        navigate_button = findViewById(R.id.fab_locate);

        tv_title = findViewById(R.id.tv_title);
        tv_type = findViewById(R.id.tv_type);
        tv_id = findViewById(R.id.tv_id);
        tv_address = findViewById(R.id.tv_address);
        tv_postal = findViewById(R.id.tv_postal);
        tv_city = findViewById(R.id.tv_city);
        tv_country = findViewById(R.id.tv_country);
        tv_long = findViewById(R.id.tv_long);
        tv_lat = findViewById(R.id.tv_lat);
        ic_drag = findViewById(R.id.ic_drag);

        listView = findViewById(R.id.listview);


        // Bottom Sheet
        Drawable icDragUp = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drag_up, null);
        Drawable icDragDown = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drag_down, null);
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheet_top = findViewById(R.id.bottom_sheet_top);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHalfExpandedRatio(0.4f);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    navigate_button.setAlpha(1f);
                    ic_drag.setImageDrawable(icDragUp);
                } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    ic_drag.setImageDrawable(icDragDown);
                } else if (state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    ic_drag.setImageDrawable(icDragDown);
                    navigate_button.setAlpha(0f);
                }

                //TODO use drag icons on top of each other and change alpha respectively
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                navigate_button.setAlpha((1-slideOffset)/2);
            }
        });

        sheet_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });

        //TODO

        //find toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        connectorList = new ArrayList<>();
        listAdapter = new ListAdapter(ctx, connectorList);


        requestPermissions();

        init();

        init_map();

        getToken();


    }

    private void init_map() {
        //create map controller
        mapController = map.getController();
        mapController.setZoom(15.0);

        //get user's location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(this);

        //show user's location
        myLocationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        myLocationNewOverlay.enableFollowLocation();

        Drawable currentDraw = ResourcesCompat.getDrawable(getResources(), R.drawable.twotone_navigation_black_48, null);
        Bitmap arrowIcon = null;
        if (currentDraw != null) {
            arrowIcon = ((BitmapDrawable) currentDraw).getBitmap();
        }
        myLocationNewOverlay.setDirectionIcon(arrowIcon);
        myLocationNewOverlay.setPersonAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);

        //rotation gestures
        rotationGestureOverlay = new RotationGestureOverlay(map);
        rotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.setVerticalMapRepetitionEnabled(false);
        map.getOverlays().add(rotationGestureOverlay);

        //scale bar
        final DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        scaleBarOverlay = new ScaleBarOverlay(map);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setAlignBottom(true);
        scaleBarOverlay.setAlignRight(true);
        /*scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2,
                displayMetrics.heightPixels / 11);*/
        map.getOverlays().add(scaleBarOverlay);

        //loading color
        map.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(R.color.light_blue);
        map.getOverlayManager().getTilesOverlay().setLoadingLineColor(R.color.white);

        map.getOverlayManager().add(new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                hideBottomSheet();
                if (InfoWindow.getOpenedInfoWindowsOn(map).size() != 0) {
                    Log.e(TAG, "there are info windows open ");
                    InfoWindow.closeAllInfoWindowsOn(map);
                }
                return super.onSingleTapConfirmed(e, mapView);
            }
        });
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


    private void init() {
        Log.d(TAG, "init: initializing");

        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getLastLocation();
                map.setMapOrientation(0);
            }
        });

        /*info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked info icon");

                toggleBottomSheet();

            }
        });*/
    }

    private void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    public void getToken() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(USERNAME, PASSWORD))
                .build();

        Retrofit tokenRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(TOKEN_URL)
                .client(client)
                .build();

        ChargePointApiService chargePointApiServiceToken = tokenRetrofit.create(ChargePointApiService.class);
        Call<TokenReceiver> getTokenCall = chargePointApiServiceToken.getToken();
        getTokenCall.enqueue(new Callback<TokenReceiver>() {
            @Override
            public void onResponse(Call<TokenReceiver> call, Response<TokenReceiver> response) {
                TokenReceiver tokenReceiver = response.body();
                token = new Token(tokenReceiver.token);
                Log.e("TOKEN CALL " + tokenReceiver.status, tokenReceiver.statusDesc);
                getPoints();
            }

            @Override
            public void onFailure(Call<TokenReceiver> call, Throwable t) {
                Log.e("TOKEN CALL FAILURE: ", t.getMessage());
            }
        });
    }

    public void getPoints() {


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        ChargePointApiService chargePointApiService = retrofit.create(ChargePointApiService.class);
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
                Log.e("FEATURE COLLECTION CALL FAILURE ", t.getMessage());
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
                    Log.d("markerClick ", "touched marker");
                    if (!marker.isInfoWindowShown()) {
                        Map<String, Object> locRequestMap = new HashMap<>();
                        locRequestMap.put("location_id", feature.properties.locationId);
                        locRequestMap.put("token", token.token);

                        Call<Root> locationInfoCall = chargePointApiService.getLocation(locRequestMap);
                        locationInfoCall.enqueue(new Callback<Root>() {
                            @Override
                            public void onResponse(Call<Root> call, Response<Root> response) {
                                Root location = response.body();
                                if (response.isSuccessful()) {
                                    Log.e("status", location.status);
                                    Log.e("statusDesc", location.statusDesc);

                                    if (null != location.loc) {
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                                        setMarkerInfo(marker, location.loc);
                                        updateBottomSheetInfo(location.loc);

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
                                Log.e("markerInfoCall", "complete");
                            }

                            @Override
                            public void onFailure(Call<Root> call, Throwable t) {
                                Log.e("markerInfoCall", t.getMessage());
                            }
                        });
                        return false;
                    } else {
                        marker.closeInfoWindow();
                        hideBottomSheet();
                        return true;
                    }
                }
            });
        });


        map.invalidate();


    }

    private void hideBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
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
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        Drawable pin = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin_foreground, null);
        marker.setIcon(pin);
        marker.setTextLabelFontSize(44);
        marker.setTitle("Charging station");
        marker.setId(feature.properties.locationId);
        map.getOverlays().add(marker);
        return marker;
    }

    public void setMarkerInfo(Marker marker, Locat location) {
        /*InfoWindow infoWindow = new InfoWindow(R.layout.info_window_layout, map) {
            @Override
            public void onOpen(Object item) {

            }

            @Override
            public void onClose() {

            }
        };
        marker.setInfoWindow(infoWindow);
        */

        marker.setTitle("[Κωδ.Σταθμου: " + location.id + "]");
        Drawable markerImage = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin_foreground, null);
        marker.setImage(markerImage);


        marker.setTextLabelBackgroundColor(R.color.design_default_color_secondary);


        marker.setSnippet("Όνομα σταθμού: " + location.name + "\n" +
                "Συντεταγμένες: [" + location.coordinates.longitude + " , " + location.coordinates.latitude + "]\n" +
                "Διεύθυνση: " + location.address + "\n" +
                "Πόλη: " + location.city + "\n" +
                "Τ.Κ.: " + location.postalCode + "\n" +
                "Χώρα: " + location.state + " " + location.country);

        if (location.operator != null && location.owner != null) {
            marker.setSubDescription("Διαχειριστής: " + location.operator.name + "\n" +
                    "Συντεταγμένες: [" + location.owner.name + "]\n");
        }


        //todo

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateBottomSheetInfo(Locat location) {
        /*float distance = Location.distanceBetween(location.coordinates.latitude,
                location.coordinates.longitude,
                );*/

        tv_title.setText(location.name);
        tv_type.setText(location.evses.get(0).connectors.get(0).standard);
        tv_id.setText(location.id);
        tv_country.setText(location.country);
        tv_city.setText(location.city);
        tv_postal.setText(location.postalCode);
        tv_address.setText(location.address);
        tv_long.setText(location.coordinates.longitude);
        tv_lat.setText(location.coordinates.latitude);

        //todo
        connectorList.clear();
        location.evses.forEach(evse -> {
            evse.connectors.forEach(connector -> {
                connectorList.add(connector);
            });
        });

        listView.setAdapter(listAdapter);
        //listView.setClickable(true);
    }


    private void moveCamera(Location location, float zoom) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        GeoPoint userLocation = new GeoPoint(location);
        Log.d("map:", "moveCamera: moving the camera to: lat: " + latitude + " long: " + longitude);
        mapController.animateTo(userLocation, (double) zoom, 1200L);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {

                Task<Location> location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e("Location", "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation == null) {
                                requestNewLocationData();
                            } else {
                                updateLocationOnMap(currentLocation);
                            }
                        } else {
                            Log.e("Location", "onComplete: current location is null");
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent getLocationPermission = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(getLocationPermission);
            }
        } else {
            // if permissions aren't available,request for permissions
            requestPermissions();
        }

    }

    private void updateLocationOnMap(Location location) {
        moveCamera(location, 15f);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        // Initializing LocationRequest object with appropriate methods
        //LocationRequest locationRequest = new LocationRequest();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Do something
            Intent goToSettings = new Intent(this, SettingsActivity.class);
            startActivity(goToSettings);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

       /* switch (item.getItemId()) {
            case R.id.action_settings:
                // Do something

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            finish();
        }
    }

}