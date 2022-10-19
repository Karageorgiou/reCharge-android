package gr.gov.yme.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.MarkerClusterer;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import gr.gov.yme.R;
import gr.gov.yme.models.feature.Feature;
import gr.gov.yme.models.location.Image;
import gr.gov.yme.network.receivers.FeatureReceiver;
import gr.gov.yme.sqlite.CustomSQLiteHelper;
import gr.gov.yme.util.EvseItemAdapter;
import gr.gov.yme.util.ImageAdapter;
import gr.gov.yme.util.WrapContentLinearLayoutManager;
import gr.gov.yme.models.location.ChargePointLocation;
import gr.gov.yme.models.location.Evse;
import gr.gov.yme.network.receivers.LocationSingleReceiver;
import gr.gov.yme.network.BasicAuthInterceptor;
import gr.gov.yme.network.BodyWithFilters;
import gr.gov.yme.network.ChargePointApiService;
import gr.gov.yme.network.Token;
import gr.gov.yme.network.receivers.TokenReceiver;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity {
    private String TAG = "MapActivity ";
    Context ctx;

    ChargePointLocation currLocation;
    ChargePointLocation currentMarkerLocation;
    Location lastUserLocation;
    Marker lastMarker;
    private List<Double> currentMarkerCoordinates;
    boolean isFirstTime;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 44;
    /********************** Network Variables **********************/
    final String TOKEN_URL = "http://dev.e-research.gr/myfah/openApi/";
    final String BASE_URL = "https://electrokinisi.yme.gov.gr/myfah-api/openApi/";
    //Client Variables
    OkHttpClient authorizationClient;
    OkHttpClient locationsClient;
    BodyWithFilters bodyWithFilters;
    //Token Retrofit
    Retrofit tokenRetrofit;
    ChargePointApiService tokenChargePointApiService;
    Call<TokenReceiver> getTokenCall;
    Call<TokenReceiver> getGetTokenWithFiltersCall;
    //Locations Retrofit
    Retrofit locationsRetrofit;
    ChargePointApiService locationsChargePointApiService;
    Call<FeatureReceiver> getLocationsCall;
    //Filters Retrofit
    Retrofit filteredLocationsRetrofit;
    ChargePointApiService filteredLocationsChargePointApiService;
    Call<FeatureReceiver> getLocationsFilteredCall;
    //Network Credentials
    private String USERNAME = "user1234";
    private String PASSWORD = "2372a5d0-e2c4-4a66-a102-719f7843e57b";
    /********************** Filters Variables **********************/
    public static final String type1_Key = "key_type1";
    public static final String type2_Key = "key_type2";
    public static final String type1ccs_Key = "key_type1css";
    public static final String type2ccs_Key = "key_type2css";
    public static final String chademo_Key = "key_chademo";
    public static final String schuko_Key = "key_schuko";
    public static final String tesla_Key = "key_tesla";
    public static final String plug_Key = "key_plug";
    public static final String status_Key = "key_status";
    boolean filterValue_type1;
    boolean filterValue_type2;
    boolean filterValue_type1ccs;
    boolean filterValue_type2ccs;
    boolean filterValue_chademo;
    boolean filterValue_schuko;
    boolean filterValue_tesla;
    public static final String dc_Key = "key_dc";
    public static final String ac1_Key = "key_ac1";
    public static final String ac2_Key = "key_ac2";
    public static final String ac2split_Key = "key_ac2split";
    public static final String ac3_Key = "key_ac3";
    boolean filterValue_dc;
    boolean filterValue_ac1;
    boolean filterValue_ac2;
    boolean filterValue_ac2split;
    boolean filterValue_ac3;
    boolean filterValue_socket;
    boolean filterValue_cable;
    String filterValue_plug;
    String filterValue_status;
    /***************************************************************/
    WrapContentLinearLayoutManager layoutManager1;
    WrapContentLinearLayoutManager layoutManager2;
    public View bottomSheet;
    public Toolbar toolbar;
    public TextView tv_title;
    public TextView tv_address_full;
    public TextView tv_address;
    public TextView tv_postal;
    public TextView tv_country;
    public TextView tv_long;
    public TextView tv_lat;
    public TextView iw_type;
    public TextView iw_address;
    public TextView iw_status;
    public TextView copyrightNoticeOSM;
    public ImageView ic_drag;
    public RecyclerView parentRecyclerViewItem;
    public RecyclerView imagesRecyclerViewItem;
    public List<Evse> evseList = new ArrayList<>();
    public EvseItemAdapter evseItemAdapter;
    public List<Image> imageList = new ArrayList<>();
    public ImageAdapter imageAdapter;
    public FloatingActionButton gps_button;
    public FloatingActionButton navigate_button;
    public FloatingActionButton navigate_button1;
    public FloatingActionButton filter_button;
    MapView map = null;
    ConstraintLayout constraintLayout;
    ConstraintLayout info_layout;
    RelativeLayout sheet_top;
    MaterialDialog pinLoading;
    MaterialDialog mapLoading;
    //Controllers
    BottomSheetBehavior bottomSheetBehavior;
    IMapController mapController;
    RadiusMarkerClusterer markerClusterer;
    //Map Overlays
    MyLocationNewOverlay myLocationNewOverlay;
    RotationGestureOverlay rotationGestureOverlay;
    ScaleBarOverlay scaleBarOverlay;
    //Database
    //SQLiteDatabase database = new CustomSQLiteHelper(this).getReadableDatabase();

    //Other
    FusedLocationProviderClient mFusedLocationClient;
    List<Feature> featureList;
    Token token;
    Token newToken;

    /******************* Inherited Methods *******************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: running");
        ctx = getApplicationContext();
        setContentView(R.layout.activity_map);
        findViews();
        runInitMethods();
        isFirstTime = true;
        mapController.setCenter(new GeoPoint(37.9838, 23.7275));
        mapController.setZoom(10d);
        // ATTENTION: This was auto-generated to handle app links.
        /*Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: running");

        /*if (checkPermissions()) {
            getLastLocation();
        }*/
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, prefs);
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, String.valueOf(resultCode) + String.valueOf(requestCode));
        if (data != null) {
            String activityResult = data.getStringExtra("FROM_ACTIVITY");
            if (activityResult == null) {
                Log.d(TAG, "previousActivity: NULL");
                getToken();
            } else if (activityResult.equals("FILTERS")) {
                Log.d(TAG, "previousActivity: FILTERS");
                getTokenWithFilters();
            } else {
                Log.d(TAG, "previousActivity: ELSE");
                getToken();
            }
        } else {
            Log.d(TAG, "previousActivity: ELSE2");
            getTokenWithFilters();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: running");
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResults: running");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: running");
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            InfoWindow.closeAllInfoWindowsOn(map);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: running");
        super.onDestroy();
    }

    /******************* ------------------ *******************/
    private void saveLocationToDB(SQLiteDatabase database, String loc_id, String loc_title, String loc_adrress, Double loc_lat, Double loc_long) {
        ContentValues values = new ContentValues();
        values.put(CustomSQLiteHelper.LOCATION_COLUMN_ID, loc_id);
        values.put(CustomSQLiteHelper.LOCATION_COLUMN_TITLE, loc_title);
        values.put(CustomSQLiteHelper.LOCATION_COLUMN_ADRRESS, loc_adrress);
        values.put(CustomSQLiteHelper.LOCATION_COLUMN_LAT, loc_lat);
        values.put(CustomSQLiteHelper.LOCATION_COLUMN_LONG, loc_long);
        long newRowId = database.insert(CustomSQLiteHelper.LOCATION_TABLE_NAME, null, values);

        Log.i(TAG, "Database added location with id: " + newRowId);
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        gps_button = findViewById(R.id.btn_gps);
        filter_button = findViewById(R.id.btn_filter);
        //info_button = findViewById(R.id.btn_info);
        info_layout = findViewById(R.id.info_layout);
        navigate_button = findViewById(R.id.fab_locate);
        navigate_button1 = findViewById(R.id.fab_locate1);
        tv_title = findViewById(R.id.tv_title);
        tv_address_full = findViewById(R.id.tv_address_full);
        //tv_id = findViewById(R.id.tv_id);
        tv_address = findViewById(R.id.tv_address);
        tv_postal = findViewById(R.id.tv_postal);
        //tv_city = findViewById(R.id.tv_city);
        tv_country = findViewById(R.id.tv_country);
        tv_long = findViewById(R.id.tv_long);
        tv_lat = findViewById(R.id.tv_lat);
        ic_drag = findViewById(R.id.ic_drag);
        constraintLayout = findViewById(R.id.constraint_1);
        parentRecyclerViewItem = findViewById(R.id.parent_recyclerview);
        imagesRecyclerViewItem = findViewById(R.id.recycler_view_images);

        /*info = findViewById(R.id.info_win);
        iw_type = findViewById(R.id.iw_type);
        iw_address = findViewById(R.id.iw_address);
        iw_status = findViewById(R.id.iw_status);*/

        Log.d(TAG, "Loading Dialog Created");
        mapLoading = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.bucket_loading)
                .setMessage("Φόρτωση Σημείων")
                .build();
        pinLoading = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.three_dots_loading)
                .setMessage("Φόρτωση Τοποθεσίας")
                .build();

    }

    /******************* Initialization Methods *******************/
    private void runInitMethods() {
        initOSMDroid();
        initRetrofit();
        requestPermissions();
        init_map();
        getToken();
        initBottomSheet();
        initToolbar();
        initRecyclerViews();
        initListeners();
        initFilterActivity();
    }

    private void initOSMDroid() {
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMaxZoomLevel(20d);
        map.setMinZoomLevel(5d);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
    }

    private void initRetrofit() {
        // TOKEN CLIENT
        authorizationClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(USERNAME, PASSWORD))
                .connectTimeout(1,TimeUnit.MINUTES)
                .readTimeout(1,TimeUnit.MINUTES)
                .build();
        tokenRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(authorizationClient)
                .build();
        tokenChargePointApiService = tokenRetrofit.create(ChargePointApiService.class);


        //LOCATIONS CLIENT
        locationsClient = new OkHttpClient.Builder()
                .connectTimeout(1,TimeUnit.MINUTES)
                .readTimeout(1,TimeUnit.MINUTES)
                .build();
        locationsRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(locationsClient)
                .baseUrl(BASE_URL)
                .build();
        locationsChargePointApiService = locationsRetrofit.create(ChargePointApiService.class);

        filteredLocationsRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(locationsClient)
                .build();
        filteredLocationsChargePointApiService = filteredLocationsRetrofit.create(ChargePointApiService.class);
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
        //myLocationNewOverlay.enableFollowLocation();

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
        scaleBarOverlay.setAlignBottom(true);
        scaleBarOverlay.setEnableAdjustLength(true);
        scaleBarOverlay.setScaleBarOffset(
                displayMetrics.widthPixels / 20,
                displayMetrics.heightPixels - (displayMetrics.heightPixels / 12));
        map.getOverlays().add(scaleBarOverlay);

        //loading color
        map.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(R.color.light_blue);
        map.getOverlayManager().getTilesOverlay().setLoadingLineColor(R.color.white);

        map.getOverlayManager().add(new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                InfoWindow.closeAllInfoWindowsOn(map);
                return super.onSingleTapConfirmed(e, mapView);
            }
        });

        copyrightNoticeOSM = findViewById(R.id.tv_copy);
        copyrightNoticeOSM.setText(R.string.copyright);
        copyrightNoticeOSM.setClickable(true);
        copyrightNoticeOSM.setFocusable(true);
        copyrightNoticeOSM.setAlpha(1);
        copyrightNoticeOSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked copyright notice");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.openstreetmap.org/copyright"));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ctx.startActivity(browserIntent);
            }
        });

    }

    private void initBottomSheet() {
        // Bottom Sheet
        Drawable icDragUp = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drag_up, null);
        Drawable icDragDown = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drag_down, null);
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheet_top = findViewById(R.id.bottom_sheet_top);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHalfExpandedRatio(0.5f);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "bottomSheetBehavior " + "onStateChanged" + newState);
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (isFirstTime) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        isFirstTime = false;
                    }
                    navigate_button.setAlpha(1f);
                    navigate_button.setClickable(true);
                    gps_button.setAlpha(1f);
                    gps_button.setClickable(true);
                    filter_button.setAlpha(1f);
                    filter_button.setClickable(true);
                    ic_drag.setImageDrawable(icDragUp);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ic_drag.setImageDrawable(icDragDown);
                    gps_button.setAlpha(0f);
                    gps_button.setClickable(false);
                    filter_button.setAlpha(0f);
                    filter_button.setClickable(false);
                    navigate_button.setClickable(false);
                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    ic_drag.setImageDrawable(icDragDown);
                    navigate_button.setAlpha(1f);
                    navigate_button.setClickable(true);
                    filter_button.setAlpha(1f);
                    filter_button.setClickable(true);
                    gps_button.setAlpha(0f);
                    gps_button.setClickable(false);
                }
                //TODO use drag icons on top of each other and change alpha respectively
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                navigate_button.setAlpha((1 - slideOffset) / 2);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
    }

    private void initRecyclerViews() {
        layoutManager1 = new WrapContentLinearLayoutManager(MapActivity.this, WrapContentLinearLayoutManager.HORIZONTAL, false);
        evseItemAdapter = new EvseItemAdapter(evseList);
        parentRecyclerViewItem.setAdapter(evseItemAdapter);
        parentRecyclerViewItem.setLayoutManager(layoutManager1);

        layoutManager2 = new WrapContentLinearLayoutManager(MapActivity.this, WrapContentLinearLayoutManager.HORIZONTAL, false);
        imageAdapter = new ImageAdapter(imageList);
        imagesRecyclerViewItem.setLayoutManager(layoutManager2);
        imagesRecyclerViewItem.setAdapter(imageAdapter);
    }

    private void initListeners() {
        Log.i(TAG, "initListeners: initializing");

        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getLastLocation();
                updateLocationOnMap(lastUserLocation);
                map.setMapOrientation(0);
            }
        });

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFilters = new Intent(ctx, FiltersActivity.class);
                startActivityForResult(goToFilters, 1);
                //startActivity(goToFilters);


            }
        });

        sheet_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });

        navigate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarkerCoordinates.get(0) + "," + currentMarkerCoordinates.get(1));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        navigate_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarkerCoordinates.get(0) + "," + currentMarkerCoordinates.get(1));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void initFilterActivity() {
        /*sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);*/
    }


    /******************* ---------------------- *******************/

    public void getToken() {


        mapLoading.show();

        getTokenCall = tokenChargePointApiService.getToken();
        getTokenCall.enqueue(new Callback<TokenReceiver>() {

            @Override
            public void onResponse(Call<TokenReceiver> call, Response<TokenReceiver> response) {
                TokenReceiver tokenReceiver = response.body();
                token = new Token(tokenReceiver.token);
                Log.d("TOKEN CALL " + tokenReceiver.status, token.token);
                getPoints();
            }

            @Override
            public void onFailure(Call<TokenReceiver> call, Throwable t) {
                Log.e("TOKEN CALL FAILURE: ", t.getMessage());
                if(mapLoading!=null){
                    mapLoading.dismiss();
                }
            }

        });
    }

    public void getTokenWithFilters() {
        Log.d(TAG, "Loading Dialog Created");

        mapLoading.show();

        getGetTokenWithFiltersCall = tokenChargePointApiService.getToken();
        getGetTokenWithFiltersCall.enqueue(new Callback<TokenReceiver>() {

            @Override
            public void onResponse(Call<TokenReceiver> call, Response<TokenReceiver> response) {
                TokenReceiver tokenReceiver = response.body();
                assert tokenReceiver != null;
                newToken = new Token(tokenReceiver.token);
                Log.d("TOKEN CALL " + tokenReceiver.status, newToken.token);
                applyFilters();
            }

            @Override
            public void onFailure(Call<TokenReceiver> call, Throwable t) {
                Log.e("TOKEN CALL FAILURE: ", t.getMessage());
                if(mapLoading!=null){
                    mapLoading.dismiss();
                }
            }

        });
    }

    public void getPoints() {
        hideBottomSheet();
        removeMarkers();


        Log.i(TAG, "getPoints");
        getLocationsCall = locationsChargePointApiService.getLocations(token);
        getLocationsCall.enqueue(new Callback<FeatureReceiver>() {
            @Override
            public void onResponse(Call<FeatureReceiver> call, Response<FeatureReceiver> response) {
                Log.e(TAG, "getLocations: Success");
                assert response.body() != null;
                FeatureReceiver featureReceiver = new FeatureReceiver(response.body());

                featureList = new ArrayList<>(featureReceiver.features);

                setFeaturesOnMap(featureList, locationsChargePointApiService, token);

                if (mapLoading != null) {
                    mapLoading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<FeatureReceiver> call, Throwable t) {
                Log.e(TAG + " getPoints", t.getMessage());
                if(mapLoading!=null){
                    mapLoading.dismiss();
                }
            }
        });
    }

    public void getPointsFiltered(BodyWithFilters.CenterPointCoordinates centerPointCoordinates,
                                  String maxNumberOfResponsePoints,
                                  ArrayList<String> connectorTypes,
                                  ArrayList<String> powerTypes,
                                  String connectorFormat,
                                  String evseStatus) {
        hideBottomSheet();
        removeMarkers();
        Log.i(TAG, "getPointsFiltered");


        bodyWithFilters = new BodyWithFilters(token.token,
                centerPointCoordinates,
                maxNumberOfResponsePoints,
                connectorTypes,
                powerTypes,
                connectorFormat,
                evseStatus);

        getLocationsFilteredCall = filteredLocationsChargePointApiService.getPLocations(bodyWithFilters);
        getLocationsFilteredCall.enqueue(new Callback<FeatureReceiver>() {
            @Override
            public void onResponse(Call<FeatureReceiver> call, Response<FeatureReceiver> response) {
                assert response.body() != null;
                FeatureReceiver featureReceiver = new FeatureReceiver(response.body());

                if (featureReceiver.status.equals("error")) {
                    Log.e(TAG, featureReceiver.statusDesc);
                    Toast.makeText(ctx, "Αποτυχία εφαρμογής φίλτρων.  Φορτώνονται όλα τα σημεία.", Toast.LENGTH_SHORT).show();
                    getPoints();
                    return;
                }
                try {
                    featureList = new ArrayList<>(featureReceiver.features);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                Log.i(TAG, "getPointsFiltered: getLocationsDetails: Success");

                Toast.makeText(ctx, "Found " + featureList.size() + " points", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Found " + featureList.size() + " points");
                setFeaturesOnMap(featureList, locationsChargePointApiService, token);
                if(mapLoading!=null){
                    mapLoading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<FeatureReceiver> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                if(mapLoading!=null){
                    mapLoading.dismiss();
                }
            }
        });
    }

    @Deprecated
    public void setPointsOnMap(List<ChargePointLocation> locations, ChargePointApiService chargePointApiService, Token token) {
        Log.i(TAG, "setPointsOnMap:");

        markerClusterer = new RadiusMarkerClusterer(ctx);
        map.getOverlays().add(markerClusterer);

        locations.forEach(location -> {
            Marker marker = createMarker(
                    Double.parseDouble(location.coordinates.latitude),
                    Double.parseDouble(location.coordinates.longitude)
            );
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView map) {
                    currentMarkerLocation = location;
                    Log.d("markerClick ", "touched marker");
                    if (!marker.isInfoWindowShown()) {
                        Map<String, Object> locRequestMap = new HashMap<>();
                        locRequestMap.put("location_id", location.openApiLocation.locationId);
                        locRequestMap.put("token", token.token);

                        Call<LocationSingleReceiver> locationInfoCall = chargePointApiService.getLocation(locRequestMap);
                        locationInfoCall.enqueue(new Callback<LocationSingleReceiver>() {
                            @Override
                            public void onResponse(Call<LocationSingleReceiver> call, Response<LocationSingleReceiver> response) {
                                LocationSingleReceiver location = response.body();
                                if (response.isSuccessful()) {
                                    Log.e("status", location.status);
                                    Log.e("statusDesc", location.statusDesc);
                                    //Toast.makeText(ctx, "statusDesc: " + location.statusDesc, Toast.LENGTH_SHORT).show();
                                    if (null != location.loc) {
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        currLocation = location.loc;
                                        setMarkerInfo(marker);
                                        updateBottomSheetInfo(location.loc);
                                        try {
                                            GeoPoint center = new GeoPoint(Double.parseDouble(location.loc.coordinates.latitude), Double.parseDouble(location.loc.coordinates.longitude));
                                            mapController.animateTo(center);
                                            marker.showInfoWindow();

                                        } catch (NullPointerException e) {
                                            Log.e("EXCEPTION", "onClick: NullPointerException: " + e.getMessage());
                                        }
                                    }
                                }
                                Log.e("markerInfoCall", "complete");
                            }

                            @Override
                            public void onFailure(Call<LocationSingleReceiver> call, Throwable t) {
                                Log.e("markerInfoCall", t.getMessage());
                            }
                        });
                        return false;
                    } else {
                        marker.closeInfoWindow();
                        return true;
                    }
                }
            });
        });
        map.invalidate();
    }

    public void setFeaturesOnMap(List<Feature> features, ChargePointApiService chargePointApiService, Token token) {
        Log.i(TAG, "setFeaturesOnMap:");

        markerClusterer = new RadiusMarkerClusterer(ctx);
        //markerClusterer.setMaxClusteringZoomLevel(15);
        markerClusterer.setRadius(200);
        map.getOverlays().add(markerClusterer);
        SQLiteDatabase database = new CustomSQLiteHelper(this).getReadableDatabase();
        //database.delete("location","*",null);
        database.execSQL("delete from " + "location");
        features.forEach(feature -> {
            Marker marker = createMarker(
                    feature.geometry.coordinates.get(0),
                    feature.geometry.coordinates.get(1)
            );
            saveLocationToDB(database,
                    feature.properties.locationId,
                    feature.properties.locationName,
                    feature.properties.locationName,
                    feature.geometry.coordinates.get(0),
                    feature.geometry.coordinates.get(1));


            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView map) {
                    pinLoading.show();
                    currentMarkerCoordinates = feature.geometry.coordinates;
                    Log.d("markerClick ", "touched marker");
                    lastMarker = marker;
                    if (!marker.isInfoWindowShown()) {
                        Map<String, Object> locRequestMap = new HashMap<>();
                        locRequestMap.put("location_id", feature.properties.locationId);
                        locRequestMap.put("token", token.token);
                        Call<LocationSingleReceiver> locationInfoCall = chargePointApiService.getLocation(locRequestMap);
                        locationInfoCall.enqueue(new Callback<LocationSingleReceiver>() {
                            @Override
                            public void onResponse(Call<LocationSingleReceiver> call, Response<LocationSingleReceiver> response) {
                                LocationSingleReceiver location = response.body();
                                if (response.isSuccessful()) {
                                    assert location != null;
                                    Log.e("status", location.status);
                                    Log.e("statusDesc", location.statusDesc);
                                    if (null != location.loc) {
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        currLocation = location.loc;
                                        setMarkerInfo(marker);
                                        updateBottomSheetInfo(location.loc);
                                        try {
                                            GeoPoint center = new GeoPoint(Double.parseDouble(location.loc.coordinates.latitude), Double.parseDouble(location.loc.coordinates.longitude));
                                            mapController.animateTo(center);
                                            marker.showInfoWindow();

                                        } catch (NullPointerException e) {
                                            Log.e("EXCEPTION", "onClick: NullPointerException: " + e.getMessage());
                                        }
                                    }
                                }
                                Log.e("markerInfoCall", "complete");
                                pinLoading.dismiss();
                            }

                            @Override
                            public void onFailure(Call<LocationSingleReceiver> call, Throwable t) {
                                Log.e("markerInfoCall", t.getMessage());
                                pinLoading.dismiss();
                            }
                        });
                        return false;
                    } else {
                        marker.closeInfoWindow();
                        if (pinLoading != null) {
                            pinLoading.dismiss();
                        }
                        return true;
                    }
                }
            });
        });
        map.invalidate();
    }

    @NonNull
    private Marker createMarker(Double latitude, Double longitude) {
        //Make marker
        Marker marker = new Marker(map);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM - 0.2f);
        Drawable pin = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin_foreground, null);
        marker.setIcon(pin);
        marker.setTextLabelFontSize(44);
        marker.setId(latitude.toString() + longitude.toString());
        //todo
        markerClusterer.add(marker);
        return marker;
    }

    private void removeMarkers() {
        InfoWindow.closeAllInfoWindowsOn(map);
        map.getOverlays().forEach(overlay -> {
            if ((overlay instanceof Marker) || (overlay instanceof RadiusMarkerClusterer)) {
                map.getOverlays().remove(overlay);
            }
        });
    }

    public void setMarkerInfo(Marker marker) {
        Drawable pin = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin_foreground, null);
        Drawable pin_selected = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_pin_selected_foreground, null);


        InfoWindow infoWindow = new InfoWindow(R.layout.info_window_layout, map) {

            @Override
            public void onOpen(Object item) {

                Marker activeMarker = (Marker) item;
                iw_type = activeMarker.getInfoWindow().getView().findViewById(R.id.iw_type);
                iw_address = activeMarker.getInfoWindow().getView().findViewById(R.id.iw_address);
                iw_status = activeMarker.getInfoWindow().getView().findViewById(R.id.iw_status);

                String target;
                final String[] connectorTypes = new String[currLocation.evses.size()];
                AtomicInteger iterator = new AtomicInteger();
                currLocation.evses.forEach(evse -> {
                    evse.connectors.forEach(connector -> {
                        connectorTypes[iterator.get()] = connector.standard + " | ";
                        iterator.getAndIncrement();
                    });
                });
                List<String> values = new ArrayList<String>();
                for (String data : connectorTypes) {
                    if (data != null) {
                        values.add(data);
                    }
                }
                target = values.toString();


                // split the string by spaces in a
                String a[] = target.split(" ");

                String T1 = "IEC_62196_T1";
                String T2 = "IEC_62196_T2";
                String T1C = "IEC_62196_T1_COMBO";
                String T2C = "IEC_62196_T2_COMBO";
                String CHAD = "CHADEMO";
                String DOM = "DOMESTIC_F";


                // search for pattern in a
                int T1count = 0, T2count = 0, T1Ccount = 0, T2Ccount = 0, CHADcount = 0, DOMcount = 0;
                for (int i = 0; i < a.length; i++) {
                    // if match found increase count
                    if (T1.equals(a[i])) {
                        T1count++;
                    } else if (T2.equals(a[i])) {
                        T2count++;
                    } else if (T1C.equals(a[i])) {
                        T1Ccount++;
                    } else if (T2C.equals(a[i])) {
                        T2Ccount++;
                    } else if (CHAD.equals(a[i])) {
                        CHADcount++;
                    } else if (DOM.equals(a[i])) {
                        DOMcount++;
                    }
                }

                String types = "|";
                if (T1count > 0) {
                    types = types + T1count + "x Type 1" + " | ";
                }
                if (T2count > 0) {
                    types = types + T2count + "x Type 2" + " | ";
                }
                if (T1Ccount > 0) {
                    types = types + T1Ccount + "x Type 1 Combo" + " | ";
                }
                if (T2Ccount > 0) {
                    types = types + T2Ccount + "x Type 2 Combo" + " | ";
                }
                if (CHADcount > 0) {
                    types = types + CHADcount + "x Chademo" + " | ";
                }
                if (DOMcount > 0) {
                    types = types + DOMcount + "x Domestic" + " | ";
                }


                iw_type.setText(types);
                iw_address.setText(currLocation.address + " , " + currLocation.city + " , " + currLocation.postalCode);
                iw_status.setText(currLocation.evses.get(0).status);


                Log.i(TAG, "Info Window Open: " + item.getClass().toString());
                marker.setIcon(pin_selected);
            }


            @Override
            public void onClose() {
                Log.i(TAG, "Info Window Closed");
                marker.setIcon(pin);
                bottomSheetBehavior.setHideable(true);
                hideBottomSheet();
            }
        };
        marker.setInfoWindow(infoWindow);
        bottomSheetBehavior.setHideable(false);

    }


    private void updateBottomSheetInfo(ChargePointLocation location) {
        tv_title.setText(location.name);
        tv_address_full.setText(location.address + " , " + location.city + " , " + location.postalCode);
        tv_country.setText(location.country);
        tv_postal.setText(location.postalCode + " , " + location.city);
        tv_address.setText(location.address);
        tv_long.setText(location.coordinates.longitude);
        tv_lat.setText(location.coordinates.latitude);

        evseList.clear();
        for (int j = 0; j < location.evses.size(); j++) {
            evseList.add(location.evses.get(j));
            evseItemAdapter.notifyItemInserted(j);
        }

        imageList.clear();
        ViewGroup.LayoutParams params = imagesRecyclerViewItem.getLayoutParams();
        params.height = 0;
        imagesRecyclerViewItem.setLayoutParams(params);
        if (location.images != null) {
            for (int i = 0; i < location.images.size(); i++) {
                Log.v(TAG, "i: " + i);
                imageList.add(location.images.get(i));
                imageAdapter.notifyItemInserted(i);
            }
        }

    }

    public void getFiltersFromView() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

        filterValue_type1 = pref.getBoolean(type1_Key, false);
        filterValue_type2 = pref.getBoolean(type2_Key, false);
        filterValue_type1ccs = pref.getBoolean(type1ccs_Key, false);
        filterValue_type2ccs = pref.getBoolean(type2ccs_Key, false);
        filterValue_chademo = pref.getBoolean(chademo_Key, false);
        filterValue_schuko = pref.getBoolean(schuko_Key, false);
        filterValue_tesla = pref.getBoolean(tesla_Key, false);

        filterValue_dc = pref.getBoolean(dc_Key, false);
        filterValue_ac1 = pref.getBoolean(ac1_Key, false);
        filterValue_ac2 = pref.getBoolean(ac2_Key, false);
        filterValue_ac2split = pref.getBoolean(ac2split_Key, false);
        filterValue_ac3 = pref.getBoolean(ac3_Key, false);


        filterValue_plug = pref.getString(plug_Key, "NORESTRICTION");
        filterValue_status = pref.getString(status_Key, "NORESTRICTION");


        ArrayList<String> connectorFilterRestrictions = new ArrayList<String>();

        if (filterValue_type1) {
            connectorFilterRestrictions.add("IEC_62196_T1");
        }
        if (filterValue_type2) {
            connectorFilterRestrictions.add("IEC_62196_T2");
        }
        if (filterValue_type1ccs) {
            connectorFilterRestrictions.add("IEC_62196_T1_COMBO");
        }
        if (filterValue_type2ccs) {
            connectorFilterRestrictions.add("IEC_62196_T2_COMBO");
        }
        if (filterValue_chademo) {
            connectorFilterRestrictions.add("CHADEMO");
        }
        if (filterValue_schuko) {
            connectorFilterRestrictions.add("DOMESTIC_F");
        }
        if (filterValue_tesla) {
            connectorFilterRestrictions.add("TESLA_S");
        }


        ArrayList<String> powerTypesFilterRestrictions = new ArrayList<String>();

        if (filterValue_dc) {
            powerTypesFilterRestrictions.add("DC");
        }
        if (filterValue_ac1) {
            powerTypesFilterRestrictions.add("AC_1_PHASE");
        }
        if (filterValue_ac2) {
            powerTypesFilterRestrictions.add("AC_2_PHASE");
        }
        if (filterValue_ac2split) {
            powerTypesFilterRestrictions.add("AC_2_PHASE_SPLIT");
        }
        if (filterValue_ac3) {
            powerTypesFilterRestrictions.add("AC_3_PHASE");
        }


        ArrayList connectorFormatFilterPrefs = new ArrayList<Boolean>() {{
            add(filterValue_socket);
            add(filterValue_cable);
        }};
        ArrayList connectorFormatFilterRestrictions = new ArrayList<String>() {{
            add("SOCKET");
            add("CABLE");
        }};

        ArrayList evseStatusFilterRestrictions = new ArrayList<String>() {{
            add("AVAILABLE");
            add("BLOCKED");
            add("CHARGING");
            add("INOPERATIVE");
            add("OUTOFORDER");
            add("PLANNED");
            add("REMOVED");
            add("RESERVED");
            add("UNKNOWN");
        }};


        Log.v(TAG, "getFiltersFromView: \n" +
                filterValue_type1 + "\n" +
                filterValue_type2 + "\n" +
                filterValue_type1ccs + "\n" +
                filterValue_type2ccs + "\n" +
                filterValue_chademo + "\n" +
                filterValue_schuko + "\n" +
                filterValue_tesla + "\n" +
                filterValue_dc + "\n" +
                filterValue_ac1 + "\n" +
                filterValue_ac2 + "\n" +
                filterValue_ac2split + "\n" +
                filterValue_ac3 + "\n" +
                filterValue_socket + "\n" +
                filterValue_cable + "\n" +
                filterValue_status + "\n");


        BodyWithFilters.CenterPointCoordinates centerPointCoordinates = new BodyWithFilters.CenterPointCoordinates("", "");
        String maxNumberOfResponsePoints = "";

        String ConnectorFormat = "";
        if (filterValue_plug == "NORESTRICTION") {
            ConnectorFormat = "";
        } else if (filterValue_plug == "SOCKET") {
            ConnectorFormat = filterValue_plug;
        } else if (filterValue_plug == "CABLE") {
            ConnectorFormat = filterValue_plug;
        }

        if (connectorFilterRestrictions.isEmpty()) {
            connectorFilterRestrictions = null;
        }
        if (powerTypesFilterRestrictions.isEmpty()) {
            powerTypesFilterRestrictions = null;
        }


        if (filterValue_status.equals("NORESTRICTION")) {
            Log.d(TAG, "NOOO------------RESTRICTION");
            getPointsFiltered(
                    centerPointCoordinates,
                    maxNumberOfResponsePoints,
                    connectorFilterRestrictions,
                    powerTypesFilterRestrictions,
                    ConnectorFormat,
                    ""
            );
        } else {
            getPointsFiltered(
                    centerPointCoordinates,
                    maxNumberOfResponsePoints,
                    connectorFilterRestrictions,
                    powerTypesFilterRestrictions,
                    ConnectorFormat,
                    filterValue_status
            );
        }


    }

    private void applyFilters() {
        getFiltersFromView();

    }

    private void toggleBottomSheet() {
        Log.d(TAG, "ToggleBottomSheet");
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void hideBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                Task<Location> location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "getLastLocation onComplete: found location");
                            lastUserLocation = (Location) task.getResult();
                            if (lastUserLocation == null) {
                                requestNewLocationData();
                            }
                        } else {
                            Log.e(TAG, "getLastLocation onComplete: current location is null");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...  ", Toast.LENGTH_LONG).show();
                Intent getLocationPermission = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(getLocationPermission);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        Log.e(TAG, "requestNewLocationData: running");
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
            Log.e(TAG, "LocationCallback onLocationResult: success");
            //Location mLastLocation = locationResult.getLastLocation();
            //updateLocationOnMap(mLastLocation);
        }
    };

    private void updateLocationOnMap(Location location) {
        moveCamera(location, 15f);
    }

    private void moveCamera(Location location, float zoom) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        GeoPoint userLocation = new GeoPoint(location);
        Log.e("map:", "moveCamera: moving the camera to: lat: " + latitude + " long: " + longitude);
        mapController.animateTo(userLocation, (double) zoom, 1200L);
    }


}