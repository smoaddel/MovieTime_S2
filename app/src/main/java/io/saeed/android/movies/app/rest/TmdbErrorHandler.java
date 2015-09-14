package io.saeed.android.movies.app.rest;

import java.net.HttpURLConnection;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Error handler for the TMDB Api.

 */
public class TmdbErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return new UnauthorizedException(cause);
        } else {
            return new Throwable("Unknown server error.");
        }
    }
}
