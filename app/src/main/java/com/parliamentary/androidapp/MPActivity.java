package com.parliamentary.androidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.MpParliamentProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class MPActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonListPage;
    private Button buttonMpPage;
    private Button buttonFavouritePage;
    private Button buttonProfilePage;
    private ListView mpVotedList;
    private ProgressBar spinner;

    String m_Text = "";
    MpParliamentProfile mpParliamentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp);

        buttonListPage = (Button) findViewById(R.id.buttonListPage);
        buttonMpPage = (Button) findViewById(R.id.buttonMpPage);
        buttonFavouritePage = (Button) findViewById(R.id.buttonFavouritePage);
        buttonProfilePage = (Button) findViewById(R.id.buttonProfilePage);
        mpVotedList = (ListView) findViewById(R.id.mpVotedList);
        spinner = (ProgressBar)findViewById(R.id.progressBar);

        buttonListPage.setOnClickListener(this);
        buttonMpPage.setOnClickListener(this);
        buttonFavouritePage.setOnClickListener(this);
        buttonProfilePage.setOnClickListener(this);

        GetUserPostCode();
        spinner.setVisibility(View.GONE);
    }

    private void GetMPCommonsDivisions() {
        spinner.setVisibility(View.VISIBLE);
        MpCommonsDivisionsVotes asyncTask = new MpCommonsDivisionsVotes(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                ListAdapter adapter = new SimpleAdapter(MPActivity.this, (ArrayList<HashMap<String, String>>) output,
                        R.layout.mpvote_list_item, new String[]{"title", "date", "ayes", "noes", "mpVote"},
                        new int[]{R.id.text_bill_title, R.id.text_bill_date, R.id.text_bill_ayes, R.id.text_bill_noes, R.id.text_mpvote});
                mpVotedList.setAdapter(adapter);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonProfilePage:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.buttonListPage:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
