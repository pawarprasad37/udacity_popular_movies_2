package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class MovieTrailer {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("key")
    private String key;

    public MovieTrailer(String id, String name, String key) {
        this.id = id;
        this.name = name;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
