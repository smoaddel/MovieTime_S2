package io.saeed.android.movies.app.rest;

import retrofit.RetrofitError;

/**
 * Unauthorized exception for TMDB Api.

 */
public class UnauthorizedException extends Throwable {
    public UnauthorizedException(RetrofitError cause) {
        super(cause.getMessage());
    }
}
