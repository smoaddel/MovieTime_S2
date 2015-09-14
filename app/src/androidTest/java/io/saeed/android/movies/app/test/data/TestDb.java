package io.saeed.android.movies.app.test.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import io.saeed.android.movies.app.data.MovieContract.MovieEntry;
import io.saeed.android.movies.app.data.MovieDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Tests Movie DB.

 */
public class TestDb extends AndroidTestCase {
    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = MovieDbHelper.getInstance(mContext).getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        MovieDbHelper dbHelper = MovieDbHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieContentValues = getMovieContentValues();

        long movieId = db.insert(MovieEntry.TABLE_NAME, null, movieContentValues);
        assertTrue(movieId > 0);

        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        validateCursor(cursor, movieContentValues);
    }

    static ContentValues getMovieContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_ID, "356");
        cv.put(MovieEntry.COLUMN_LANGUAGE, "english");
        cv.put(MovieEntry.COLUMN_TITLE, "lorem");
        cv.put(MovieEntry.COLUMN_OVERVIEW, "lorem ipsum");
        cv.put(MovieEntry.COLUMN_POSTER_PATH, "/poster.jpg");
        cv.put(MovieEntry.COLUMN_BACKDROP_PATH, "/backdrop.jpg");
        cv.put(MovieEntry.COLUMN_VOTE_AVG, 1.4);
        cv.put(MovieEntry.COLUMN_VOTE_COUNT, 10);
        cv.put(MovieEntry.COLUMN_POPULARITY, 1.46);
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, "2015-06-09");
        return cv;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
