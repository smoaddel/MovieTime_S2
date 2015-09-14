package io.saeed.android.movies.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import io.saeed.android.movies.app.MovieDetailFragment;
import io.saeed.android.movies.app.R;

/**
 * Adapter to represent list of movie reviews.

 */
public class ReviewsAdapter extends CursorAdapter {
    public ReviewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String reviewAuthor = cursor.getString(MovieDetailFragment.INDEX_REVIEW_AUTHOR);
        String reviewContent = cursor.getString(MovieDetailFragment.INDEX_REVIEW_CONTENT);
        viewHolder.mReviewAuthor.setText(context.getString(R.string.movie_review_author, reviewAuthor));
        viewHolder.mReviewContent.setText(Html.fromHtml(reviewContent));
    }

    class ViewHolder {
        TextView mReviewAuthor;
        TextView mReviewContent;

        ViewHolder(View view) {
            mReviewAuthor = (TextView) view.findViewById(R.id.movie_review_author);
            mReviewContent = (TextView) view.findViewById(R.id.movie_review_content);
        }
    }
}
