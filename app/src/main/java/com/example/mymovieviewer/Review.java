package com.example.mymovieviewer;

import android.os.Parcel;
import android.os.Parcelable;

//TODO(10) Make Parcelable
public class Review implements Parcelable {
    private String actualReview;
    private String ReviewAuthor;
    private String ReviewID;

    public Review(String actualReview, String reviewAuthor, String reviewID) {
        this.actualReview = actualReview;
        ReviewAuthor = reviewAuthor;
        ReviewID = reviewID;
    }

    public Review() {
        this.actualReview = "No Review to display...";
        this.ReviewAuthor = "Juan Del Pueblo";
        this.ReviewID = "0";
    }

    protected Review(Parcel in) {
        actualReview = in.readString();
        ReviewAuthor = in.readString();
        ReviewID = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getActualReview() {
        return actualReview;
    }

    public void setActualReview(String actualReview) {
        this.actualReview = actualReview;
    }

    public String getReviewAuthor() {
        return ReviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        ReviewAuthor = reviewAuthor;
    }

    public String getReviewID() {
        return ReviewID;
    }

    public void setReviewID(String reviewID) {
        ReviewID = reviewID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(actualReview);
        parcel.writeString(ReviewAuthor);
        parcel.writeString(ReviewID);
    }
}