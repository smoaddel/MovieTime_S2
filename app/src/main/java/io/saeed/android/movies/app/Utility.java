package io.saeed.android.movies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import io.saeed.android.movies.app.sync.MovieSyncAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Holds some utility methods.

 */
public class Utility {
    private static final String BASE_TRAILER_URL = "http://www.youtube.com/watch?v=";

    /**
     * Gets preferred sort order from user settings
     *
     * @param c the context to get SharedPreferences from
     * @return the preferred sort order
     */
    static public String getPreferredSortOrder(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_sort_key),
                c.getString(R.string.pref_default_sort_order_value));
    }

    /**
     * Formats a data according to format
     *
     * @param date   the date to format. If null, then null is returned.
     * @param format the provided format
     * @return string representation of date according to the provided format
     */
    static public String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * Gets the connection status
     *
     * @param context the Context to get Preferences from
     * @return the connection status
     */
    @SuppressWarnings("ResourceType")
    static public
    @MovieSyncAdapter.ConnectionStatus
    int getConnectionStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_conn_status_key),
                MovieSyncAdapter.CONNECTION_UNKNOWN);
    }

    /**
     * Returns true if the network is available or about to become available
     *
     * @param c Context used to the ConnectivityManager
     * @return whether network currently available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    /**
     * Gets movie rating according to max allowed stars
     *
     * @param c       the Context to get max stars from
     * @param voteAvg average vote
     * @return normalized rating
     */
    public static float getRating(Context c, float voteAvg) {
        int maxVote = 10;
        int maxStars = c.getResources().getInteger(R.integer.movie_rating_stars);
        return voteAvg * maxStars / maxVote;
    }

    /**
     * Gets movie release date from provided date string
     *
     * @param dateStr the date string
     * @return year as string
     */
    public static String getReleaseYear(String dateStr) {
        if (dateStr != null) {
            return dateStr.split("-")[0];
        }
        return null;
    }

    /**
     * Constructs trailer url by adding key to base trailer url
     *
     * @param key the key of trailer
     * @return trailer URL
     */
    public static String getTrailerUrl(String key) {
        return BASE_TRAILER_URL + key;
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     * http://blog.lovelyhq.com/setting-listview-height-depending-on-the-items/
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;

            float px = 320 * listView.getResources()
                    .getDisplayMetrics().density;

            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px,
                                View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets movie sorting order
     *
     * @param ctx            the Context to get SharedPreferences
     * @param sortOrderValue sorting order to be set
     */
    public static void setSortOrder(Context ctx, String sortOrderValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(ctx.getString(R.string.pref_sort_key), sortOrderValue);
        ed.commit();
    }
}
