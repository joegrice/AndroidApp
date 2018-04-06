package com.parliamentary.androidapp;

import com.parliamentary.androidapp.helpers.CommonsDivisionFactory;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.MpParliamentProfile;

import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jg413 on 06/04/2018.
 */

public class CommonsDivisionsListTest {

    private MpParliamentProfile createMpParliamentProfile(String name) {
        MpParliamentProfile mpParliamentProfile = new MpParliamentProfile();
        mpParliamentProfile.Name = name;
        return mpParliamentProfile;
    }

    @Test
    public void CreateCommonsDivisionMpList() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        MpParliamentProfile mpParliamentProfile = createMpParliamentProfile("Jonathan Edwards");
        ArrayList<CommonsDivision> commonsDivisionArrayList = new ArrayList<>();

        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites, mpParliamentProfile);
        if (commonsDivision.MpVote != null) {
            commonsDivisionArrayList.add(commonsDivision);
        }

        assertEquals(commonsDivisionArrayList.size(), 1);
    }

    @Test
    public void CreateCommonsDivisionNoMpList() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        MpParliamentProfile mpParliamentProfile = createMpParliamentProfile("Jonathan AAAAAAA");
        ArrayList<CommonsDivision> commonsDivisionArrayList = new ArrayList<>();

        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites, mpParliamentProfile);
        if (commonsDivision.MpVote != null) {
            commonsDivisionArrayList.add(commonsDivision);
        }

        assertEquals(commonsDivisionArrayList.size(), 0);
    }
}
