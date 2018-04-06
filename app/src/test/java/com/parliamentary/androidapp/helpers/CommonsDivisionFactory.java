package com.parliamentary.androidapp.helpers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by jg413 on 06/04/2018.
 */

public class CommonsDivisionFactory {

    public JSONObject getJsonObject() throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("commonsDivisionExample.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return new JSONObject(total.toString());
    }

    public HashMap<String, Long> getFavourites() throws Exception {
        HashMap<String, Long> favourites = new HashMap<>();
        favourites.put("-L5Oyan4hKLyBeZq_4U1", new Long(838200));
        favourites.put("-L5OywvdVM0Vi3tmduQJ", new Long(838186));
        return favourites;
    }
}
