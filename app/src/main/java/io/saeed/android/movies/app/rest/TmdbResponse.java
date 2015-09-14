package io.saeed.android.movies.app.rest;

import java.util.List;

/**
 * POJO representing generic response from TMDB Api.

 */
public class TmdbResponse<TmdbEntity> {
    private List<TmdbEntity> results;

    public List<TmdbEntity> getResults() {
        return results;
    }
}
