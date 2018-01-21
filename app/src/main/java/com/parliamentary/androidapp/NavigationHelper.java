package com.parliamentary.androidapp;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

/**
 * Created by jg413 on 18/01/2018.
 */

public class NavigationHelper {

    Context context;

    public NavigationHelper(Context context) {
        this.context = context;
    }

    public void onBottomNavigationViewClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                context.startActivity(new Intent(context, ProfileActivity.class));
                break;
            case R.id.navigation_list:
                context.startActivity(new Intent(context, MainActivity.class));
                break;
            case R.id.navigation_mp:
                context.startActivity(new Intent(context, MpActivity.class));
                break;
            case R.id.navigation_favourite:
                context.startActivity(new Intent(context, FavouriteActivity.class));
                break;
        }
    }
}