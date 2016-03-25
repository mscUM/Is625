package de.unima.is625;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ActivityEntryDataSource
{
    private static final String LOG_TAG = ActivityEntryDataSource.class.getSimpleName();
    private SQLiteDatabase db;
    private ActivityEntryDbHelper dbHelper;

    private String[] columns =
            {
                ActivityEntryDbHelper.COLUMN_ID,
                ActivityEntryDbHelper.COLUMN_ACT_NAME,
                ActivityEntryDbHelper.COLUMN_ACT_LOCATION,
                ActivityEntryDbHelper.COLUMN_ACT_RANGE
            };

    public ActivityEntryDataSource(Context context)
    {
        //create db helper
        dbHelper = new ActivityEntryDbHelper(context); //context = environment
    }

    public void open()
    {
        db = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public ActivityEntry createActivityEntry(String act_name, String act_location, int act_range)
            {
                ContentValues values = new ContentValues();
                values.put(ActivityEntryDbHelper.COLUMN_ACT_NAME, act_name);
                values.put(ActivityEntryDbHelper.COLUMN_ACT_LOCATION, act_location);
                values.put(ActivityEntryDbHelper.COLUMN_ACT_RANGE, act_range);
                long insertId = db.insert(ActivityEntryDbHelper.TABLE_ACTIVITY_ENTRY_LIST, null, values);

                Cursor cursor = db.query
                        (
                                ActivityEntryDbHelper.TABLE_ACTIVITY_ENTRY_LIST,
                                columns, ActivityEntryDbHelper.COLUMN_ID + "=" + insertId,
                                null, null, null, null
                        );
                cursor.moveToFirst();
                ActivityEntry activityEntry = cursorToActivityEntry(cursor);
                cursor.close();

                return activityEntry;
             }

    private ActivityEntry cursorToActivityEntry(Cursor cursor)
    {
        int idIndex = cursor.getColumnIndex(ActivityEntryDbHelper.COLUMN_ID);
        int idActName = cursor.getColumnIndex(ActivityEntryDbHelper.COLUMN_ACT_NAME);
        int idActLocation = cursor.getColumnIndex(ActivityEntryDbHelper.COLUMN_ACT_LOCATION);
        int idActRange = cursor.getColumnIndex(ActivityEntryDbHelper.COLUMN_ACT_RANGE);

        String act_mame= cursor.getString(idActName);
        String act_location = cursor.getString(idActLocation);
        int act_range = cursor.getInt(idActRange);
        long id = cursor.getLong(idIndex);

        ActivityEntry activityEntry = new ActivityEntry(act_mame, act_location, act_range, id);

        return activityEntry;
    }

    public List<ActivityEntry> getAllActivityEntries()
    {
        List<ActivityEntry> activityEntryList = new ArrayList<>();
        Cursor cursor = db.query(ActivityEntryDbHelper.TABLE_ACTIVITY_ENTRY_LIST,
                columns, null, null, null, null, null); //Search String = null -> all db instances are returned

        cursor.moveToFirst();
        ActivityEntry activityEntry;

        //read all instances & create ActivityEntry objects
        while(!cursor.isAfterLast())
        {
            activityEntry = cursorToActivityEntry(cursor);
            activityEntryList.add(activityEntry);
            Log.d(LOG_TAG, "ID: " + activityEntry.getId() + ", Inhalt: " + activityEntry.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return activityEntryList;
    }


    public void deleteActivityEntry(ActivityEntry activityEntry)
    {
        long id = activityEntry.getId();
        db.delete
                (
                        ActivityEntryDbHelper.TABLE_ACTIVITY_ENTRY_LIST,
                        ActivityEntryDbHelper.COLUMN_ID + "=" + id,
                        null
                );

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + activityEntry.toString());
    }


    public ActivityEntry updateActivityEntry(long id, String newActName, String newActLocation, int newActRange)
    {
        ContentValues values = new ContentValues();
        values.put(ActivityEntryDbHelper.COLUMN_ACT_NAME, newActName);
        values.put(ActivityEntryDbHelper.COLUMN_ACT_LOCATION, newActLocation);
        values.put(ActivityEntryDbHelper.COLUMN_ACT_RANGE, newActRange);

        db.update
                (
                    ActivityEntryDbHelper.TABLE_ACTIVITY_ENTRY_LIST,
                    values,
                    ActivityEntryDbHelper.COLUMN_ID + "=" + id,
                    null
                );

        Cursor cursor = db.query
                (
                        ActivityEntryDbHelper.TABLE_ACTIVITY_ENTRY_LIST,
                        columns, ActivityEntryDbHelper.COLUMN_ID + "=" + id, //search criteria
                        null, null, null, null
                );

        cursor.moveToFirst();
        ActivityEntry activityEntry = cursorToActivityEntry(cursor);
        cursor.close();

        return activityEntry;
    }

} //class ActivityEntryDataSource