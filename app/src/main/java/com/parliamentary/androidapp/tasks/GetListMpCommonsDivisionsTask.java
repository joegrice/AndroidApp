package com.parliamentary.androidapp.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.parliamentary.androidapp.R;
import com.parliamentary.androidapp.helpers.HttpHandler;
import com.parliamentary.androidapp.MainActivity;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.MpParliamentProfile;
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

public class GetListMpCommonsDivisionsTask extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate = null;
    private MpParliamentProfile mpParliamentProfile;
    private TextView progressBarText;

    public GetListMpCommonsDivisionsTask(TextView progressBarText, AsyncResponse delegate) {
        this.delegate = delegate;
        this.progressBarText = progressBarText;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        progressBarText.setText((String) values[0]);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpHandler sh = new HttpHandler();
        ArrayList<CommonsDivision> commonsDivisions = new ArrayList<>();
        mpParliamentProfile = (MpParliamentProfile) objects[0];
        HashMap<String, Long> favourites = (HashMap<String, Long>) objects[1];
        int pageNumber = (int) objects[2];

        String url = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=10&_page=" + pageNumber;
        String jsonStr = sh.makeServiceCall(url);

        Log.e(TAG, "Result Page: " + pageNumber);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject result = jsonObj.getJSONObject("result");
                JSONArray items = result.getJSONArray("items");
                commonsDivisions = createListCommonsDivisions(items, favourites);
            } catch (final JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
        return commonsDivisions;
    }

    private ArrayList<CommonsDivision> createListCommonsDivisions(JSONArray items, HashMap<String, Long> favourites) throws JSONException {
        ArrayList<CommonsDivision> commonsDivisions = new ArrayList<>();
        HttpHandler httpHandler = new HttpHandler();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String _about = item.getString("_about");
            String[] aboutSplit = _about.split("/");
            String divisionUrl = "http://lda.data.parliament.uk/commonsdivisions/id/" + aboutSplit[aboutSplit.length - 1] + ".json";
            String json = httpHandler.makeServiceCall(divisionUrl);
            JSONObject itemObj = new JSONObject(json);
            CommonsDivision commonsDivision = new CommonsDivision(itemObj, favourites, mpParliamentProfile);
            if (commonsDivision.MpVote != null && !commonsDivision.MpVote.isEmpty()
                     && !commonsDivision.MpVote.equals("null")) {
                commonsDivisions.add(commonsDivision);
                publishProgress("Commons Divisions Found: " + commonsDivisions.size());
            }
        }
        return commonsDivisions;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}
