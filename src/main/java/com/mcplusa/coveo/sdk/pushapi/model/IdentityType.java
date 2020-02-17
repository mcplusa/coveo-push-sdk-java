package com.mcplusa.coveo.sdk.pushapi.model;

public enum IdentityType {
    UNKNOWN("UNKNOWN"),
    USER("USER"),
    GROUP("GROUP"),
    VIRTUAL_GROUP("VIRTUAL_GROUP");

    public final String label;

    private IdentityType(String label) {
        this.label = label;
    }

    public static IdentityType valueOfLabel(String label) {
        for (IdentityType e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
