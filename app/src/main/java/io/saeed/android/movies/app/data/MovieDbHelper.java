package io.saeed.android.movies.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.saeed.android.movies.app.data.MovieContract.*;


/**
 * Helper to work with SQLite database.

 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "movie.db";

    private static MovieDbHelper sInstance;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton implementation
    public static MovieDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MovieDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold movie data
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL,  " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_SORT_ORDER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                // Provide uniqueness of external movie id
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_FAVORED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY (" + FavoriteEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + "), " +
                // Assure that just one movie with the same external id is favored
                " UNIQUE (" + FavoriteEntry._ID + ", "
                + FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        // TODO: Add foreign keys?
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_ID + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_ISO_639_1 + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_SIZE + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_SITE + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_KEY + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_NAME + " TEXT NOT NULL," +
                // Make sure only movie doesn't have duplicated trailers
                " UNIQUE (" + TrailerEntry.COLUMN_MOVIE_ID + ", "
                + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";
        // TODO: Add foreign keys?
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_URL + " TEXT NOT NULL," +
                // Make sure only movie doesn't have duplicated reviews
                " UNIQUE (" + ReviewEntry.COLUMN_MOVIE_ID + ", "
                + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
