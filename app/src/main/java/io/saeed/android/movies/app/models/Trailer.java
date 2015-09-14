package io.saeed.android.movies.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * The movie trailer pojo.

 */
public class Trailer {
    private String id;
    private String key;
    private String name;
    private String type;
    private String site;
    private int size;
    @SerializedName("iso_639_1")
    private String iso6391;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getIso6391() {
        return iso6391;
    }
}
