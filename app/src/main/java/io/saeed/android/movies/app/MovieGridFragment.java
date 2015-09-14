package io.saeed.android.movies.app;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.TextView;

import io.saeed.android.movies.app.adapters.MoviesAdapter;
import io.saeed.android.movies.app.data.MovieContract;
import io.saeed.android.movies.app.sync.MovieSyncAdapter;


/**
 * Movies list fragment.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SELECTED_MOVIE_KEY = "selected";
    protected GridView mMoviesGrid;

    private final int MOVIES_LOADER = 0;

    private MoviesAdapter mMoviesAdapter;

    // Defines whether auto select view in movie grid layout
    private boolean mAutoSelectMovie = false;

    private int mPosition = GridView.INVALID_POSITION;
    private String LOG_TAG = getClass().getSimpleName();

    private String mMovieQuery;

    /**
     * Movie column projection
     **/
    public static final String[] MOVIE_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavoriteEntry.TABLE_NAME + "." +
                    MovieContract.FavoriteEntry.COLUMN_FAVORED_AT,
            MovieContract.MovieEntry.COLUMN_SORT_ORDER
    };

    /**
     * Corresponding column indices
     **/
    public final static int _ID = 0;
    public final static int MOVIE_ID = 1;
    public final static int TITLE = 2;
    public final static int OVERVIEW = 3;
    public final static int POPULARITY = 4;
    public final static int VOTE_AVG = 5;
    public final static int VOTE_COUNT = 6;
    public final static int POSTER_PATH = 7;
    public final static int BACKDROP_PATH = 8;
    public final static int RELEASE_DATE = 9;
    public final static int FAVORED_AT = 10;
    public final static int SORT_ORDER = 11;

    // Initialy selected movie
    private Uri mInitialSelectedMovie;
    private MovieGridItemClickListener mMovieGridItemClickListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.MovieGridFragment,
                0, 0);
        mAutoSelectMovie = a.getBoolean(R.styleable.MovieGridFragment_autoSelectMovie, false);
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mMoviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movies_empty);
        mMoviesGrid.setEmptyView(emptyView);
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        // Set movie adapter filtering
        mMoviesAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                mMovieQuery = constraint.toString();
                getLoaderManager().restartLoader(MOVIES_LOADER, null, MovieGridFragment.this);
                return null;
            }
        });
        mMoviesGrid.setAdapter(mMoviesAdapter);
        mMovieGridItemClickListener = new MovieGridItemClickListener();
        mMoviesGrid.setOnItemClickListener(mMovieGridItemClickListener);
        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        Log.d(LOG_TAG, "Saved instance " + savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_MOVIE_KEY)) {
            // The gridview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_MOVIE_KEY);
        }
        return rootView;
    }

    private class MovieGridItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            if (cursor != null) {
                long movieId = cursor.getLong(MOVIE_ID);
                Uri movieUri = MovieContract.MovieEntry.buildMovieUri(movieId);
                ((Callback) getActivity()).onItemSelected(movieUri);
            }
            mPosition = position;
        }
    }

    public void setInitialSelectedMovie(Uri initialSelectedMovie) {
        mInitialSelectedMovie = initialSelectedMovie;
    }

    /**
     * Interface when item selected
     */
    public interface Callback {
        void onItemSelected(Uri movieUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Initial selection and selection arguments
        String selection;
        String[] selectionArgs = null;
        // Order by column
        String orderBy = MovieContract.MovieEntry.COLUMN_POPULARITY;
        // Get preferred sort order
        String sortOrder = Utility.getPreferredSortOrder(getActivity());
        if (sortOrder.equals(getString(R.string.pref_sort_favourite))) {
            selection = MovieContract.FavoriteEntry.TABLE_NAME + "." +
                    MovieContract.FavoriteEntry.COLUMN_FAVORED_AT + " IS NOT NULL";
        } else {
            if (sortOrder.equals(getString(R.string.pref_sort_popularity))) {
                orderBy = MovieContract.MovieEntry.COLUMN_POPULARITY;
            } else {
                orderBy = MovieContract.MovieEntry.COLUMN_VOTE_AVG;
            }
            selection = MovieContract.MovieEntry.COLUMN_SORT_ORDER + " = ?";
            selectionArgs = new String[]{sortOrder};
        }
        // Keep movie query if there is already selections
        if (null != mMovieQuery && !mMovieQuery.isEmpty()) {
            selection += " AND " + MovieContract.MovieEntry.COLUMN_TITLE + " LIKE LOWER(?)";
            if (selectionArgs != null) {
                String[] newSelectionArgs = new String[selectionArgs.length + 1];
                for (int i = 0; i < selectionArgs.length; i++) {
                    newSelectionArgs[i] = selectionArgs[i];
                }
                newSelectionArgs[newSelectionArgs.length - 1] = mMovieQuery + "%";
                selectionArgs = newSelectionArgs;
            } else {
                selectionArgs = new String[]{mMovieQuery + "%"};
            }
        }
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_PROJECTION,
                selection,
                selectionArgs,
                orderBy + " DESC");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_conn_status_key))) {
            updateEmptyView();
        } else if (key.equals(getString(R.string.pref_sort_key))) {
            // Reload the data
            Log.d(LOG_TAG, "Sort order changed. Reloading movies.");
            // Get sync sort order
            String sortOrder = sharedPreferences.getString(key, getString(R.string.pref_default_sort_order_value));
            Log.d(LOG_TAG, "Selected sort order " + sortOrder);
            if (!sortOrder.equals(getString(R.string.pref_sort_favourite))) {
                MovieSyncAdapter.syncImmediately(getActivity());
            }
            mPosition = GridView.INVALID_POSITION;
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }

    /**
     * Updates the empty list view with the contextually relevant information that the user
     * can use to determine why they aren't seeing the weather.
     */
    private void updateEmptyView() {
        if (mMoviesAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.movies_empty);
            if (null != tv) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_movie_list;
                @MovieSyncAdapter.ConnectionStatus int connStatus = Utility.getConnectionStatus(getActivity());
                switch (connStatus) {
                    case MovieSyncAdapter.CONNECTION_SYNC:
                        message = R.string.empty_movie_list_syncing;
                        break;
                    case MovieSyncAdapter.CONNECTION_SERVER_DOWN:
                        message = R.string.empty_movie_list_server_down;
                        break;
                    case MovieSyncAdapter.CONNECTION_SERVER_INVALID:
                        message = R.string.empty_movie_list_list_server_error;
                        break;
                    case MovieSyncAdapter.CONNECTION_UNKNOWN:
                        message = R.string.empty_movie_list_connection_unknown;
                        break;
                    case MovieSyncAdapter.CONNECTION_OK:
                        if (null != mMovieQuery && !mMovieQuery.isEmpty()) {
                            message = R.string.empty_movie_list_not_found;
                        }
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_movie_list_no_network;
                        }
                }
                tv.setText(message);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_MOVIE_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        updateEmptyView();
        if (data.getCount() > 0) {
            if (mPosition == GridView.INVALID_POSITION &&
                    null != mInitialSelectedMovie) {
                Cursor c = mMoviesAdapter.getCursor();
                int count = c.getCount();
                for (int i = 0; i < count; i++) {
                    c.moveToPosition(i);
                    if (c.getLong(MOVIE_ID) == ContentUris.parseId(mInitialSelectedMovie)) {
                        mPosition = i;
                        break;
                    }

                }
            }
            if (mPosition == GridView.INVALID_POSITION) mPosition = 0;
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mMoviesGrid.smoothScrollToPosition(mPosition);
            // Check if auto selection needed
            if (mAutoSelectMovie) {
                mMoviesGrid.setItemChecked(mPosition, true);
                Cursor c = mMoviesAdapter.getCursor();
                if (c.moveToPosition(mPosition)) {
                    final long movieId = c.getLong(MOVIE_ID);
                    // Android bug? http://stackoverflow.com/questions/22788684/can-not-perform-this-action-inside-of-onloadfinished
                    final int WHAT = 1;
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == WHAT) {
                                ((MainActivity) getActivity()).onItemSelected(MovieContract
                                        .MovieEntry.buildMovieUri(movieId));
                            }
                        }
                    };
                    if (mMovieQuery == null) {
                        handler.sendEmptyMessage(WHAT);
                    }

                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}
