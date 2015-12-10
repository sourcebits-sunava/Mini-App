package com.sourcebits.footin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sunavar on 09/12/15.
 */
public class PlaceDatabaseHelper {
    private static final String TAG = PlaceDatabaseHelper.class.getSimpleName();

    // database configuration
    // if you want the onUpgrade to run then change the database_version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mydatabase.db";

    // table configuration
    private static final String TABLE_NAME = "Places_table";         // Table name
    private static final String PLACE_TABLE_COLUMN_ID = "_id";     // a column named "_id" is required for cursor
    private static final String PLACE_TABLE_COLUMN_NAME = "place_name";

    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;
    private SQLiteDatabase rdatabase;


    // but under the hood actually DatabaseOpenHelper class will perform database CRUD operations
    public PlaceDatabaseHelper(Context aContext) {

        openHelper = new DatabaseOpenHelper(aContext);
        database = openHelper.getWritableDatabase();
        rdatabase = openHelper.getReadableDatabase();
    }

    public void insertData (String aPlaceName) {

        // we are using ContentValues to avoid sql format errors

        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_TABLE_COLUMN_NAME, aPlaceName);

        database.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData () {

        String buildSQL = "SELECT * FROM " + TABLE_NAME;

        Log.d(TAG, "getAllData SQL: " + buildSQL);

        return rdatabase.rawQuery(buildSQL, null);
    }

    public void onDelete (String ID) {

        database.delete(TABLE_NAME, PLACE_TABLE_COLUMN_ID + " = ?",
                new String[]{ID});
        database.close();
    }


    // this DatabaseOpenHelper class will actually be used to perform database related operation

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            // Create your tables here

            String buildSQL = "CREATE TABLE " + TABLE_NAME + "( " + PLACE_TABLE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    PLACE_TABLE_COLUMN_NAME + " TEXT )";

            Log.d(TAG, "onCreate SQL: " + buildSQL);

            sqLiteDatabase.execSQL(buildSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // Database schema upgrade code goes here

            String buildSQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

            Log.d(TAG, "onUpgrade SQL: " + buildSQL);

            sqLiteDatabase.execSQL(buildSQL);       // drop previous table

            onCreate(sqLiteDatabase);               // create the table from the beginning
        }

    }
}
