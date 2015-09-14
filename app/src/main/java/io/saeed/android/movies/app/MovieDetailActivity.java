package io.saeed.android.movies.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Movie detail activity.

 */
public class MovieDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, getIntent().getData());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, MovieDetailFragment.newInstance(arguments))
                    .commit();
        }
    }
}
