package com.parliamentary.androidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parliamentary.androidapp.models.MpVote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jg413 on 13/01/2018.
 */

public class MpVoteAdapter extends ArrayAdapter<MpVote> {

    private Context context;
    private List<MpVote> mpVoteList = new ArrayList<>();

    public MpVoteAdapter(Context context, ArrayList<MpVote> mpVotes) {
        super(context, 0, mpVotes);
        this.context = context;
        mpVoteList = mpVotes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mpvote_list_item, parent, false);
        }
        // Get the data item for this position
        MpVote mpVote = mpVoteList.get(position);

        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.text_bill_title);
        TextView date = convertView.findViewById(R.id.text_bill_date);
        TextView ayes = convertView.findViewById(R.id.text_bill_ayes);
        TextView noes = convertView.findViewById(R.id.text_bill_noes);
        TextView mpVoteOutcome = convertView.findViewById(R.id.text_mpvote);

        // TODO: Change HashMap to handle ints
        String ayeVotes = "Aye Votes: " + Integer.toString(mpVote.AyeVotes);
        String noVotes = "No Votes: " + Integer.toString(mpVote.NoVotes);

        // Populate the data into the template view using the data object
        title.setText(mpVote.Title);
        date.setText(mpVote.Date);
        ayes.setText(ayeVotes);
        noes.setText(noVotes);
        mpVoteOutcome.setText(mpVote.MpVote);

        // Return the completed view to render on screen
        return convertView;
    }
}