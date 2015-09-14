package io.saeed.android.movies.app.models;

import android.content.Context;

import io.saeed.android.movies.app.R;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * POJO representing the movie object.

 */
public class Movie {
    private final String BASE_IMG_PATH = "http://image.tmdb.org/t/p";

    private boolean adult;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private long id;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("original_title")
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("release_date")
    private Date releaseDate;
    @SerializedName("vote_average")
    private float voteAvg;
    @SerializedName("vote_count")
    private int voteCount;
    private String overview;
    private float popularity;

    public Movie() {

    }

    public boolean isAdult() {
        return adult;
    }

    public String getBackdropPath() {
        return BASE_IMG_PATH + "/w185" + backdropPath;
    }

    public long getId() {
        return id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Gets poster path with default size 185
     * @return poster path with default size
     */
    public String getPosterPath() {
        return getPosterPath("w185");
    }

    /**
     * Gets poster path with provided width
     * @param width the width of poster
     * @return poster url
     */
    public String getPosterPath(String width) {
        return BASE_IMG_PATH + "/" + width + posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public float getVoteAvg() {
        return voteAvg;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getOverview() {
        return overview;
    }

}
