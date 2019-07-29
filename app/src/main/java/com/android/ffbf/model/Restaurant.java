package com.android.ffbf.model;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String restaurantId;
    private String restaurantName;
    private String restaurantInfo;

    public Restaurant() {
    }

    public Restaurant(String restaurantId, String restaurantName, String restaurantInfo) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantInfo = restaurantInfo;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(String restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }
}
