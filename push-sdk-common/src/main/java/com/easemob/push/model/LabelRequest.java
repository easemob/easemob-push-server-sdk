package com.easemob.push.model;

public class LabelRequest {

    private final String name;
    private final String description;

    public LabelRequest(String name, String description) {
        if (name == null || name.length() == 0 || name.length() > 64) {
            throw new IllegalArgumentException("label name illegal");
        }
        this.name = name;
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("label description illegal");
        }
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
