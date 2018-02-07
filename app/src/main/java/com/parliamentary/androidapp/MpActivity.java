package com.parliamentary.androidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.FavouriteCommonDivision;
import com.parliamentary.androidapp.models.MpParliamentProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MpActivity extends AppCompatActivity {

    private String TAG = MpActivity.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private ListView mpVotedList;
    private ProgressBar spinner;
    private String postcode = "";
    private int pageNumber = 0;
    private MpCommonsDivisionsAdapter adapter;
    private HashMap<String, Long> favourites;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MpParliamentProfile mpParliamentProfile;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            NavigationHelper navigationHelper = new NavigationHelper(MpActivity.this);
            navigationHelper.onBottomNavigationViewClick(item);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mpVotedList = (ListView) findViewById(R.id.mpVotedList);
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_mp_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        getUserPostCode();
    }

    private void refreshContent() {
        pageNumber++;
        addMPCommonsDivisions();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getFavourites() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("favourites");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Long>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Long>>() {};
                favourites = dataSnapshot.getValue(genericTypeIndicator);
                getMPCommonsDivisions();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void addMPCommonsDivisions() {
        spinner.setVisibility(View.VISIBLE);
        GetListMpCommonsDivisionsTask asyncTask = new GetListMpCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                adapter.addAll(commonsDivisions);
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(mpParliamentProfile, favourites, pageNumber);
    }

    private void getMPCommonsDivisions() {
        spinner.setVisibility(View.VISIBLE);
        GetListMpCommonsDivisionsTask asyncTask = new GetListMpCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                adapter = new MpCommonsDivisionsAdapter(MpActivity.this, firebaseAuth, commonsDivisions);
                ListView listView = mpVotedList;
                listView.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(mpParliamentProfile, favourites, pageNumber);
    }

    private void getNewMP() {
        spinner.setVisibility(View.VISIBLE);
        MpNameJsoup asyncTask = new MpNameJsoup(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                mpParliamentProfile = (MpParliamentProfile) output;
                ((TextView) findViewById(R.id.textViewMpName)).setText(mpParliamentProfile.Name);
                ((TextView) findViewById(R.id.text_commonsconstituency)).setText(mpParliamentProfile.CommonsConstituency);
                ((TextView) findViewById(R.id.text_commonsparty)).setText(mpParliamentProfile.CommonsParty);
                new DownloadImageTask((ImageView) findViewById(R.id.image_member))
                        .execute(mpParliamentProfile.MemberImg);
                spinner.setVisibility(View.GONE);
                getFavourites();
            }
        });
        asyncTask.execute(postcode);
    }

    private void getUserPostCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your postcode:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postcode = input.getText().toString();
                getNewMP();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
