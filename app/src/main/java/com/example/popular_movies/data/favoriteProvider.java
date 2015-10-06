package com.example.popular_movies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by geekpro on 10/4/15.
 */
public class favoriteProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private favoriteDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = favoriteContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI( authority, favoriteContract.PATH_MOVIE, MOVIE);
        matcher.addURI( authority, favoriteContract.PATH_MOVIE+"/#", MOVIE_WITH_ID);
        return matcher;
    }

    //movie.,movie_id = ?
    private static final String sMovieWithIDSelection =
            favoriteContract.MovieEntry.TABLE_NAME+
                    "." + favoriteContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String movieId = favoriteContract.MovieEntry.getMovieId(uri);

        String[] selectionArgs;
        String selection;

        if (movieId !=null) {
            selection = sMovieWithIDSelection;
            selectionArgs = new String[]{movieId};

            return mOpenHelper.getReadableDatabase().query(favoriteContract.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }else
            return null;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new favoriteDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        favoriteContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID:
            {
                retCursor = getMovieById(uri,projection,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return favoriteContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return favoriteContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(favoriteContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = favoriteContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        favoriteContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                String where = favoriteContract.MovieEntry.COLUMN_MOVIE_ID + " = " + favoriteContract.MovieEntry.getMovieId(uri);
                if (!selection.isEmpty()) {
                    where += " AND "+selection;
                }
                rowsDeleted = db.delete(favoriteContract.MovieEntry.TABLE_NAME,where,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
