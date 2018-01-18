package com.parliamentary.androidapp;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivisionsInformation;
import com.parliamentary.androidapp.models.PartyVoteDetail;
import com.parliamentary.androidapp.models.Vote;
import com.parliamentary.androidapp.models.VoteOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jg413 on 12/01/2018.
 */

public class MpCommonsDivisionsVote extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate = null;
    CommonsDivisionsInformation commonsDivisionsInformation;

    public MpCommonsDivisionsVote(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpHandler sh = new HttpHandler();
        commonsDivisionsInformation = new CommonsDivisionsInformation();
        commonsDivisionsInformation.partyVoteDetails = new ArrayList<>();
        Vote vote = (Vote) objects[0];
        String jsonStr = sh.makeServiceCall(vote.CommonsDivisionsUrl);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject result = jsonObj.getJSONObject("result");
                JSONObject primaryTopic = result.getJSONObject("primaryTopic");
                JSONArray ayesCountArr = primaryTopic.getJSONArray("AyesCount");
                JSONObject ayesCountObj = ayesCountArr.getJSONObject(0);
                String ayesCount = "Ayes: " + ayesCountObj.getString("_value");
                String[] ayesCountVal = ayesCount.split(": ");
                commonsDivisionsInformation.AyeVotes = Integer.parseInt(ayesCountVal[1]);
                JSONArray noesCountArr = primaryTopic.getJSONArray("Noesvotecount");
                JSONObject noesCountObj = noesCountArr.getJSONObject(0);
                String noesCount = "Noes: " + noesCountObj.getString("_value");
                String[] noesCountVal = noesCount.split(": ");
                commonsDivisionsInformation.NoVotes = Integer.parseInt(noesCountVal[1]);
                JSONArray voteArray = primaryTopic.getJSONArray("vote");
                for (int j = 0; j < voteArray.length(); j++) {
                    JSONObject voteItem = voteArray.getJSONObject(j);
                    String memberParty = voteItem.getString("memberParty");
                    String[] type = voteItem.getString("type").split("#");
                    boolean updatePartyVotes = UpdatePartyVotesDetails(type[1], memberParty);
                    if (!updatePartyVotes) {
                        AddNewPartyVoteDetail(type[1], memberParty);
                    }
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
        return commonsDivisionsInformation;
    }

    private boolean UpdatePartyVotesDetails(String type, String partyName) {
        boolean updated = false;
        for (PartyVoteDetail partyVoteDetail: commonsDivisionsInformation.partyVoteDetails) {
            if (partyVoteDetail.Name.equals(partyName)) {
                IncrementVoteValues(type, partyVoteDetail);
                updated = true;
            }
        }
        return updated;
    }

    private void AddNewPartyVoteDetail(String type, String partyName) {
        PartyVoteDetail partyVoteDetail = new PartyVoteDetail(partyName);
        IncrementVoteValues(type, partyVoteDetail);
        commonsDivisionsInformation.partyVoteDetails.add(partyVoteDetail);
    }

    private void IncrementVoteValues(String type, PartyVoteDetail partyVoteDetails) {
        if (type.equals(VoteOptions.AyeVote)) {
            partyVoteDetails.AyeVotes++;
        } else if (type.equals(VoteOptions.NoVote)) {
            partyVoteDetails.NoVotes++;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}
