package com.parliamentary.androidapp;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.PartyVoteDetail;
import com.parliamentary.androidapp.models.VoteOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jg413 on 12/01/2018.
 */

public class GetListFavouriteCommonsDivisionsTask extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate = null;

    public GetListFavouriteCommonsDivisionsTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpHandler sh = new HttpHandler();
        ArrayList<CommonsDivision> commonsDivisions = new ArrayList<>();
        HashMap<String, Long> favourites = (HashMap<String, Long>) objects[0];

        for (Map.Entry<String, Long> entry : favourites.entrySet()) {
            String commonsDivisionUrl = "http://lda.data.parliament.uk/commonsdivisions/id/" + entry.getValue() + ".json";
            String jsonStr = sh.makeServiceCall(commonsDivisionUrl);

            if (jsonStr != null) {
                try {
                    CommonsDivision commonsDivision = createCommonsDivision(jsonStr);
                    commonsDivisions.add(commonsDivision);
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
        }
        return commonsDivisions;
    }

    private CommonsDivision createCommonsDivision(String jsonStr) throws JSONException {
        CommonsDivision commonsDivision = new CommonsDivision();
        JSONObject jsonObj = new JSONObject(jsonStr);
        JSONObject result = jsonObj.getJSONObject("result");
        JSONObject primaryTopic = result.getJSONObject("primaryTopic");
        String title = primaryTopic.getString("title");
        commonsDivision.Title = title;
        JSONObject dateObj = primaryTopic.getJSONObject("date");
        String date = dateObj.getString("_value");
        commonsDivision.Date = date;
        updateAyeVoteCount(commonsDivision, primaryTopic);
        updateNoVoteCount(commonsDivision, primaryTopic);
        JSONArray vote = primaryTopic.getJSONArray("vote");
        updatePartyDetails(commonsDivision, vote);
        return commonsDivision;
    }

    private void updateAyeVoteCount(CommonsDivision commonsDivision, JSONObject primaryTopic) throws JSONException {
        JSONArray ayesCountArr = primaryTopic.getJSONArray("AyesCount");
        JSONObject ayesCountObj = ayesCountArr.getJSONObject(0);
        String ayesCount = "Ayes: " + ayesCountObj.getString("_value");
        String[] ayesCountVal = ayesCount.split(": ");
        commonsDivision.AyeVotes = Integer.parseInt(ayesCountVal[1]);
    }

    private void updateNoVoteCount(CommonsDivision commonsDivision, JSONObject primaryTopic) throws JSONException {
        JSONArray noesCountArr = primaryTopic.getJSONArray("Noesvotecount");
        JSONObject noesCountObj = noesCountArr.getJSONObject(0);
        String noesCount = "Noes: " + noesCountObj.getString("_value");
        String[] noesCountVal = noesCount.split(": ");
        commonsDivision.NoVotes = Integer.parseInt(noesCountVal[1]);
    }

    private void updatePartyDetails(CommonsDivision commonsDivision, JSONArray vote) throws JSONException {
        commonsDivision.partyVoteDetails = new ArrayList<>();
        for (int j = 0; j < vote.length(); j++) {
            JSONObject voteItem = vote.getJSONObject(j);
            String memberParty = voteItem.getString("memberParty");
            String[] type = voteItem.getString("type").split("#");
            boolean updatePartyVotes = updatePartyVotesDetails(commonsDivision, type[1], memberParty);
            if (!updatePartyVotes) {
                addNewPartyVoteDetail(commonsDivision, type[1], memberParty);
            }
        }
    }

    private boolean updatePartyVotesDetails(CommonsDivision commonsDivision, String type, String partyName) {
        boolean updated = false;
        for (PartyVoteDetail partyVoteDetail : commonsDivision.partyVoteDetails) {
            if (partyVoteDetail.Name.equals(partyName)) {
                incrementVoteValues(type, partyVoteDetail);
                updated = true;
            }
        }
        return updated;
    }

    private void addNewPartyVoteDetail(CommonsDivision commonsDivision, String type, String partyName) {
        PartyVoteDetail partyVoteDetail = new PartyVoteDetail(partyName);
        incrementVoteValues(type, partyVoteDetail);
        commonsDivision.partyVoteDetails.add(partyVoteDetail);
    }

    private void incrementVoteValues(String type, PartyVoteDetail partyVoteDetails) {
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
