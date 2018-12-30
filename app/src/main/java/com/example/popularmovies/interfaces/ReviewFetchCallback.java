package com.example.popularmovies.interfaces;

import com.example.popularmovies.model.MovieReview;

import java.util.List;

public interface ReviewFetchCallback {
    void onFailure();

    void onSuccess(List<MovieReview> movieReviews);
}
