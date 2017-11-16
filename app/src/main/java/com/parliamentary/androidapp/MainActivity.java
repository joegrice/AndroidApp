package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private Button buttonListPage;
    private Button buttonMpPage;
    private Button buttonFavouritePage;
    private Button buttonProfilePage;

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        buttonListPage = (Button) findViewById(R.id.buttonListPage);
        buttonMpPage = (Button) findViewById(R.id.buttonMpPage);
        buttonFavouritePage = (Button) findViewById(R.id.buttonFavouritePage);
        buttonProfilePage = (Button) findViewById(R.id.buttonProfilePage);

        buttonListPage.setOnClickListener(this);
        buttonMpPage.setOnClickListener(this);
        buttonFavouritePage.setOnClickListener(this);
        buttonProfilePage.setOnClickListener(this);

        new GetContacts().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.buttonProfilePage:
            startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            int pageSize = 10;
            String url = "http://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=" + pageSize + "&_page=0";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject result = jsonObj.getJSONObject("result");
                    JSONArray items = result.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
                        String description = c.getString("_about");
                        String title = c.getString("title");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("title", title);
                        contact.put("description", description);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"title", "description"},
                    new int[]{R.id.bill_title, R.id.bill_description});
            lv.setAdapter(adapter);
        }
    }
}