package com.parliamentary.androidapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.MpParliamentProfile;

import java.util.ArrayList;

public class MpActivity extends AppCompatActivity {

    private ListView mpVotedList;
    private ProgressBar spinner;
    private String postcode = "";
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

        mpVotedList = (ListView) findViewById(R.id.mpVotedList);
        spinner = (ProgressBar) findViewById(R.id.progressBar);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);

        getUserPostCode();
    }

    private void getMPCommonsDivisions() {
        spinner.setVisibility(View.VISIBLE);
        GetListMpCommonsDivisionsTask asyncTask = new GetListMpCommonsDivisionsTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<CommonsDivision> commonsDivisions = (ArrayList<CommonsDivision>) output;
                MpVoteAdapter adapter = new MpVoteAdapter(MpActivity.this, commonsDivisions);
                ListView listView = mpVotedList;
                listView.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(mpParliamentProfile);
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
                getMPCommonsDivisions();
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
