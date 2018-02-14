package com.parliamentary.androidapp.helpers;

import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parliamentary.androidapp.models.CommonsDivision;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jg413 on 14/02/2018.
 */

public class FavouriteOnClickListener implements View.OnClickListener {

    private String TAG;
    private FirebaseAuth firebaseAuth;
    private CommonsDivision commonsDivision;
    private ArrayAdapter<CommonsDivision> arrayAdapter;

    public FavouriteOnClickListener(String tag, FirebaseAuth firebaseAuth, CommonsDivision commonsDivision, ArrayAdapter<CommonsDivision> arrayAdapter) {
        TAG = tag;
        this.firebaseAuth = firebaseAuth;
        this.commonsDivision = commonsDivision;
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    public void onClick(View view) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //final DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("favourites");
        final DatabaseReference myRef = database.getReference("users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (commonsDivision.Favourite) {
                    for (DataSnapshot data : dataSnapshot.child(user.getUid()).child("favourites").getChildren()) {
                        Long dataValue = data.getValue(Long.class);
                        if (dataValue.equals(commonsDivision.Id)) {
                            data.getRef().removeValue();
                            commonsDivision.Favourite = false;
                            arrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else {
                    if (!dataSnapshot.child(user.getUid()).exists()) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Object> favourites = new HashMap<>();
                        favourites.put(myRef.child(user.getUid()).push().getKey(), commonsDivision.Id);
                        childUpdates.put("favourites", favourites);
                        myRef.child(user.getUid()).setValue(childUpdates);
                        commonsDivision.Favourite = true;
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(myRef.child(user.getUid()).child("favourites").push().getKey(), commonsDivision.Id);
                        myRef.updateChildren(childUpdates);
                        commonsDivision.Favourite = true;
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to remove value.", error.toException());
            }
        });
    }
}
