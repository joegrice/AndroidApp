package com.parliamentary.androidapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivision;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private int pageNumber = 0;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            NavigationHelper navigationHelper = new NavigationHelper(MainActivity.this);
            navigationHelper.onBottomNavigationViewClick(item);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(0).setChecked(true);

        getListCommonsDivisions();
    }

    private void getListCommonsDivisions() {
        spinner.setVisibility(View.VISIBLE);
        GetListCommonsDivisionsTask asyncTask = new GetListCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                CommonsDivisionsAdapter adapter = new CommonsDivisionsAdapter(MainActivity.this, commonsDivisions);
                ListView listView = (ListView) findViewById(R.id.mainListView);
                listView.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(pageNumber);
    }
}