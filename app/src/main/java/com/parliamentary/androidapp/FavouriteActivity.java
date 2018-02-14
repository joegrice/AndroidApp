package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.parliamentary.androidapp.tasks.GetListFavouriteCommonsDivisionsTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.design.widget.BottomNavigationView.*;

public class FavouriteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private CardView progressCardView;
    private TextView progressBarText;
    private String TAG = FavouriteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        progressCardView = findViewById(R.id.favProgressCardView);
        progressBarText = findViewById(R.id.favProgressBarText);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationHelper(this);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navigation.getMenu().getItem(2).setChecked(true);

        getFavourites();
    }
    private void getFavourites() {
        progressCardView.setVisibility(View.VISIBLE);
        progressBarText.setText("Getting User Favourites...");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("favourites");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Long>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Long>>() {};
                HashMap<String, Long> favourites = dataSnapshot.getValue(genericTypeIndicator);
                getCommonsDivisions(favourites);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void getCommonsDivisions(HashMap<String, Long> favourites) {
        progressBarText.setText("Getting Commons Divisions...");
        GetListFavouriteCommonsDivisionsTask asyncTask = new GetListFavouriteCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                CommonsDivisionsAdapter adapter = new CommonsDivisionsAdapter(FavouriteActivity.this, firebaseAuth, commonsDivisions);
                ListView listView = findViewById(R.id.favouritesListView);
                listView.setAdapter(adapter);
                progressCardView.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(favourites);
    }
}
