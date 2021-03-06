package com.parliamentary.androidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.parliamentary.androidapp.CommonsDivisionsActivity;
import com.parliamentary.androidapp.R;
import com.parliamentary.androidapp.helpers.FavouriteOnClickListener;
import com.parliamentary.androidapp.models.CommonsDivision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jg413 on 13/01/2018.
 */

public class CommonsDivisionsAdapter extends ArrayAdapter<CommonsDivision> {

    private Context context;
    private String TAG;
    private List<CommonsDivision> commonsDivisions = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    public CommonsDivisionsAdapter(Context context, FirebaseAuth firebaseAuth, ArrayList<CommonsDivision> commonsDivisions) {
        super(context, 0, commonsDivisions);
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.commonsDivisions = commonsDivisions;
        TAG = context.getClass().getSimpleName();
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
        final ImageView favourite = convertView.findViewById(R.id.imageView_favourite_list);

        // Attach the click event handler
        FavouriteOnClickListener favouriteOnClickListener = new FavouriteOnClickListener(TAG, firebaseAuth, commonsDivision, this);
        convertView.findViewById(R.id.imageView_favourite_list).setOnClickListener(favouriteOnClickListener);

        // Add Vote Titles
        String ayeVotes = "Aye Votes: " + Integer.toString(commonsDivision.AyeVotes);
        String noVotes = "No Votes: " + Integer.toString(commonsDivision.NoVotes);

        // Populate the data into the template view using the data object
        title.setText(commonsDivision.Title);
        date.setText(commonsDivision.Date);
        ayes.setText(ayeVotes);
        noes.setText(noVotes);
        if (commonsDivision.Favourite) {
            favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}