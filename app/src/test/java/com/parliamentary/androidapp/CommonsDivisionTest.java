package com.parliamentary.androidapp;

import com.parliamentary.androidapp.helpers.CommonsDivisionFactory;
import com.parliamentary.androidapp.models.CommonsDivision;
import com.parliamentary.androidapp.models.MpParliamentProfile;
import com.parliamentary.androidapp.models.VoteOptions;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jg413 on 15/02/2018.
 */
public class CommonsDivisionTest {

    private MpParliamentProfile createMpParliamentProfile() {
        MpParliamentProfile mpParliamentProfile = new MpParliamentProfile();
        mpParliamentProfile.CommonsConstituency = "Amber Valley";
        mpParliamentProfile.CommonsParty = "Conservative";
        mpParliamentProfile.Name = "Nigel Mills";
        return mpParliamentProfile;
    }

    @Test
    public void CreateCommonsDivision() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertNotNull(commonsDivision);
    }

    @Test
    public void CheckId() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Id, new Long(837690));
    }

    @Test
    public void CheckIsFavourite() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Favourite, false);
    }

    @Test
    public void CheckAyeVotesCount() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.AyeVotes, 33);
    }

    @Test
    public void CheckNoVotesCount() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.NoVotes, 282);
    }

    @Test
    public void CheckTitle() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Title, "Space Industry Bill: Report Stage Amdt 1");
    }

    @Test
    public void CheckDate() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Date, "2018-02-06");
    }

    @Test
    public void CheckUrl() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Url, "http://eldaddp.azurewebsites.net/commonsdivisions/id/837690.json");
    }

    @Test
    public void CheckMpVote() throws Exception {
        CommonsDivisionFactory commonsDivisionFactory = new CommonsDivisionFactory();
        JSONObject jsonObject = commonsDivisionFactory.getJsonObject();
        HashMap<String, Long> favourites = commonsDivisionFactory.getFavourites();
        MpParliamentProfile mpParliamentProfile = createMpParliamentProfile();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites, mpParliamentProfile);

        Assert.assertEquals(commonsDivision.MpVote, VoteOptions.NoVote);
    }
}