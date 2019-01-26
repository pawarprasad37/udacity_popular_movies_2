package com.theandroiddeveloper.popularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.theandroiddeveloper.popularmovies.Constant;
import com.theandroiddeveloper.popularmovies.Util;
import com.theandroiddeveloper.popularmovies.behavior.HomeScreenDataManager;
import com.theandroiddeveloper.popularmovies.R;

public class MainActivity extends AppCompatActivity {
    private HomeScreenDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = new HomeScreenDataManager(this);

        if (savedInstanceState != null) {
            String activeFilter = savedInstanceState
                    .getString(Constant.InstanceState.ACTIVE_FILTER);
            selectFilter(activeFilter);
        } else {
            if (Util.isConnectedToInternet(this)) {
                dataManager.pullListWithPath(Constant.POPULAR_URL_PATH);
            } else {
                dataManager.displayFavourites();
            }
        }
    }

    private void selectFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            dataManager.pullListWithPath(Constant.POPULAR_URL_PATH);
            return;
        }
        if (getString(R.string.most_popular).contentEquals(filter)) {
            dataManager.pullListWithPath(Constant.POPULAR_URL_PATH);
        } else if (getString(R.string.highest_rated).contentEquals(filter)) {
            dataManager.pullListWithPath(Constant.TOP_RATED_URL_PATH);
        } else {
            dataManager.displayFavourites();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home_menu_filter) {
            onFilterClicked(findViewById(R.id.home_menu_filter));
        }
        return true;
    }

    private void onFilterClicked(View item) {
        if (HomeScreenDataManager.isFetching()) {
            return;
        }
        PopupMenu popupMenu = new PopupMenu(this, item);
        popupMenu.getMenu().add(getString(R.string.most_popular));
        popupMenu.getMenu().add(getString(R.string.highest_rated));
        popupMenu.getMenu().add(getString(R.string.favourites));
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().toString().contentEquals(getString(R.string.most_popular))) {
                    dataManager.pullListWithPath(Constant.POPULAR_URL_PATH);
                } else if (menuItem.getTitle().toString()
                        .contentEquals(getString(R.string.highest_rated))) {
                    dataManager.pullListWithPath(Constant.TOP_RATED_URL_PATH);
                } else {
                    //favourites selected
                    dataManager.displayFavourites();
                }
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String activeFilter = ((TextView) findViewById(R.id.tvActiveFilter))
                .getText()
                .toString();
        outState.putString(Constant.InstanceState.ACTIVE_FILTER, activeFilter);
    }
}
