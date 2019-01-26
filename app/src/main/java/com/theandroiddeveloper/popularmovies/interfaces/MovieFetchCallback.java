package com.theandroiddeveloper.popularmovies.interfaces;

import com.theandroiddeveloper.popularmovies.model.Movie;

import java.util.List;

public interface MovieFetchCallback {
    void onSuccess(List<Movie> movies, String activeFilter);

    void onFailure();
}
