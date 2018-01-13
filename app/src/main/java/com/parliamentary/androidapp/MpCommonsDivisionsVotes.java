package com.parliamentary.androidapp;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.MpParliamentProfile;
import com.parliamentary.androidapp.models.MpVote;
import com.parliamentary.androidapp.models.VoteOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jg413 on 12/01/2018.
 */

public class MpCommonsDivisionsVotes extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate = null;
    ArrayList<HashMap<String, String>> mpCommonsDivisions;

    public MpCommonsDivisionsVotes(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpHandler sh = new HttpHandler();
        mpCommonsDivisions = new ArrayList<>();
        MpParliamentProfile mpParliamentProfile = (MpParliamentProfile) objects[0];

        for (int pageNumber = 0; pageNumber < 5; pageNumber++) {
            String url = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=10&_page=" + pageNumber;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject result = jsonObj.getJSONObject("result");
                    JSONArray items = result.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        MpVote mpVote = new MpVote();
                        JSONObject item = items.getJSONObject(i);
                        String title = item.getString("title");
                        mpVote.Title = title;
                        JSONObject dateObj = item.getJSONObject("date");
                        String date = dateObj.getString("_value");
                        mpVote.Date = date;
                        String _about = item.getString("_about");
                        String[] aboutSplit = _about.split("/");
                        String divisionUrl = "http://lda.data.parliament.uk/commonsdivisions/id/" + aboutSplit[aboutSplit.length - 1] + ".json";
                        jsonStr = sh.makeServiceCall(divisionUrl);
                        jsonObj = new JSONObject(jsonStr);
                        result = jsonObj.getJSONObject("result");
                        JSONObject primaryTopic = result.getJSONObject("primaryTopic");
                        JSONArray ayesCountArr = primaryTopic.getJSONArray("AyesCount");
                        JSONObject ayesCountObj = ayesCountArr.getJSONObject(0);
                        String ayesCount = "Ayes: " + ayesCountObj.getString("_value");
                        String[] ayesCountVal = ayesCount.split(": ");
                        mpVote.AyeVotes = Integer.parseInt(ayesCountVal[1]);
                        JSONArray noesCountArr = primaryTopic.getJSONArray("Noesvotecount");
                        JSONObject noesCountObj = noesCountArr.getJSONObject(0);
                        String noesCount = "Noes: " + noesCountObj.getString("_value");
                        String[] noesCountVal = noesCount.split(": ");
                        mpVote.NoVotes = Integer.parseInt(noesCountVal[1]);
                        JSONArray vote = primaryTopic.getJSONArray("vote");
                        for (int j = 0; j < vote.length(); j++) {
                            JSONObject voteItem = vote.getJSONObject(j);
                            JSONObject memberPrinted = voteItem.getJSONObject("memberPrinted");
                            if (memberPrinted.getString("_value").equals(mpParliamentProfile.Name)) {
                                String[] type = voteItem.getString("type").split("#");
                                if (type[1].equals(VoteOptions.AyeVote)) {
                                    mpVote.MpVote = VoteOptions.AyeVote;
                                    addVoteToHashMap(mpVote);
                                } else if (type[1].equals(VoteOptions.NoVote)) {
                                    mpVote.MpVote = VoteOptions.NoVote;
                                    addVoteToHashMap(mpVote);
                                }
                                break;
                            }
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
        }
        return mpCommonsDivisions;
    }

    private void addVoteToHashMap(MpVote vote) {
        // tmp hash map for single contact
        HashMap<String, String> contact = new HashMap<>();

        // TODO: Change HashMap to handle ints
        String ayeVotes = "Aye Votes: " + Integer.toString(vote.AyeVotes);
        String noVotes = "No Votes: " + Integer.toString(vote.NoVotes);

        // adding each child node to HashMap key => value
        contact.put("title", vote.Title);
        contact.put("date", vote.Date);
        contact.put("ayes", ayeVotes);
        contact.put("noes", noVotes);
        contact.put("mpVote", vote.MpVote);

        // adding contact to contact list
        mpCommonsDivisions.add(contact);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}
