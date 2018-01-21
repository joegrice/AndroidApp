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
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.PartyVoteDetail;

import java.util.ArrayList;

public class CommonsDivisionsActivity extends AppCompatActivity {

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commons_divisions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        displayData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void displayData() {
        spinner.setVisibility(View.VISIBLE);
        CommonsDivision commonsDivision = getCommonsDivision();
        TextView title = (TextView) findViewById(R.id.text_cd_title);
        title.setText(commonsDivision.Title);
        setUpAyePieChart(commonsDivision);
        setUpNoPieChart(commonsDivision);
        spinner.setVisibility(View.GONE);
    }

    private CommonsDivision getCommonsDivision() {
        Intent intent = getIntent();
        String commonsDivisionString = intent.getStringExtra("commonsDivisionString");
        Gson gson = new Gson();
        CommonsDivision commonsDivision = gson.fromJson(commonsDivisionString, CommonsDivision.class);
        return commonsDivision;
    }

    public void setUpAyePieChart(CommonsDivision commonsDivision) {
        PieChart pieChart = (PieChart) findViewById(R.id.ayePieChart);
        ArrayList<PieEntry> ayePieEntries = getAyeValues(commonsDivision);
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

    public void setUpNoPieChart(CommonsDivision commonsDivision) {
        PieChart pieChart = (PieChart) findViewById(R.id.noPieChart);
        ArrayList<PieEntry> noPieEntries = getNoValues(commonsDivision);
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

    public ArrayList<PieEntry> getAyeValues(CommonsDivision commonsDivision) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (PartyVoteDetail partyVoteDetail : commonsDivision.partyVoteDetails) {
            PieEntry pieEntry = new PieEntry((float) partyVoteDetail.AyeVotes, partyVoteDetail.Name);
            pieEntries.add(pieEntry);
        }
        return pieEntries;
    }

    public ArrayList<PieEntry> getNoValues(CommonsDivision commonsDivision) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (PartyVoteDetail partyVoteDetail : commonsDivision.partyVoteDetails) {
            PieEntry pieEntry = new PieEntry((float) partyVoteDetail.NoVotes, partyVoteDetail.Name);
            pieEntries.add(pieEntry);
        }
        return pieEntries;
    }
}
