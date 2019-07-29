package com.android.ffbf.model;

import java.io.Serializable;

public class Review implements Serializable {

    private String reviewId;
    private String reviewerName;
    private String reviewText;
    private float reviewRating;
    private String restaurantId;


    public Review() {
    }

    public Review(String reviewId, String reviewerName, String reviewText, float reviewRating, String restaurantId) {
        this.reviewId = reviewId;
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.reviewRating = reviewRating;
        this.restaurantId = restaurantId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public float getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(float reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
