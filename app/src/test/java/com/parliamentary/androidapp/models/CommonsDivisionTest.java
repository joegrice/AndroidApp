package com.parliamentary.androidapp.models;

import org.json.JSONObject;
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

    private JSONObject getJsonObject() throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("commonsDivisionExample.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return new JSONObject(total.toString());
    }

    private HashMap<String, Long> getFavourites() throws Exception {
        HashMap<String, Long> favourites = new HashMap<>();
        favourites.put("-L5Oyan4hKLyBeZq_4U1", new Long(838200));
        favourites.put("-L5OywvdVM0Vi3tmduQJ", new Long(838186));
        return favourites;
    }

    private MpParliamentProfile createMpParliamentProfile() {
        MpParliamentProfile mpParliamentProfile = new MpParliamentProfile();
        mpParliamentProfile.CommonsConstituency = "Amber Valley";
        mpParliamentProfile.CommonsParty = "Conservative";
        mpParliamentProfile.Name = "Nigel Mills";
        return mpParliamentProfile;
    }

    @Test
    public void CreateCommonsDivision() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertNotNull(commonsDivision);
    }

    @Test
    public void CheckId() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Id, new Long(837690));
    }

    @Test
    public void CheckIsFavourite() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Favourite, false);
    }

    @Test
    public void CheckAyeVotesCount() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.AyeVotes, 33);
    }

    @Test
    public void CheckNoVotesCount() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.NoVotes, 282);
    }

    @Test
    public void CheckTitle() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Title, "Space Industry Bill: Report Stage Amdt 1");
    }

    @Test
    public void CheckDate() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Date, "2018-02-06");
    }

    @Test
    public void CheckUrl() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites);

        assertEquals(commonsDivision.Url, "http://eldaddp.azurewebsites.net/commonsdivisions/id/837690.json");
    }

    @Test
    public void CheckMpVote() throws Exception {
        JSONObject jsonObject = getJsonObject();
        HashMap<String, Long> favourites = getFavourites();
        MpParliamentProfile mpParliamentProfile = createMpParliamentProfile();
        CommonsDivision commonsDivision = new CommonsDivision(jsonObject, favourites, mpParliamentProfile);

        assertEquals(commonsDivision.MpVote, VoteOptions.NoVote);
    }
}