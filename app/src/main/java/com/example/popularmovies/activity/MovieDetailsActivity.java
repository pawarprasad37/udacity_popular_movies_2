package com.example.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.popularmovies.AppExecutors;
import com.example.popularmovies.Constant;
import com.example.popularmovies.R;
import com.example.popularmovies.Util;
import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.interfaces.ImageSaveCallback;
import com.example.popularmovies.interfaces.ReviewFetchCallback;
import com.example.popularmovies.interfaces.TrailerFetchCallback;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieReview;
import com.example.popularmovies.model.MovieTrailer;
import com.example.popularmovies.network.NetworkManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        String selectedMoviewJson = getIntent()
                .getStringExtra(Constant.IntentExtra.SELECTED_MOVIE_JSON);
        if (selectedMoviewJson == null) {
            Toast.makeText(this, getString(R.string.movie_details_unavailable_error),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        movie = new Gson().fromJson(selectedMoviewJson, new TypeToken<Movie>() {
        }.getType());
        setTitle(movie.getTitle());

        populateUI(movie);

        fetchMovieData();

        checkFavouriteStatus();

        findViewById(R.id.ibtFav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMovieFavourite();
            }
        });
    }

    private void checkFavouriteStatus() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movie = AppDatabase.getInstance(getApplicationContext())
                        .getMovieDao()
                        .getMovieById(MovieDetailsActivity.this.movie.getId());
                if (movie == null) {
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageButton) findViewById(R.id.ibtFav))
                                    .setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                            R.mipmap.ic_star_empty));
                        }
                    });
                } else {
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageButton) findViewById(R.id.ibtFav))
                                    .setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                            R.mipmap.ic_star_filled));
                        }
                    });
                }
            }
        });
    }

    private void toggleMovieFavourite() {
        ((findViewById(R.id.ibtFav))).setEnabled(false);
        Toast.makeText(getApplicationContext(), getString(R.string.adding_to_favs),
                Toast.LENGTH_SHORT).show();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movie = AppDatabase.getInstance(getApplicationContext())
                        .getMovieDao()
                        .getMovieById(MovieDetailsActivity.this.movie.getId());
                if (movie == null) {
                    //add movie to DB
                    Util.saveImageForMovie(getApplicationContext(),
                            MovieDetailsActivity.this.movie, new ImageSaveCallback() {
                                @Override
                                public void onImageSaved(Movie movie) {
                                    onMoviePostedStored(movie);
                                }
                            });
                } else {
                    //remove movie from DB
                    File posterFile = new File(movie.getLocalPosterPath());
                    if (posterFile.exists()) {
                        posterFile.delete();
                    }
                    AppDatabase.getInstance(getApplicationContext())
                            .getMovieDao()
                            .removeMovieFromFavourites(movie);
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageButton) findViewById(R.id.ibtFav))
                                    .setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                            R.mipmap.ic_star_empty));
                            Toast.makeText(getApplicationContext(), getString(R.string.remove_from_fav_success),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            ((findViewById(R.id.ibtFav))).setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void onMoviePostedStored(Movie movie) {
        AppDatabase.getInstance(getApplicationContext())
                .getMovieDao()
                .addMovieToFavourites(movie);
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                ((ImageButton) findViewById(R.id.ibtFav))
                        .setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                                R.mipmap.ic_star_filled));
                Toast.makeText(getApplicationContext(), getString(R.string.add_to_fav_success),
                        Toast.LENGTH_SHORT)
                        .show();
                ((findViewById(R.id.ibtFav))).setEnabled(true);
            }
        });
    }

    private void fetchMovieData() {
        NetworkManager.pullMovieTrailers(movie.getId(), new TrailerFetchCallback() {
            @Override
            public void onFailure() {
                //ignore
            }

            @Override
            public void onSuccess(List<MovieTrailer> trailerList) {
                if (trailerList == null || trailerList.isEmpty() || isFinishing()) {
                    return;
                }
                findViewById(R.id.llTrailers).setVisibility(View.VISIBLE);
                displayTrailerItems(trailerList);
            }

            private void displayTrailerItems(List<MovieTrailer> trailerList) {
                if (trailerList == null || trailerList.isEmpty()) {
                    return;
                }
                LinearLayout llTrailerItems = findViewById(R.id.llTrailerItems);
                llTrailerItems.removeAllViews();
                for (final MovieTrailer movieTrailer : trailerList) {
                    View itemRootView = LayoutInflater.from(getApplicationContext())
                            .inflate(R.layout.movie_details_trailer_list_item, null);
                    TextView textView = itemRootView.findViewById(R.id.textView);
                    textView.setText(movieTrailer.getName());
                    itemRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = "http://www.youtube.com/watch?v=" + movieTrailer.getKey();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                        }
                    });
                    itemRootView.findViewById(R.id.ibtShare)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String uri = "http://www.youtube.com/watch?v=" + movieTrailer.getKey();
                                    Util.forwardText(getApplicationContext(), uri);
                                }
                            });
                    llTrailerItems.addView(itemRootView,
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        });

        NetworkManager.pullMovieReviews(movie.getId(), new ReviewFetchCallback() {
            @Override
            public void onFailure() {
                //ignore
            }

            @Override
            public void onSuccess(List<MovieReview> movieReviews) {
                if (movieReviews == null || movieReviews.isEmpty() || isFinishing()) {
                    return;
                }
                findViewById(R.id.llReviews).setVisibility(View.VISIBLE);
                displayReviewItems(movieReviews);
            }

            private void displayReviewItems(List<MovieReview> movieReviews) {
                if (movieReviews == null || movieReviews.isEmpty()) {
                    return;
                }
                LinearLayout llReviewItems = findViewById(R.id.llReviewItems);
                llReviewItems.removeAllViews();

                for (final MovieReview movieReview : movieReviews) {
                    View itemRootView = LayoutInflater.from(getApplicationContext())
                            .inflate(R.layout.movie_details_review_list_item, null);
                    TextView tvAuthor = itemRootView.findViewById(R.id.tvAuthor);
                    tvAuthor.setText(movieReview.getAuthor());
                    TextView tvReview = itemRootView.findViewById(R.id.tvReview);
                    tvReview.setText(movieReview.getContent());
                    llReviewItems.addView(itemRootView);

                    itemRootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(movieReview.getUrl())));
                        }
                    });
                }
            }
        });
    }

    private void populateUI(Movie movie) {
        String localPosterPath = movie.getLocalPosterPath();
        if (localPosterPath != null && new File(localPosterPath).exists()) {
            Glide.with(this)
                    .load(localPosterPath)
                    .into(((ImageView) findViewById(R.id.ivPoster)));
        } else {
            Glide.with(this)
                    .load(movie.getPosterFullPath())
                    .into(((ImageView) findViewById(R.id.ivPoster)));
        }
        ((TextView) findViewById(R.id.tvReleaseDate))
                .setText(movie.getReleaseDate());
        ((TextView) findViewById(R.id.tvVoteAverage))
                .setText(String.valueOf(movie.getVoteAverage()));
        ((TextView) findViewById(R.id.tvPlotSynopsis))
                .setText(movie.getOverview());
    }
}
