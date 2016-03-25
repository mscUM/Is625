package de.unima.is625;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ActivityEntryDbHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = ActivityEntryDbHelper.class.getSimpleName();

    public static final String DB_NAME = "activity_entry_list.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_ACTIVITY_ENTRY_LIST = "activity_entry_list";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ACT_NAME = "act_name";
    public static final String COLUMN_ACT_LOCATION = "act_location";
    public static final String COLUMN_ACT_RANGE = "act_range";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_ACTIVITY_ENTRY_LIST +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ACT_NAME + " TEXT NOT NULL, " +
                    COLUMN_ACT_LOCATION + " TEXT NOT NULL, " +
                    COLUMN_ACT_RANGE + " INTEGER NOT NULL);";


    public ActivityEntryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}