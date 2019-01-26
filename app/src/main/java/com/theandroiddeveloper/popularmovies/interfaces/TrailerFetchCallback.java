package com.theandroiddeveloper.popularmovies.interfaces;

import com.theandroiddeveloper.popularmovies.model.MovieTrailer;

import java.util.List;

public interface TrailerFetchCallback {
    public void onFailure();

    public void onSuccess(List<MovieTrailer> trailerList);
}
