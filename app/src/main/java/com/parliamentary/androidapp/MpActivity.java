package com.parliamentary.androidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.parliamentary.androidapp.tasks.GetCommonsDivisionsPageTask;
import com.parliamentary.androidapp.tasks.GetMpCommonsDivisionsTask;
import com.parliamentary.androidapp.tasks.GetMpNameTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private View mpProgressBar;
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
        mpProgressBar = findViewById(R.id.mpProgressBar);
        progressBarText = mpProgressBar.findViewById(R.id.progressBarText);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationHelper(this);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);
        ListView mainListView = findViewById(R.id.mpVotedList);
        mainListView.setOnScrollListener(this);

        // Update Ui
        getUserMpFromDatabase();
    }

    private void getUserMpFromDatabase() {
        mpProgressBar.setVisibility(View.VISIBLE);
        progressBarText.setText("Getting User Saved Mp...");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).child("mp").exists()) {
                    mpParliamentProfile = new MpParliamentProfile();
                    mpParliamentProfile.Name = dataSnapshot.child(user.getUid()).child("mp").child("name").getValue(String.class);
                    mpParliamentProfile.CommonsConstituency = dataSnapshot.child(user.getUid()).child("mp").child("commonsConstituency").getValue(String.class);
                    mpParliamentProfile.CommonsParty = dataSnapshot.child(user.getUid()).child("mp").child("commonsParty").getValue(String.class);
                    mpParliamentProfile.MemberImg = dataSnapshot.child(user.getUid()).child("mp").child("memberImg").getValue(String.class);
                    displayMP();
                    getFavourites(false);
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

    private void saveUserMpToDatabase() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(user.getUid()).child("mp").exists()) {
                    Map<String, Object> mp = new HashMap<>();
                    mp.put("name", mpParliamentProfile.Name);
                    mp.put("commonsConstituency", mpParliamentProfile.CommonsConstituency);
                    mp.put("commonsParty", mpParliamentProfile.CommonsParty);
                    mp.put("memberImg", mpParliamentProfile.MemberImg);
                    myRef.child(user.getUid()).child("mp").setValue(mp);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void updateContent() {
        pageNumber++;
        addMPCommonsDivisions();
    }

    private void getFavourites(final boolean update) {
        mpProgressBar.setVisibility(View.VISIBLE);
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
                getCommonsDivisionsPage(update);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void addMPCommonsDivisions() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mpProgressBar.setVisibility(View.VISIBLE);
        progressBarText.setText("Updating Mp Commons Divisions...");
        getFavourites(true);
    }

    private void getCommonsDivisionsPage(final boolean update) {
        progressBarText.setText("Getting Mp Commons Divisions...");
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
            GetMpCommonsDivisionsTask commonsDivisionTask = new GetMpCommonsDivisionsTask(new AsyncResponse() {
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
                commonsDivisionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, divisionUrl, favourites, mpParliamentProfile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayData(boolean update, JSONArray jsonArray, ArrayList<CommonsDivision> commonsDivisions) {
        if (jsonArray.length() == commonsDivisions.size()) {
            if (!update) {
                adapter = new MpCommonsDivisionsAdapter(getApplicationContext(), firebaseAuth, commonsDivisions);
                ListView listView = mpVotedList;
                listView.setAdapter(adapter);
                mpProgressBar.setVisibility(View.GONE);
            } else {
                adapter.addAll(commonsDivisions);
                mFlagOnScrollBeingProcessed = false;
                mpProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    private void getNewMP() {
        progressBarText.setText("Getting Mp Name...");
        GetMpNameTask asyncTask = new GetMpNameTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                mpParliamentProfile = (MpParliamentProfile) output;
                displayMP();
                saveUserMpToDatabase();
                getFavourites(false);
            }
        });
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, postcode);
    }

    private void displayMP() {
        findViewById(R.id.mpLinearLayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textViewMpName)).setText(mpParliamentProfile.Name);
        ((TextView) findViewById(R.id.text_commonsconstituency)).setText(mpParliamentProfile.CommonsConstituency);
        ((TextView) findViewById(R.id.text_commonsparty)).setText(mpParliamentProfile.CommonsParty);
        new DownloadImageTask((ImageView) findViewById(R.id.image_member))
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mpParliamentProfile.MemberImg);
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
