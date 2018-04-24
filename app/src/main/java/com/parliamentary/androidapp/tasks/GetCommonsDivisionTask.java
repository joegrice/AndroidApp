package com.parliamentary.androidapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.MainActivity;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.helpers.HttpHandler;
import com.parliamentary.androidapp.models.CommonsDivision;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jg413 on 12/01/2018.
 */

public class GetCommonsDivisionTask extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate;

    public GetCommonsDivisionTask(AsyncResponse delegate) {
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

        String jsonStr = httpHandler.makeServiceCall(url);
        CommonsDivision commonsDivision = null;
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                commonsDivision = new CommonsDivision(jsonObj, favourites);
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
