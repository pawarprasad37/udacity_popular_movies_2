package com.example.popularmovies.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.popularmovies.Constant;
import com.example.popularmovies.R;
import com.example.popularmovies.Util;
import com.example.popularmovies.activity.MovieDetailsActivity;
import com.example.popularmovies.behavior.HomeScreenDataManager;
import com.example.popularmovies.model.Movie;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

public class HomeScreenMovieAdapter extends RecyclerView.Adapter {
    private Activity mActivity;
    private List<Movie> movies;

    public HomeScreenMovieAdapter(Activity mActivity, List<Movie> movies) {
        this.mActivity = mActivity;
        this.movies = movies;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(mActivity)
                .inflate(R.layout.home_screen_grid_item_layout, null);
        ViewHolder viewHolder = new ViewHolder(movies.get(i).getId(), rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        final Movie movie = movies.get(i);

        final ImageView imageView = ((ViewHolder) viewHolder).imageView;

        String localPosterPath = movie.getLocalPosterPath();
        boolean hasLocalPosterFile = (localPosterPath != null &&
                new File(localPosterPath).exists());

        Glide.with(mActivity)
                .asBitmap()
                .load(hasLocalPosterFile ? new File(movie.getLocalPosterPath()) :
                        movie.getPosterFullPath())
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        onBitmapReadyForDisplay((ViewHolder) viewHolder, movie, resource,
                                imageView);
                    }
                });
    }

    private void onBitmapReadyForDisplay(ViewHolder viewHolder, Movie movie, Bitmap resource,
                                         ImageView imageView) {
        if (((ViewHolder) viewHolder).getMovieId() != movie.getId()) {
            return;
        }

        float imgWidth = resource.getWidth();
        float imgHeight = resource.getHeight();

        float aspect = imgWidth / imgHeight;
        int deviceWidth = Util.getScreenWidth(mActivity);
        float itemWidth = deviceWidth / HomeScreenDataManager.NUMBER_OF_COLUMNS;
        float itemHeight = itemWidth / aspect;
        imageView.setLayoutParams(new FrameLayout.LayoutParams((int) itemWidth,
                (int) itemHeight));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private long movieId;
        private ImageView imageView;

        public ViewHolder(long movieId, @NonNull View itemView) {
            super(itemView);
            this.movieId = movieId;
            this.imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGridItemClicked(ViewHolder.this.getAdapterPosition());
                }
            });
        }

        public long getMovieId() {
            return movieId;
        }
    }

    private void onGridItemClicked(int adapterPosition) {
        Movie selectedMovie = movies.get(adapterPosition);

        Intent intent = new Intent(mActivity, MovieDetailsActivity.class);
        intent.putExtra(Constant.IntentExtra.SELECTED_MOVIE_JSON, new Gson().toJson(selectedMovie));
        mActivity.startActivity(intent);
    }
}
