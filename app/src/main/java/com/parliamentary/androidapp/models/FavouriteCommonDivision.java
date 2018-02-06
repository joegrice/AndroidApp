package com.parliamentary.androidapp.models;

/**
 * Created by jg413 on 21/01/2018.
 */

public class FavouriteCommonDivision {
    public String id;
    public Long commonsDivisionId;

    public FavouriteCommonDivision() {
        // Default constructor required for calls to DataSnapshot.getValue(FavouriteCommonDivision.class)
    }

    public FavouriteCommonDivision(String id, Long commonsDivisionId) {
        this.id = id;
        this.commonsDivisionId = commonsDivisionId;
    }
}
