package com.parliamentary.androidapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jg413 on 20/01/2018.
 */

public class CommonsDivision {
    public String Title;
    public String Date;
    public int AyeVotes;
    public int NoVotes;
    public String Url;
    public Long Id;
    public String MpVote;
    public ArrayList<PartyVoteDetail> partyVoteDetails;
    public boolean Favourite;
    private MpParliamentProfile mpParliamentProfile;

    public CommonsDivision(JSONObject item, HashMap<String, Long> favourites) throws JSONException {
        createCommonsDivision(item, favourites);
    }

    public CommonsDivision(JSONObject item, HashMap<String, Long> favourites, MpParliamentProfile mpParliamentProfile) throws JSONException {
        this.mpParliamentProfile = mpParliamentProfile;
        createCommonsDivision(item, favourites);
    }

    private void createCommonsDivision(JSONObject item, HashMap<String, Long> favourites) throws JSONException {
        JSONObject result = item.getJSONObject("result");
        JSONObject primaryTopic = result.getJSONObject("primaryTopic");
        String _about = primaryTopic.getString("_about");
        String[] aboutSplit = _about.split("/");
        String id = aboutSplit[aboutSplit.length - 1];
        Long idLong = Long.parseLong(id);
        Id = idLong;
        Favourite = isFavourite(id, favourites);
        String url = primaryTopic.getString("isPrimaryTopicOf");
        Url = url;
        String title = primaryTopic.getString("title");
        Title = title;
        JSONObject dateObj = primaryTopic.getJSONObject("date");
        String date = dateObj.getString("_value");
        Date = date;
        updateAyeVoteCount(primaryTopic);
        updateNoVoteCount(primaryTopic);
        JSONArray vote = primaryTopic.getJSONArray("vote");
        updatePartyDetails(vote);
    }

    private boolean isFavourite(String id, HashMap <String, Long>  favourites) {
        boolean favourite = false;
        for (Map.Entry<String, Long> entry : favourites.entrySet()) {
            if (Long.parseLong(id) == entry.getValue()) {
                favourite = true;
                break;
            }
        }
        return favourite;
    }

    private void updateAyeVoteCount(JSONObject primaryTopic) throws JSONException {
        JSONArray ayesCountArr = primaryTopic.getJSONArray("AyesCount");
        JSONObject ayesCountObj = ayesCountArr.getJSONObject(0);
        String ayesCount = "Ayes: " + ayesCountObj.getString("_value");
        String[] ayesCountVal = ayesCount.split(": ");
        AyeVotes = Integer.parseInt(ayesCountVal[1]);
    }

    private void updateNoVoteCount(JSONObject primaryTopic) throws JSONException {
        JSONArray noesCountArr = primaryTopic.getJSONArray("Noesvotecount");
        JSONObject noesCountObj = noesCountArr.getJSONObject(0);
        String noesCount = "Noes: " + noesCountObj.getString("_value");
        String[] noesCountVal = noesCount.split(": ");
        NoVotes = Integer.parseInt(noesCountVal[1]);
    }

    private void updatePartyDetails(JSONArray vote) throws JSONException {
        partyVoteDetails = new ArrayList<>();
        for (int j = 0; j < vote.length(); j++) {
            JSONObject voteItem = vote.getJSONObject(j);
            JSONObject memberPrinted = voteItem.getJSONObject("memberPrinted");
            if (mpParliamentProfile != null && memberPrinted.getString("_value").equals(mpParliamentProfile.Name)) {
                String[] type = voteItem.getString("type").split("#");
                if (type[1].equals(VoteOptions.AyeVote)) {
                    MpVote = VoteOptions.AyeVote;
                } else if (type[1].equals(VoteOptions.NoVote)) {
                    MpVote = VoteOptions.NoVote;
                }
            }
            String memberParty = voteItem.getString("memberParty");
            String[] type = voteItem.getString("type").split("#");
            boolean updatePartyVotes = updatePartyVotesDetails(type[1], memberParty);
            if (!updatePartyVotes) {
                addNewPartyVoteDetail(type[1], memberParty);
            }
        }
    }

    private boolean updatePartyVotesDetails(String type, String partyName) {
        boolean updated = false;
        for (PartyVoteDetail partyVoteDetail : partyVoteDetails) {
            if (partyVoteDetail.Name.equals(partyName)) {
                incrementVoteValues(type, partyVoteDetail);
                updated = true;
            }
        }
        return updated;
    }

    private void addNewPartyVoteDetail(String type, String partyName) {
        PartyVoteDetail partyVoteDetail = new PartyVoteDetail(partyName);
        incrementVoteValues(type, partyVoteDetail);
        partyVoteDetails.add(partyVoteDetail);
    }

    private void incrementVoteValues(String type, PartyVoteDetail partyVoteDetails) {
        if (type.equals(VoteOptions.AyeVote)) {
            partyVoteDetails.AyeVotes++;
        } else if (type.equals(VoteOptions.NoVote)) {
            partyVoteDetails.NoVotes++;
        }
    }
}
