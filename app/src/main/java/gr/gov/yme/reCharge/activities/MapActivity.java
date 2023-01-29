package gr.gov.yme.reCharge.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.osmdroid.api.IMapController;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import gr.gov.yme.BuildConfig;
import gr.gov.yme.R;
import gr.gov.yme.reCharge.models.ChargePointMarker;
import gr.gov.yme.reCharge.models.feature.Feature;
import gr.gov.yme.reCharge.models.location.Image;
import gr.gov.yme.reCharge.network.receivers.FeatureReceiver;
import gr.gov.yme.reCharge.sqlite.CustomSQLiteHelper;
import gr.gov.yme.reCharge.util.EnableGPSDialogFragment;
import gr.gov.yme.reCharge.util.EvseItemAdapter;
import gr.gov.yme.reCharge.util.EvsePagerAdapter;
import gr.gov.yme.reCharge.util.FilterVariables;
import gr.gov.yme.reCharge.util.ImageAdapter;
import gr.gov.yme.reCharge.util.WrapContentLinearLayoutManager;
import gr.gov.yme.reCharge.models.location.ChargePointLocation;
import gr.gov.yme.reCharge.models.location.Evse;
import gr.gov.yme.reCharge.network.receivers.LocationSingleReceiver;
import gr.gov.yme.reCharge.network.BasicAuthInterceptor;
import gr.gov.yme.reCharge.network.BodyWithFilters;
import gr.gov.yme.reCharge.network.ChargePointApiService;
import gr.gov.yme.reCharge.network.Token;
import gr.gov.yme.reCharge.network.receivers.TokenReceiver;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity {
    private final String TAG = "MapActivity ";
    Context ctx;

    Location lastUserLocation;
    Marker lastMarker;
    private List<Double> currentMarkerCoordinates;
    boolean isFirstTime = true;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 100;
    /********************** Network Variables **********************/
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
    public TextView tv_provider;
    public TextView copyrightNoticeOSM;
    public ImageView ic_drag;
    public RecyclerView parentRecyclerViewItem;
    public RecyclerView imagesRecyclerViewItem;
    public ProgressBar spinner;
    public ConstraintLayout blur;


    public TabLayout tabLayout;
    public ViewPager2 viewPager;

    public List<Evse> evseList = new ArrayList<>();
    public EvseItemAdapter evseItemAdapter;
    public EvsePagerAdapter evsePagerAdapter;
    public List<Image> imageList = new ArrayList<>();
    public ImageAdapter imageAdapter;
    public FloatingActionButton navigate_button;
    public FrameLayout navigate_button1;
    public FrameLayout filter_button;
    public ImageButton disclaimer_button;
    public ImageButton gps_button;
    MapView map = null;
    ConstraintLayout sheetTopUnder;
    ConstraintLayout info_layout;
    RelativeLayout sheet_top;
    MaterialDialog pinLoading;
    MaterialDialog mapLoading;
    //Controllers
    BottomSheetBehavior<View> bottomSheetBehavior;
    IMapController mapController;
    RadiusMarkerClusterer markerClusterer;
    Bitmap clusterBackgroundBitmap;
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

    //Pins
    Drawable pin_selected;


    /******************* Inherited Methods *******************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: running");
        ctx = getApplicationContext();
        setContentView(R.layout.activity_map);


        findViews();
        //requestPermissions();
        runInitMethods();
        getToken();
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
        //Log.i(TAG, "Database added location with id: " + newRowId);
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        gps_button = findViewById(R.id.btn_gps);
        filter_button = findViewById(R.id.btn_filter);
        disclaimer_button = findViewById(R.id.btn_disclaimer);
        navigate_button = findViewById(R.id.fab_locate);
        info_layout = findViewById(R.id.info_layout);
        navigate_button1 = findViewById(R.id.fab_locate1);
        tv_title = findViewById(R.id.tv_title);
        tv_address_full = findViewById(R.id.tv_address_full);
        tv_address = findViewById(R.id.tv_address);
        tv_postal = findViewById(R.id.tv_postal);
        tv_country = findViewById(R.id.tv_country);
        tv_long = findViewById(R.id.tv_long);
        tv_lat = findViewById(R.id.tv_lat);
        tv_provider = findViewById(R.id.tv_provider);
        ic_drag = findViewById(R.id.ic_drag);
        sheetTopUnder = findViewById(R.id.constraint_1);
        //parentRecyclerViewItem = findViewById(R.id.parent_recyclerview);
        imagesRecyclerViewItem = findViewById(R.id.recycler_view_images);
        spinner = findViewById(R.id.progressBar1);
        blur = findViewById(R.id.blur1);
        blur.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);

        Log.d(TAG, "Loading Dialog Created");
        MaterialDialog.Builder mapLoadingBuilder = (MaterialDialog.Builder) new MaterialDialog.Builder(this)
                .setAnimation(R.raw.bucket_loading)
                .setMessage("Φόρτωση Σημείων");

        mapLoading = mapLoadingBuilder.build();
        pinLoading = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.three_dots_loading)
                .setMessage("Φόρτωση Τοποθεσίας")
                .build();


        pin_selected = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.pin_selected, null);
    }

    /******************* Initialization Methods *******************/
    private void runInitMethods() {
        initMap();
        initRetrofit();
        initBottomSheet();
        initToolbar();
        initRecyclerViews();
        initListeners();
    }

    private void initMap() {
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMaxZoomLevel(20d);
        map.setMinZoomLevel(5d);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        //create map controller
        mapController = map.getController();
        mapController.setCenter(new GeoPoint(37.9838, 23.7275));
        mapController.setZoom(10.0);

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
        rotationGestureOverlay.setEnabled(false);

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

        Drawable clusterBackgroundDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.circle_white, null);
        Canvas canvas = new Canvas();
        assert clusterBackgroundDrawable != null;
        clusterBackgroundBitmap = Bitmap.createBitmap(clusterBackgroundDrawable.getIntrinsicWidth(), clusterBackgroundDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(clusterBackgroundBitmap);
        clusterBackgroundDrawable.setBounds(0, 0, clusterBackgroundDrawable.getIntrinsicWidth(), clusterBackgroundDrawable.getIntrinsicHeight());
        clusterBackgroundDrawable.draw(canvas);

    }

    private void initRetrofit() {
        // TOKEN CLIENT
        //Network Credentials
        String USERNAME = BuildConfig.USERNAME;
        String PASSWORD = BuildConfig.PASSWORD;
        authorizationClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(USERNAME, PASSWORD))
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
        tokenRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(authorizationClient)
                .build();
        tokenChargePointApiService = tokenRetrofit.create(ChargePointApiService.class);


        //LOCATIONS CLIENT
        locationsClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
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
                    copyrightNoticeOSM.setAlpha(1f);
                    copyrightNoticeOSM.setClickable(true);
                    navigate_button.setAlpha(1f);
                    navigate_button.setClickable(true);
                    gps_button.setAlpha(1f);
                    gps_button.setClickable(true);
                    filter_button.setAlpha(1f);
                    filter_button.setClickable(true);
                    disclaimer_button.setAlpha(1f);
                    disclaimer_button.setClickable(true);
                    ic_drag.setImageDrawable(icDragUp);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ic_drag.setImageDrawable(icDragDown);
                    copyrightNoticeOSM.setAlpha(0f);
                    copyrightNoticeOSM.setClickable(false);
                    gps_button.setAlpha(0f);
                    gps_button.setClickable(false);
                    filter_button.setAlpha(0f);
                    filter_button.setClickable(false);
                    disclaimer_button.setAlpha(0f);
                    disclaimer_button.setClickable(false);
                    navigate_button.setClickable(false);
                    ic_drag.setImageDrawable(icDragDown);
                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    ic_drag.setImageDrawable(icDragUp);
                    copyrightNoticeOSM.setAlpha(1f);
                    copyrightNoticeOSM.setClickable(true);
                    navigate_button.setAlpha(1f);
                    navigate_button.setClickable(true);
                    filter_button.setAlpha(1f);
                    filter_button.setClickable(true);
                    disclaimer_button.setAlpha(1f);
                    disclaimer_button.setClickable(true);
                    gps_button.setAlpha(0f);
                    gps_button.setClickable(false);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    copyrightNoticeOSM.setAlpha(1f);
                    copyrightNoticeOSM.setClickable(true);
                    navigate_button.setAlpha(1f);
                    navigate_button.setClickable(true);
                    gps_button.setAlpha(1f);
                    gps_button.setClickable(true);
                    filter_button.setAlpha(1f);
                    filter_button.setClickable(true);
                    disclaimer_button.setAlpha(1f);
                    disclaimer_button.setClickable(true);
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
    }

    private void initRecyclerViews() {
        /*layoutManager1 = new WrapContentLinearLayoutManager(MapActivity.this, WrapContentLinearLayoutManager.HORIZONTAL, false);
        evseItemAdapter = new EvseItemAdapter(ctx, evseList);
        parentRecyclerViewItem.setAdapter(evseItemAdapter);
        parentRecyclerViewItem.setLayoutManager(layoutManager1);
        parentRecyclerViewItem.setNestedScrollingEnabled(false);*/

        layoutManager2 = new WrapContentLinearLayoutManager(MapActivity.this, WrapContentLinearLayoutManager.HORIZONTAL, false);
        imageAdapter = new ImageAdapter(imageList);
        imagesRecyclerViewItem.setLayoutManager(layoutManager2);
        imagesRecyclerViewItem.setAdapter(imageAdapter);


        evsePagerAdapter = new EvsePagerAdapter(this, evseList);

        // adding the adapter to viewPager2
        // to show the views in recyclerview
        viewPager.setAdapter(evsePagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(evseList.get(position).position);
        }).attach();

    }

    private void initListeners() {
        Log.i(TAG, "initListeners: initializing");

        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getLastLocation();
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

        disclaimer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDisclaimer = new Intent(ctx, DisclaimerActivity.class);
                startActivity(goToDisclaimer);
            }
        });

        sheet_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });

        sheetTopUnder.setOnClickListener(new View.OnClickListener() {
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

    /******************* ---------------------- *******************/

    public void getToken() {


//        mapLoading.show();
        spinner.setVisibility(View.VISIBLE);
        blur.setVisibility(View.VISIBLE);



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
                /*if (mapLoading != null) {
                    mapLoading.dismiss();
                }*/
                if (spinner.getVisibility() != View.GONE) {
                    spinner.setVisibility(View.GONE);
                    blur.setVisibility(View.GONE);
                }

            }

        });
    }

    public void getTokenWithFilters() {
        Log.d(TAG, "Loading Dialog Created");

//        mapLoading.show();
        spinner.setVisibility(View.VISIBLE);
        blur.setVisibility(View.VISIBLE);

        getGetTokenWithFiltersCall = tokenChargePointApiService.getToken();
        getGetTokenWithFiltersCall.enqueue(new Callback<TokenReceiver>() {

            @Override
            public void onResponse(Call<TokenReceiver> call, Response<TokenReceiver> response) {
                TokenReceiver tokenReceiver = response.body();
                assert tokenReceiver != null;
                newToken = new Token(tokenReceiver.token);
                Log.d("TOKEN CALL " + tokenReceiver.status, newToken.token);
                getFiltersFromView();
            }

            @Override
            public void onFailure(Call<TokenReceiver> call, Throwable t) {
                Log.e("TOKEN CALL FAILURE: ", t.getMessage());
                /*if (mapLoading != null) {
                    mapLoading.dismiss();
                }*/
                if (spinner.getVisibility() != View.GONE) {
                    spinner.setVisibility(View.GONE);
                    blur.setVisibility(View.GONE);
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

                /*if (mapLoading != null) {
                    mapLoading.dismiss();
                }*/
                if (spinner.getVisibility() != View.GONE) {
                    spinner.setVisibility(View.GONE);
                    blur.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<FeatureReceiver> call, Throwable t) {
                Log.e(TAG + " getPoints", t.getMessage());
                /*if (mapLoading != null) {
                    mapLoading.dismiss();
                }*/
                if (spinner.getVisibility() != View.GONE) {
                    spinner.setVisibility(View.GONE);
                    blur.setVisibility(View.GONE);
                }
            }
        });
    }

    public void getPointsFiltered(BodyWithFilters.CenterPointCoordinates centerPointCoordinates, String maxNumberOfResponsePoints, ArrayList<String> connectorTypes, ArrayList<String> powerTypes, String connectorFormat, String evseStatus) {
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
                    Toast.makeText(ctx, "Δεν βρέθηκαν σημεία με αυτά τα χαρακτηριστικά.", Toast.LENGTH_SHORT).show();
                    /*if (mapLoading != null) {
                        mapLoading.dismiss();
                    }*/
                    if (spinner.getVisibility() != View.GONE) {
                        spinner.setVisibility(View.GONE);
                        blur.setVisibility(View.GONE);
                    }
                    return;
                }
                try {
                    featureList = new ArrayList<>(featureReceiver.features);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                Log.i(TAG, "getPointsFiltered: getLocationsDetails: Success");

                Toast.makeText(ctx, "Βρέθηκαν " + featureList.size() + " σημεία", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Βρέθηκαν " + featureList.size() + " σημεία");
                setFeaturesOnMap(featureList, locationsChargePointApiService, token);
                /*if (mapLoading != null) {
                    mapLoading.dismiss();
                }*/
                if (spinner.getVisibility() != View.GONE) {
                    spinner.setVisibility(View.GONE);
                    blur.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<FeatureReceiver> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                /*if (mapLoading != null) {
                    mapLoading.dismiss();
                }*/
                if (spinner.getVisibility() != View.GONE) {
                    spinner.setVisibility(View.GONE);
                    blur.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setFeaturesOnMap(List<Feature> features, ChargePointApiService chargePointApiService, Token token) {
        Log.i(TAG, "setFeaturesOnMap:");

        markerClusterer = new RadiusMarkerClusterer(ctx);

        markerClusterer.setIcon(clusterBackgroundBitmap);
        markerClusterer.getTextPaint().setColor(Color.parseColor("#003575"));
        //markerClusterer.setMaxClusteringZoomLevel(15);
        markerClusterer.setRadius(200);
        map.getOverlays().add(markerClusterer);
        SQLiteDatabase database = new CustomSQLiteHelper(this).getReadableDatabase();
        //database.delete("location","*",null);
        database.execSQL("delete from " + "location");
        features.forEach(feature -> {
            ChargePointMarker marker = new ChargePointMarker(map, feature, ctx);
            markerClusterer.add(marker);
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
                                        //currLocation = location.loc;
                                        setMarkerInfo((ChargePointMarker) marker, feature);
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

    private void removeMarkers() {
        InfoWindow.closeAllInfoWindowsOn(map);
        map.getOverlays().forEach(overlay -> {
            if ((overlay instanceof Marker) || (overlay instanceof RadiusMarkerClusterer)) {
                map.getOverlays().remove(overlay);
            }
        });
    }

    public void setMarkerInfo(ChargePointMarker marker, Feature feature) {
        marker.updateIcon(feature, ctx);
        InfoWindow infoWindow = new InfoWindow(R.layout.info_window_layout_empty, map) {

            @Override
            public void onOpen(Object item) {
                Log.i(TAG, "Info Window Open: " + item.getClass());
                marker.setIcon(pin_selected);
            }


            @Override
            public void onClose() {
                Log.i(TAG, "Info Window Closed");
                marker.updateIcon(feature, ctx);
                bottomSheetBehavior.setHideable(true);
                hideBottomSheet();
            }
        };
        marker.setInfoWindow(infoWindow);
        bottomSheetBehavior.setHideable(false);
    }

    private void updateBottomSheetInfo(ChargePointLocation location) {
        tv_title.setText(location.name);
        tv_address_full.setText(String.format("%s , %s , %s", location.address, location.city, location.postalCode));
        tv_provider.setText(location.openApiLocation.companyCommercialName);
        tv_country.setText(location.country);
        tv_postal.setText(String.format("%s , %s", location.postalCode, location.city));
        tv_address.setText(location.address);
        tv_long.setText(location.coordinates.longitude);
        tv_lat.setText(location.coordinates.latitude);

        evseList.clear();
        for (int j = 0; j < location.evses.size(); j++) {
            location.evses.get(j).position = String.valueOf(j + 1);
            evseList.add(location.evses.get(j));
        }
        evsePagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0, false);


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
        FilterVariables filterVariables = FilterVariables.getInstance();

        filterVariables.type1 = pref.getBoolean(filterVariables.type1_Key, false);
        filterVariables.type2 = pref.getBoolean(filterVariables.type2_Key, false);
        filterVariables.type1ccs = pref.getBoolean(filterVariables.type1ccs_Key, false);
        filterVariables.type2ccs = pref.getBoolean(filterVariables.type2ccs_Key, false);
        filterVariables.chademo = pref.getBoolean(filterVariables.chademo_Key, false);
        filterVariables.schuko = pref.getBoolean(filterVariables.schuko_Key, false);
        filterVariables.tesla = pref.getBoolean(filterVariables.tesla_Key, false);
        filterVariables.dc = pref.getBoolean(filterVariables.dc_Key, false);
        filterVariables.ac1 = pref.getBoolean(filterVariables.ac1_Key, false);
        filterVariables.ac2 = pref.getBoolean(filterVariables.ac2_Key, false);
        filterVariables.ac2split = pref.getBoolean(filterVariables.ac2split_Key, false);
        filterVariables.ac3 = pref.getBoolean(filterVariables.ac3_Key, false);
        filterVariables.plug = pref.getString(filterVariables.plug_Key, "NORESTRICTION");
        filterVariables.status = pref.getString(filterVariables.status_Key, "NORESTRICTION");

        ArrayList<String> connectorFilterRestrictions = new ArrayList<String>();
        if (filterVariables.type1) {
            connectorFilterRestrictions.add("IEC_62196_T1");
        }
        if (filterVariables.type2) {
            connectorFilterRestrictions.add("IEC_62196_T2");
        }
        if (filterVariables.type1ccs) {
            connectorFilterRestrictions.add("IEC_62196_T1_COMBO");
        }
        if (filterVariables.type2ccs) {
            connectorFilterRestrictions.add("IEC_62196_T2_COMBO");
        }
        if (filterVariables.chademo) {
            connectorFilterRestrictions.add("CHADEMO");
        }
        if (filterVariables.schuko) {
            connectorFilterRestrictions.add("DOMESTIC_F");
        }
        if (filterVariables.tesla) {
            connectorFilterRestrictions.add("TESLA_S");
        }

        ArrayList<String> powerTypesFilterRestrictions = new ArrayList<String>();
        if (filterVariables.dc) {
            powerTypesFilterRestrictions.add("DC");
        }
        if (filterVariables.ac1) {
            powerTypesFilterRestrictions.add("AC_1_PHASE");
        }
        if (filterVariables.ac2) {
            powerTypesFilterRestrictions.add("AC_2_PHASE");
        }
        if (filterVariables.ac2split) {
            powerTypesFilterRestrictions.add("AC_2_PHASE_SPLIT");
        }
        if (filterVariables.ac3) {
            powerTypesFilterRestrictions.add("AC_3_PHASE");
        }


        ArrayList<Boolean> connectorFormatFilterPrefs = new ArrayList<Boolean>() {{
            add(filterVariables.socket);
            add(filterVariables.cable);
        }};
        ArrayList<String> connectorFormatFilterRestrictions = new ArrayList<String>() {{
            add("SOCKET");
            add("CABLE");
        }};

        ArrayList<String> evseStatusFilterRestrictions = new ArrayList<String>() {{
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

        BodyWithFilters.CenterPointCoordinates centerPointCoordinates = new BodyWithFilters.CenterPointCoordinates("", "");
        String maxNumberOfResponsePoints = "";

        String ConnectorFormat = "";
        if (Objects.equals(filterVariables.plug, "NORESTRICTION")) {
            ConnectorFormat = "";
        } else if (Objects.equals(filterVariables.plug, "SOCKET")) {
            ConnectorFormat = filterVariables.plug;
        } else if (Objects.equals(filterVariables.plug, "CABLE")) {
            ConnectorFormat = filterVariables.plug;
        }

        if (connectorFilterRestrictions.isEmpty()) {
            connectorFilterRestrictions = null;
        }
        if (powerTypesFilterRestrictions.isEmpty()) {
            powerTypesFilterRestrictions = null;
        }


        if (filterVariables.status.equals("NORESTRICTION")) {
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
                    filterVariables.status
            );
        }


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

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permissions granted");
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i(TAG, "getLastLocation(): Success");
                            if (location != null) {
                                lastUserLocation = location;
                                updateLocationOnMap(lastUserLocation);
                            } else if (!isLocationEnabled()){
                                EnableGPSDialogFragment enableGPSDialogFragment = new EnableGPSDialogFragment();
                                enableGPSDialogFragment.show(getSupportFragmentManager(),"EnableGPSDialog");
                            }
                        }
                    });
        } else {
            requestPermissions();
        }
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

    private void updateLocationOnMap(Location location) {

        moveCamera(location, map.getZoomLevelDouble());
    }

    private void moveCamera(Location location, Double zoom) {
        GeoPoint userLocation = new GeoPoint(location);
        mapController.animateTo(userLocation, zoom, 1200L);
    }
}