package com.example.mymovieviewer.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.mymovieviewer.Review;

@Entity(tableName = "ReviewsPerMovie")
public class ReviewsEntry {
    @PrimaryKey
    @NonNull
    private String ReviewID;
    private String Author;
    private String ContentOfReview;
    @ForeignKey(entity = MoviesEntry.class, onDelete = ForeignKey.CASCADE, childColumns = "MovieIDForReview", parentColumns = "MovieID")
    private String MovieIDForReview;

    public String getMovieIDForReview() {
        return MovieIDForReview;
    }

    public void setMovieIDForReview(String movieIDForReview) {
        MovieIDForReview = movieIDForReview;
    }

    public String getReviewID() {
        return ReviewID;
    }

    public void setReviewID(String reviewID) {
        ReviewID = reviewID;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getContentOfReview() {
        return ContentOfReview;
    }

    public void setContentOfReview(String contentOfReview) {
        ContentOfReview = contentOfReview;
    }

    public Review getMovieReviewEquivalent(){
        return new Review(ContentOfReview, Author, ReviewID);
    }

    public ReviewsEntry(String ReviewID, String Author, String ContentOfReview, String MovieIDForReview) {
        this.ReviewID =ReviewID;
        this.Author = Author;
        this.ContentOfReview = ContentOfReview;
        this.MovieIDForReview = MovieIDForReview;
    }
}
