package io.saeed.android.movies.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.saeed.android.movies.app.sync.MovieSyncAdapter;


public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback {
    public static final String MOVIEDETAILFRAGMENT_TAG = "movie_detail_tag";
    private Toolbar mToolbar;
    // Indicates if two pane mode should be used
    private boolean mTwoPane;

    // Sort orders
    private final int POPULARITY = 0;
    private final int HIGHEST_RATE = 1;
    private final int FAVOURITE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri movieUri = getIntent() != null ? getIntent().getData() : null;
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction
            if (savedInstanceState == null) {
                Fragment fragment;
                if (movieUri != null) {
                    Bundle args = new Bundle();
                    args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, movieUri);
                    fragment = MovieDetailFragment.newInstance(args);
                } else {
                    fragment = new MovieDetailFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        if (movieUri != null) {
            MovieGridFragment movieGridFragment = ((MovieGridFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.movie_grid_fragment));
            movieGridFragment.setInitialSelectedMovie(movieUri);
        }

        // make sure we've gotten an account created and we're syncing
        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, movieUri);
            Fragment fragment = MovieDetailFragment.newInstance(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            // Prepare sort order dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get currently selected sort order
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String currentOrder = sp.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_default_sort_order_value));
            int checkedItem = POPULARITY;
            if (currentOrder.equals(getString(R.string.pref_sort_rating))) {
                checkedItem = HIGHEST_RATE;
            } else if (currentOrder.equals(getString(R.string.pref_sort_favourite))) {
                checkedItem = FAVOURITE;
            }
            builder.setTitle(R.string.pref_sort_title)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setSingleChoiceItems(R.array.sort_by, checkedItem,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case POPULARITY:
                                            Utility.setSortOrder(getApplicationContext(),
                                                    getString(R.string.pref_sort_popularity));
                                            break;
                                        case HIGHEST_RATE:
                                            Utility.setSortOrder(getApplicationContext(),
                                                    getString(R.string.pref_sort_rating));
                                            break;
                                        case FAVOURITE:
                                            Utility.setSortOrder(getApplicationContext(),
                                                    getString(R.string.pref_sort_favourite));
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            });
            AlertDialog dialog = builder.create();
            // Display the dialog
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
