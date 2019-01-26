package com.theandroiddeveloper.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.theandroiddeveloper.popularmovies.interfaces.ImageSaveCallback;
import com.theandroiddeveloper.popularmovies.model.Movie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Util {
    public static boolean isConnectedToInternet(Context mContext) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static int getScreenWidth(Activity mContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
        return displayMetrics.widthPixels;
    }

    public static void saveImageForMovie(final Context mContext, final Movie movie,
                                         final ImageSaveCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(mContext.getCacheDir() + File.separator +
                        System.currentTimeMillis() + ".jpg");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    Bitmap bitmap = getBitmapFromURL(movie.getPosterFullPath());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    bitmap.recycle();
                    movie.setLocalPosterPath(file.getPath());
                    callback.onImageSaved(movie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void forwardText(Context applicationContext, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chooserIntent = Intent.createChooser(intent,
                applicationContext.getString(R.string.title_forward_text_chooser_title));
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(chooserIntent);
    }
}
