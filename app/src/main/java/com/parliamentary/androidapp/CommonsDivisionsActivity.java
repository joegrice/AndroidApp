package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivisionsInformation;
import com.parliamentary.androidapp.models.MpVote;
import com.parliamentary.androidapp.models.PartyVoteDetail;
import com.parliamentary.androidapp.models.Vote;

import java.util.ArrayList;

public class CommonsDivisionsActivity extends AppCompatActivity {

    Vote vote;
    CommonsDivisionsInformation commonsDivisionsInformation;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commons_divisions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (ProgressBar) findViewById(R.id.cdProgressBar);
        Intent intent = getIntent();
        String mpVoteString = intent.getStringExtra("mpVoteString");
        Gson gson = new Gson();
        vote = gson.fromJson(mpVoteString, MpVote.class);
        displayData();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void SetUpAyePieChart() {
        PieChart pieChart = (PieChart) findViewById(R.id.ayePieChart);
        ArrayList<PieEntry> ayePieEntries = new ArrayList<>();
        AddAyeValues(ayePieEntries);
        PieDataSet pieDataSet = new PieDataSet(ayePieEntries, "");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setCenterText("Breakdown of Aye Votes");
        pieChart.setCenterTextSize(14f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateY(3000);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.setVisibility(View.VISIBLE);
    }

    public void SetUpNoPieChart() {
        PieChart pieChart = (PieChart) findViewById(R.id.noPieChart);
        ArrayList<PieEntry> noPieEntries = new ArrayList<>();
        AddNoValues(noPieEntries);
        PieDataSet pieDataSet = new PieDataSet(noPieEntries, "");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setCenterText("Breakdown of No Votes");
        pieChart.setCenterTextSize(14f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateY(3000);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.setVisibility(View.VISIBLE);
    }

    public void AddAyeValues(ArrayList<PieEntry> ayePieEntries){
        for (PartyVoteDetail partyVoteDetail: commonsDivisionsInformation.partyVoteDetails) {
            PieEntry pieEntry = new PieEntry((float) partyVoteDetail.AyeVotes, partyVoteDetail.Name);
            ayePieEntries.add(pieEntry);
        }
    }

    public void AddNoValues(ArrayList<PieEntry> noPieEntries){
        for (PartyVoteDetail partyVoteDetail: commonsDivisionsInformation.partyVoteDetails) {
            PieEntry pieEntry = new PieEntry((float) partyVoteDetail.NoVotes, partyVoteDetail.Name);
            noPieEntries.add(pieEntry);
        }
    }

    private void displayData() {
        TextView title = (TextView) findViewById(R.id.text_cd_title);
        title.setText(vote.Title);

        MpCommonsDivisionsVote asyncTask = new MpCommonsDivisionsVote(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                commonsDivisionsInformation = (CommonsDivisionsInformation) output;
                SetUpAyePieChart();
                SetUpNoPieChart();
                spinner.setVisibility(View.GONE);
            }
        });
        asyncTask.execute(vote);
    }
}
