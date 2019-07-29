package com.android.ffbf.model;

import java.io.Serializable;

public class StreetStall implements Serializable {

    private String streetStallId;
    private String streetStallImageUrl;
    private String streetStallName;
    private String streetStallLocation;
    private boolean isVegetarian;
    private String streetStallInfo;

    public StreetStall() {
    }

    public StreetStall(String streetStallId, String streetStallImageUrl, String streetStallName, String streetStallLocation, boolean isVegetarian, String streetStallInfo) {
        this.streetStallId = streetStallId;
        this.streetStallImageUrl = streetStallImageUrl;
        this.streetStallName = streetStallName;
        this.streetStallLocation = streetStallLocation;
        this.isVegetarian = isVegetarian;
        this.streetStallInfo = streetStallInfo;
    }

    public String getStreetStallId() {
        return streetStallId;
    }

    public void setStreetStallId(String streetStallId) {
        this.streetStallId = streetStallId;
    }

    public String getStreetStallImageUrl() {
        return streetStallImageUrl;
    }

    public void setStreetStallImageUrl(String streetStallImageUrl) {
        this.streetStallImageUrl = streetStallImageUrl;
    }

    public String getStreetStallName() {
        return streetStallName;
    }

    public void setStreetStallName(String streetStallName) {
        this.streetStallName = streetStallName;
    }

    public String getStreetStallLocation() {
        return streetStallLocation;
    }

    public void setStreetStallLocation(String streetStallLocation) {
        this.streetStallLocation = streetStallLocation;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public String getStreetStallInfo() {
        return streetStallInfo;
    }

    public void setStreetStallInfo(String streetStallInfo) {
        this.streetStallInfo = streetStallInfo;
    }
}
