package com.parliamentary.androidapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parliamentary.androidapp.models.CommonsDivision;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jg413 on 13/01/2018.
 */

public class CommonsDivisionsAdapter extends ArrayAdapter<CommonsDivision> {

    private Context context;
    private List<CommonsDivision> commonsDivisions = new ArrayList<>();

    public CommonsDivisionsAdapter(Context context, ArrayList<CommonsDivision> commonsDivisions) {
        super(context, 0, commonsDivisions);
        this.context = context;
        this.commonsDivisions = commonsDivisions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        // Get the data item for this position
        final CommonsDivision commonsDivision = commonsDivisions.get(position);

        // Attach the click event handler
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommonsDivisionsActivity.class);
                Gson gson = new Gson();
                String commonsDivisionString = gson.toJson(commonsDivision);
                intent.putExtra("commonsDivisionString", commonsDivisionString);
                context.startActivity(intent);
            }
        });

        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.bill_title);
        TextView date = convertView.findViewById(R.id.bill_date);
        TextView ayes = convertView.findViewById(R.id.vote_ayes);
        TextView noes = convertView.findViewById(R.id.vote_noes);

        // Add Vote Titles
        String ayeVotes = "Aye Votes: " + Integer.toString(commonsDivision.AyeVotes);
        String noVotes = "No Votes: " + Integer.toString(commonsDivision.NoVotes);

        // Populate the data into the template view using the data object
        title.setText(commonsDivision.Title);
        date.setText(commonsDivision.Date);
        ayes.setText(ayeVotes);
        noes.setText(noVotes);

        // Return the completed view to render on screen
        return convertView;
    }
}