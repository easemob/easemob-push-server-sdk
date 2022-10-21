package com.easemob.push.model;

import java.util.List;

public class LabelUserRequest {

    private final List<String> usernames;

    public LabelUserRequest(List<String> usernames) {
        if (usernames == null || usernames.isEmpty() || usernames.size() > 100) {
            throw new IllegalArgumentException("user name list illegal");
        }
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }
}
