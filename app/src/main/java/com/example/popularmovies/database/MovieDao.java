package com.example.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.popularmovies.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAllMovies();

    @Insert
    void addMovieToFavourites(Movie movie);

    @Delete
    void removeMovieFromFavourites(Movie movie);

    @Query("SELECT * FROM movie WHERE id = :id")
    Movie getMovieById(long id);
}
