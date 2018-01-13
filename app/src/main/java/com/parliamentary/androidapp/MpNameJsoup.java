package com.parliamentary.androidapp;

import android.os.AsyncTask;
import android.util.Log;

import com.parliamentary.androidapp.data.AsyncResponse;
import com.parliamentary.androidapp.models.MpParliamentProfile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by jg413 on 11/01/2018.
 */

public class MpNameJsoup extends AsyncTask<Object, Object, Object> {

    private String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate = null;

    public MpNameJsoup(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        MpParliamentProfile mpParliamentProfile = new MpParliamentProfile();
        try {
            Document doc = Jsoup.connect("http://www.parliament.uk/mps-lords-and-offices/mps/?search_term=" + objects[0].toString()).get();
            mpParliamentProfile.CommonsConstituency = doc.getElementById("commons-constituency").text();
            mpParliamentProfile.CommonsParty = doc.getElementById("commons-party").text();
            mpParliamentProfile.MemberImg = doc.getElementById("ctl00_ctl00_FormContent_SiteSpecificPlaceholder_PageContent_mbiMemberImage_imgMember").attr("src");
            String url = doc.getElementById("ctl00_ctl00_FormContent_SiteSpecificPlaceholder_PageContent_lnkElectionResults").attr("href");
            doc = Jsoup.connect("http://www.parliament.uk" + url).get();
            Element electionClass = doc.select(".election-result-candidate").first();
            mpParliamentProfile.Name = electionClass.text();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return mpParliamentProfile;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o);
    }
}
