package com.parliamentary.androidapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.MainActivity;
import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.helpers.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jg413 on 12/01/2018.
 */

public class GetCommonsDivisionsPageTask extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate;

    public GetCommonsDivisionsPageTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpHandler httpHandler = new HttpHandler();
        JSONArray items = new JSONArray();
        int pageNumber = (int) objects[0];

        String url = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=10&_page=" + pageNumber;
        String jsonStr = httpHandler.makeServiceCall(url);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject result = jsonObj.getJSONObject("result");
                items = result.getJSONArray("items");
            } catch (final JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
        return items;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}
