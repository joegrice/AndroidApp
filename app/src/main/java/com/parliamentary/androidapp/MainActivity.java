package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.parliamentary.androidapp.adapters.CommonsDivisionsAdapter;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.helpers.NavigationHelper;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.tasks.GetListCommonsDivisionsTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import static android.widget.AbsListView.OnScrollListener;

public class MainActivity extends AppCompatActivity implements OnScrollListener {

    private String TAG = MpActivity.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private TextView progressBarText;
    private View mainProgressBar;
    private int pageNumber;
    private HashMap<String, Long> favourites;
    private CommonsDivisionsAdapter adapter;
    private boolean mFlagOnScrollBeingProcessed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Initialise variables
        mainProgressBar = findViewById(R.id.mainProgressBar);
        progressBarText = mainProgressBar.findViewById(R.id.progressBarText);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationHelper(this);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navigation.getMenu().getItem(0).setChecked(true);
        ListView mainListView = findViewById(R.id.mainListView);
        mainListView.setOnScrollListener(this);

        // Update Ui
        getFavourites();
    }

    private void updateContent() {
        pageNumber++;
        addListCommonsDivisions();
    }

    private void getFavourites() {
        mainProgressBar.setVisibility(View.VISIBLE);
        progressBarText.setText("Checking User Favourites...");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("favourites");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Long>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Long>>() {
                };
                if (dataSnapshot.getValue(genericTypeIndicator) == null) {
                    favourites = new HashMap<>();
                } else {
                    favourites = dataSnapshot.getValue(genericTypeIndicator);
                }
                getListCommonsDivisions();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getListCommonsDivisions() {
        progressBarText.setText("Getting Commons Divisions...");
        GetListCommonsDivisionsTask asyncTask = new GetListCommonsDivisionsTask(progressBarText, new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                adapter = new CommonsDivisionsAdapter(MainActivity.this, firebaseAuth, commonsDivisions);
                ListView listView = findViewById(R.id.mainListView);
                listView.setAdapter(adapter);
                mFlagOnScrollBeingProcessed = false;
                mainProgressBar.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(pageNumber, favourites);
    }

    private void addListCommonsDivisions() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mainProgressBar.setVisibility(View.VISIBLE);
        progressBarText.setText("Updating Commons Divisions...");
        GetListCommonsDivisionsTask asyncTask = new GetListCommonsDivisionsTask(progressBarText, new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                adapter.addAll(commonsDivisions);
                mFlagOnScrollBeingProcessed = false;
                mainProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        asyncTask.execute(pageNumber, favourites);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == 0 ||
                mFlagOnScrollBeingProcessed ||
                firstVisibleItem + visibleItemCount < totalItemCount) {
            return;
        }

        mFlagOnScrollBeingProcessed = true;
        updateContent();
    }
}