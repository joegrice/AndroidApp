package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import com.parliamentary.androidapp.tasks.GetCommonsDivisionTask;
import com.parliamentary.androidapp.tasks.GetCommonsDivisionsPageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        getFavourites(false);
    }

    private void updateContent() {
        pageNumber++;
        addListCommonsDivisions();
    }

    private void getFavourites(final boolean update) {
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
                getListCommonsDivisions(update);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getListCommonsDivisions(final boolean update) {
        progressBarText.setText("Getting Commons Divisions Page...");
        GetCommonsDivisionsPageTask asyncTask = new GetCommonsDivisionsPageTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                JSONArray commonsDivisions = (JSONArray) output;
                if (commonsDivisions != null) {
                    progressBarText.setText("Found Commons Divisions Page...");
                    getSingleCommonsDivisions(update, commonsDivisions);
                }
            }
        });
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pageNumber);
    }

    private void getSingleCommonsDivisions(final boolean update, final JSONArray jsonArray) {
        final ArrayList<CommonsDivision> commonsDivisions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            GetCommonsDivisionTask commonsDivisionTask = new GetCommonsDivisionTask(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    CommonsDivision commonsDivision = (CommonsDivision) output;
                    if (commonsDivision != null) {
                        int found = commonsDivisions.size();
                        progressBarText.setText("Commons Divisions Found: " + ++found);
                        commonsDivisions.add((CommonsDivision) output);
                        displayData(update, jsonArray, commonsDivisions);
                    }
                }
            });
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String _about = jsonObject.getString("_about");
                String[] aboutSplit = _about.split("/");
                String divisionUrl = "http://lda.data.parliament.uk/commonsdivisions/id/" + aboutSplit[aboutSplit.length - 1] + ".json";
                commonsDivisionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, divisionUrl, favourites);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayData(boolean update, JSONArray jsonArray, ArrayList<CommonsDivision> commonsDivisions) {
        if (jsonArray.length() == commonsDivisions.size()) {
            if (!update) {
                adapter = new CommonsDivisionsAdapter(getApplicationContext(), firebaseAuth, commonsDivisions);
                ListView listView = findViewById(R.id.mainListView);
                listView.setAdapter(adapter);
                mFlagOnScrollBeingProcessed = false;
                mainProgressBar.setVisibility(View.GONE);
            } else {
                adapter.addAll(commonsDivisions);
                mFlagOnScrollBeingProcessed = false;
                mainProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    private void addListCommonsDivisions() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mainProgressBar.setVisibility(View.VISIBLE);
        progressBarText.setText("Updating Commons Divisions...");
        getFavourites(true);
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