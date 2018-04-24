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

public class GetMpCommonsDivisionsTask extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate;

    public GetMpCommonsDivisionsTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpHandler httpHandler = new HttpHandler();
        String url = (String) objects[0];
        HashMap<String, Long> favourites = (HashMap<String, Long>) objects[1];
        MpParliamentProfile profile = (MpParliamentProfile) objects[2];

        String jsonStr = httpHandler.makeServiceCall(url);
        CommonsDivision commonsDivision = null;
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                commonsDivision = new CommonsDivision(jsonObj, favourites, profile);
            } catch (final JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
        return commonsDivision;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}