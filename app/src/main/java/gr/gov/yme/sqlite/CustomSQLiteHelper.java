package gr.gov.yme.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import gr.gov.yme.activities.MapActivity;

public class CustomSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Locations_DB";
    public static final String LOCATION_TABLE_NAME = "location";
    public static final String LOCATION_COLUMN_ID = "_loc_id";
    public static final String LOCATION_COLUMN_TITLE = "title";
    public static final String LOCATION_COLUMN_ADRRESS = "adrress";
    public static final String LOCATION_COLUMN_LAT = "latitude";
    public static final String LOCATION_COLUMN_LONG = "longitude";


    public static final String EVSE_TABLE_NAME = "evse";
    public static final String EVSE_COLUMN_LOC_ID = "_loc_id";
    public static final String EVSE_COLUMN_EVSE_ID = "_evse_id";


    public static final String CONNECTOR_TABLE_NAME = "connector";
    public static final String CONNECTOR_COLUMN_EVSE_ID = "_evse_id";
    public static final String CONNECTOR_COLUMN_CON_ID = "_con_id";
    public static final String CONNECTOR_COLUMN_V = "v";
    public static final String CONNECTOR_COLUMN_I = "i";
    public static final String CONNECTOR_COLUMN_P = "p";
    public static final String CONNECTOR_COLUMN_TYPE = "type";


    public CustomSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CustomSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public CustomSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVSE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONNECTOR_TABLE_NAME);
        db.execSQL("CREATE TABLE " + LOCATION_TABLE_NAME + " (" +
                LOCATION_COLUMN_ID + " TEXT PRIMARY KEY, " +
                LOCATION_COLUMN_TITLE + " TEXT, " +
                LOCATION_COLUMN_ADRRESS + " TEXT, " +
                LOCATION_COLUMN_LAT + " DOUBLE," +
                LOCATION_COLUMN_LONG + " DOUBLE" + ")");

        db.execSQL("CREATE TABLE " + EVSE_TABLE_NAME + " (" +
                EVSE_COLUMN_EVSE_ID + " TEXT PRIMARY KEY, " +
                EVSE_COLUMN_LOC_ID + " TEXT " +  ")");

        db.execSQL("CREATE TABLE " + CONNECTOR_TABLE_NAME + " (" +
                CONNECTOR_COLUMN_CON_ID + " TEXT PRIMARY KEY, " +
                CONNECTOR_COLUMN_EVSE_ID + " TEXT, " +
                CONNECTOR_COLUMN_V + " INT, " +
                CONNECTOR_COLUMN_I + " INT," +
                CONNECTOR_COLUMN_P + " INT," +
                CONNECTOR_COLUMN_TYPE + " DOUBLE" + ")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVSE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONNECTOR_TABLE_NAME);
        onCreate(db);
    }
}
