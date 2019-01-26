package com.theandroiddeveloper.popularmovies.interfaces;

import com.theandroiddeveloper.popularmovies.model.MovieReview;

import java.util.List;

public interface ReviewFetchCallback {
    void onFailure();

    void onSuccess(List<MovieReview> movieReviews);
}
