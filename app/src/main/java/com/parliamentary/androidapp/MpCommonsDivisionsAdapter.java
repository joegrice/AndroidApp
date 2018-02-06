package com.parliamentary.androidapp;

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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.parliamentary.androidapp.models.CommonsDivision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jg413 on 13/01/2018.
 */

public class MpCommonsDivisionsAdapter extends ArrayAdapter<CommonsDivision> {

    private Context context;
    private String TAG;
    private List<CommonsDivision> commonsDivisions = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    public MpCommonsDivisionsAdapter(Context context, FirebaseAuth firebaseAuth, ArrayList<CommonsDivision> commonsDivisions) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.mp_list_item, parent, false);
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
        TextView title = convertView.findViewById(R.id.text_bill_title);
        TextView date = convertView.findViewById(R.id.text_bill_date);
        TextView ayes = convertView.findViewById(R.id.text_bill_ayes);
        TextView noes = convertView.findViewById(R.id.text_bill_noes);
        TextView mpVoteOutcome = convertView.findViewById(R.id.text_mpvote);
        ImageView favourite = convertView.findViewById(R.id.imageView_favourite);

        // Attach the click event handler
        convertView.findViewById(R.id.imageView_favourite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("favourites");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (commonsDivision.Favourite) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Long dataValue = data.getValue(Long.class);
                                if (dataValue.equals(commonsDivision.Id)) {
                                    data.getRef().removeValue();
                                    commonsDivision.Favourite = false;
                                    break;
                                }
                            }
                        } else {
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put(myRef.push().getKey(), commonsDivision.Id);
                            myRef.updateChildren(childUpdates);
                            commonsDivision.Favourite = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to remove value.", error.toException());
                    }
                });
            }
        });

        // TODO: Change HashMap to handle ints
        String ayeVotes = "Aye Votes: " + Integer.toString(commonsDivision.AyeVotes);
        String noVotes = "No Votes: " + Integer.toString(commonsDivision.NoVotes);

        // Populate the data into the template view using the data object
        title.setText(commonsDivision.Title);
        date.setText(commonsDivision.Date);
        ayes.setText(ayeVotes);
        noes.setText(noVotes);
        mpVoteOutcome.setText(commonsDivision.MpVote);
        if (commonsDivision.Favourite) {
            favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}