package com.example.vesko.capstone.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.vesko.capstone.data.RestaurantContract.RestaurantEntry.TABLE_NAME;

public class RestaurantContentProvider extends ContentProvider {
    private RestaurantDbHelper mRestaurantDbHelper;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRestaurantDbHelper = new RestaurantDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mRestaurantDbHelper.getWritableDatabase();

        Uri returnUri; // URI to be returned
        long id = db.insert(TABLE_NAME, null, values);
        if ( id > 0 ) /*if id > -1 -> insert succeed*/
            returnUri = ContentUris.withAppendedId(RestaurantContract.RestaurantEntry.CONTENT_URI, id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null); /*notify update*/

        return returnUri;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mRestaurantDbHelper.getReadableDatabase();

        Cursor retCursor;

        retCursor =  db.query(TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mRestaurantDbHelper.getWritableDatabase();

        int tasksDeleted; // starts as 0

        String id = uri.getPathSegments().get(1);
        tasksDeleted = db.delete(TABLE_NAME, "restaurant_id=?", new String[]{id});

        if (tasksDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mRestaurantDbHelper.getWritableDatabase();
        int id=0;
        id= db.update(TABLE_NAME,values,selection,null);

        getContext().getContentResolver().notifyChange(uri, null);

        return id;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
