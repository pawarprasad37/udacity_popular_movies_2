package com.example.popularmovies.interfaces;

import com.example.popularmovies.model.Movie;

import java.util.List;

public interface MovieFetchCallback {
    void onSuccess(List<Movie> movies, String activeFilter);

    void onFailure();
}
