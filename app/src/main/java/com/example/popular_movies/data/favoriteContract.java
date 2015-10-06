package com.example.popular_movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by geekpro on 10/4/15.
 */
public class favoriteContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.favoritemovies.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //columns
        public static final String COLUMN_MOVIE_ID= "movie_id";

        public static final String COLUMN_MOVIE_POSTER = "poster";

        // Table name
        public static final String TABLE_NAME = "movie";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithId(String Movie_id) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_MOVIE_ID, Movie_id).build();
        }

        public static String getMovieId(Uri uri) {
            String dateString = uri.getLastPathSegment();
            if (null != dateString && dateString.length() > 0)
                return dateString;
            else
                return null;
        }

    }
}
