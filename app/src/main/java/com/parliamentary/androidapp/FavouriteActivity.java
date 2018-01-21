package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivision;

import java.util.ArrayList;
import java.util.HashMap;

public class FavouriteActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressBar spinner;
    private String TAG = FavouriteActivity.class.getSimpleName();
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            NavigationHelper navigationHelper = new NavigationHelper(FavouriteActivity.this);
            navigationHelper.onBottomNavigationViewClick(item);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        final FirebaseUser user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("database").child("users");

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(2).setChecked(true);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.w(TAG, data.toString());
                    if (data.getKey().equals(user.getUid())) {
                        Iterable<DataSnapshot> dataSnapshotIterator = data.getChildren();
                        HashMap<String, Long> favourites = (HashMap<String, Long>) dataSnapshotIterator.iterator().next().getValue();
                        getCommonsDivisions(favourites);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getCommonsDivisions(HashMap<String, Long> favourites) {
        spinner.setVisibility(View.VISIBLE);
        GetListFavouriteCommonsDivisionsTask asyncTask = new GetListFavouriteCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                CommonsDivisionsAdapter adapter = new CommonsDivisionsAdapter(FavouriteActivity.this, commonsDivisions);
                ListView listView = (ListView) findViewById(R.id.favouritesListView);
                listView.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(favourites);
    }
}
