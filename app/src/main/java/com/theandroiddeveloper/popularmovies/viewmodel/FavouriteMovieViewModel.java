package com.theandroiddeveloper.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.theandroiddeveloper.popularmovies.database.AppDatabase;
import com.theandroiddeveloper.popularmovies.model.Movie;

import java.util.List;

public class FavouriteMovieViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> favouriteMovieList;

    public FavouriteMovieViewModel(@NonNull Application application) {
        super(application);
        favouriteMovieList = AppDatabase.getInstance(application.getApplicationContext())
                .getMovieDao()
                .getAllMovies();
    }

    public LiveData<List<Movie>> getFavouriteMovieList() {
        return favouriteMovieList;
    }
}
