package io.saeed.android.movies.app.test.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import io.saeed.android.movies.app.data.MovieContract.MovieEntry;

/**
 * Tests Movie Content Provider.

 */
public class TestProvider extends AndroidTestCase {
    // brings the database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {
        ContentValues cv = TestDb.getMovieContentValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI,
                cv);
        long movieId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, cv);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieUri(movieId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, cv);
    }
}
