package io.saeed.android.movies.app.rest;

import io.saeed.android.movies.app.models.Movie;
import io.saeed.android.movies.app.models.Review;
import io.saeed.android.movies.app.models.Trailer;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Represents service to access TMDB API.

 */
public interface TmdbService {
    /**
     * Queries for the popular movies
     * @return list of Movie
     */
    @GET("/discover/movie")
    TmdbResponse<Movie> queryMovies(
            @Query("sort_by") String sortBy, @Query("api_key") String apiKey) throws Throwable;

    @GET("/movie/{id}/videos")
    TmdbResponse<Trailer> queryTrailers(@Path("id") long movieId, @Query("api_key") String apiKey) throws Throwable;

    @GET("/movie/{id}/reviews")
    TmdbResponse<Review> queryReviews(@Path("id") long movieId, @Query("api_key") String apiKey) throws Throwable;
}
