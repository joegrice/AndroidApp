package com.parliamentary.androidapp.models;

import java.util.HashMap;

/**
 * Created by jg413 on 19/01/2018.
 */

public class DatabaseUser {
    public  HashMap<String, Long> users;

    public DatabaseUser() {}

    public DatabaseUser(HashMap<String, Long> users) {
        this.users = users;
    }
}

