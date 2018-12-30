package com.example.popularmovies.interfaces;

import com.example.popularmovies.model.MovieTrailer;

import java.util.List;

public interface TrailerFetchCallback {
    public void onFailure();

    public void onSuccess(List<MovieTrailer> trailerList);
}
