package io.saeed.android.movies.app.rest;

import io.saeed.android.movies.app.models.Movie;
import io.saeed.android.movies.app.models.Review;
import io.saeed.android.movies.app.models.Trailer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Rest client implementation.

 */
public class RestClient {
    private final static String TMDB_API_KEY = "YOUR_API_KEY_HERE";


    private static TmdbService sService;

    private static RestClient sInstanse;
    private static String LOG_TAG = RestClient.class.getSimpleName();

    static {
        // Date serialization with GSON: http://stackoverflow.com/questions/6873020/gson-date-format
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd").create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new TmdbErrorHandler())
                .build();
        sService = restAdapter.create(TmdbService.class);
    }

    /**
     * Singleton for RestClient
     * @return the rest client instance
     */
    public static RestClient getsInstance() {
        if (sInstanse == null) {
            sInstanse = new RestClient();
        }
        return sInstanse;
    }

    /**
     * Gets list of movies by calling TMDB Api, sorted by sort order
     * @param sortOrder the sort order
     * @return list of {@link Movie}
     * @throws Throwable
     */
    public List<Movie> queryMovies(String sortOrder) throws Throwable {
        TmdbResponse<Movie> resp = sService.queryMovies(sortOrder, TMDB_API_KEY);
        return resp.getResults();
    }

    /**
     * Gets list of trailers for the movie by calling TMDB Api
     * @param movieId the movie id to get trailers for
     * @return list of {@link Trailer}
     * @throws Throwable
     */
    public List<Trailer> queryTrailers(long movieId) throws  Throwable {
        TmdbResponse<Trailer> resp = sService.queryTrailers(movieId, TMDB_API_KEY);
        return resp.getResults();
    }

    /**
     * Gets list of reviews for the movie by calling TMDB Api
     * @param movieId the movie id to get reviews for
     * @return list of {@link Review}
     * @throws Throwable
     */
    public List<Review> queryReviews(long movieId) throws Throwable {
        TmdbResponse<Review> resp = sService.queryReviews(movieId, TMDB_API_KEY);
        return resp.getResults();
    }
}
