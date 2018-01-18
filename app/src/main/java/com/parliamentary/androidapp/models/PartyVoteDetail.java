package com.parliamentary.androidapp.models;

public class PartyVoteDetail {

    public String Name;
    public int AyeVotes;
    public int NoVotes;

    public PartyVoteDetail(String Name) {
        this.Name = Name;
        this.AyeVotes = 0;
        this.NoVotes = 0;
    }
}
