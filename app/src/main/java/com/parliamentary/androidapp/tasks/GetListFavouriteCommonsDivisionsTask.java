package com.parliamentary.androidapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.helpers.HttpHandler;
import com.parliamentary.androidapp.MainActivity;
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
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    CommonsDivision commonsDivision = new CommonsDivision(jsonObj, favourites);
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

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}
