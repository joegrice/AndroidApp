package com.parliamentary.androidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.parliamentary.androidapp.adapters.MpCommonsDivisionsAdapter;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.helpers.NavigationHelper;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.MpParliamentProfile;
import com.parliamentary.androidapp.tasks.DownloadImageTask;
import com.parliamentary.androidapp.tasks.GetListMpCommonsDivisionsTask;
import com.parliamentary.androidapp.tasks.GetMpNameTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.AbsListView.OnScrollListener;

public class MpActivity extends AppCompatActivity implements OnScrollListener {

    private String TAG = MpActivity.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private ListView mpVotedList;
    private String postcode = "";
    private int pageNumber = 0;
    private MpCommonsDivisionsAdapter adapter;
    private HashMap<String, Long> favourites;
    private MpParliamentProfile mpParliamentProfile;
    private boolean mFlagOnScrollBeingProcessed;
    private CardView progressCardView;
    private TextView progressBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Initialise variables
        mpVotedList = findViewById(R.id.mpVotedList);
        progressCardView = findViewById(R.id.mpProgressCardView);
        progressBarText = findViewById(R.id.mpProgressBarText);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationHelper(this);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);
        ListView mainListView = findViewById(R.id.mpVotedList);
        mainListView.setOnScrollListener(this);

        // Update Ui
        getPostcodeFromDatabase();
    }

    private void getPostcodeFromDatabase() {
        progressCardView.setVisibility(View.VISIBLE);
        progressBarText.setText("Getting User Postcode...");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("postcode");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dataValue = dataSnapshot.getValue(String.class);
                if (dataValue != null && (!TextUtils.equals(dataValue, "null")) && (!TextUtils.isEmpty(dataValue))) {
                    postcode = dataValue;
                    getNewMP();
                } else {
                    askUserForPostCode();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void savePostcodetoDatabase() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("postcode");
        myRef.setValue(postcode);
    }

    private void updateContent() {
        pageNumber++;
        addMPCommonsDivisions();
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
                GenericTypeIndicator<HashMap<String, Long>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Long>>() {
                };
                if (dataSnapshot.getValue(genericTypeIndicator) == null) {
                    favourites = new HashMap<>();
                } else {
                    favourites = dataSnapshot.getValue(genericTypeIndicator);
                }
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
        progressCardView.setVisibility(View.VISIBLE);
        progressBarText.setText("Updating Mp Commons Divisions...");
        GetListMpCommonsDivisionsTask asyncTask = new GetListMpCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                adapter.addAll(commonsDivisions);
                progressCardView.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(mpParliamentProfile, favourites, pageNumber);
    }

    private void getMPCommonsDivisions() {
        progressBarText.setText("Getting Mp Commons Divisions...");
        GetListMpCommonsDivisionsTask asyncTask = new GetListMpCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                adapter = new MpCommonsDivisionsAdapter(MpActivity.this, firebaseAuth, commonsDivisions);
                ListView listView = mpVotedList;
                listView.setAdapter(adapter);
                progressCardView.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(mpParliamentProfile, favourites, pageNumber);
    }

    private void getNewMP() {
        progressBarText.setText("Getting Mp Name...");
        GetMpNameTask asyncTask = new GetMpNameTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                mpParliamentProfile = (MpParliamentProfile) output;
                ((TextView) findViewById(R.id.textViewMpName)).setText(mpParliamentProfile.Name);
                ((TextView) findViewById(R.id.text_commonsconstituency)).setText(mpParliamentProfile.CommonsConstituency);
                ((TextView) findViewById(R.id.text_commonsparty)).setText(mpParliamentProfile.CommonsParty);
                new DownloadImageTask((ImageView) findViewById(R.id.image_member))
                        .execute(mpParliamentProfile.MemberImg);
                getFavourites();
            }
        });
        asyncTask.execute(postcode);
    }

    private void askUserForPostCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enter your postcode:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPostcode(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void checkPostcode(String input) {
        if (input.matches("([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([AZa-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))[0-9][A-Za-z]{2})")) {
            postcode = input;
            savePostcodetoDatabase();
            getNewMP();
        } else {
            Toast.makeText(MpActivity.this, "Invalid postcode please check you typed it correctly.", Toast.LENGTH_SHORT).show();
            askUserForPostCode();
        }
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
