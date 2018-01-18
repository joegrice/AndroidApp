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
import com.parliamentary.androidapp.models.MpParliamentProfile;
import com.parliamentary.androidapp.models.MpVote;

import java.util.ArrayList;

public class MPActivity extends AppCompatActivity {

    private ListView mpVotedList;
    private ProgressBar spinner;
    private String m_Text = "";
    private MpParliamentProfile mpParliamentProfile;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            NavigationHelper navigationHelper = new NavigationHelper(MPActivity.this);
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

        GetUserPostCode();
    }

    private void GetMPCommonsDivisions() {
        spinner.setVisibility(View.VISIBLE);
        GetMpCommonsDivisionsVotesTask asyncTask = new GetMpCommonsDivisionsVotesTask(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ArrayList<MpVote> mpVotes = (ArrayList<MpVote>) output;
                MpVoteAdapter adapter = new MpVoteAdapter(MPActivity.this, mpVotes);
                ListView listView = mpVotedList;
                listView.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(mpParliamentProfile);
    }

    private void GetNewMP() {
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
                GetMPCommonsDivisions();
            }
        });
        asyncTask.execute(m_Text);
    }

    private void GetUserPostCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your postcode:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                GetNewMP();
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
