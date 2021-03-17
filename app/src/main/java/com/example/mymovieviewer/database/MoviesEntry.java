package com.example.mymovieviewer.database;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.util.TableInfo;

import com.example.mymovieviewer.MovieInfo;
import com.example.mymovieviewer.R;

@Entity(tableName = "FavoriteMovies")
public class MoviesEntry {


    @PrimaryKey
    @NonNull
    private String movieID;
    @ColumnInfo(name = "Movie_Name")
    private String movieName;
    @ColumnInfo(name = "Movie_Description")
    private String movieDescription;
    @ColumnInfo(name = "User_Rating")
    private Integer userRating;
    @ColumnInfo(name = "Release_Date")
    private String releaseDate;
    @ColumnInfo(name = "Image_URL")
    private String imgID;
    @ColumnInfo(name = "Trailer_Link")
    private String trailerLink;
    @ColumnInfo(name = "IsFavorited")
    private boolean isFavorited;
    @ColumnInfo(name = "Small_movie_Img")
    @Nullable private String moviePosterImageSmallSize;
    @ColumnInfo(name = "Large_movie_Img")
    @Nullable private String moviePosterImageLargeSize;



    @Nullable
    public String getMoviePosterImageSmallSize() {
        return moviePosterImageSmallSize;
    }

    public void setMoviePosterImageSmallSize(@Nullable String moviePosterImageSmallSize) {
        this.moviePosterImageSmallSize = moviePosterImageSmallSize;
    }

    @Nullable
    public String getMoviePosterImageLargeSize() {
        return moviePosterImageLargeSize;
    }

    public void setMoviePosterImageLargeSize(@Nullable String moviePosterImageLargeSize) {
        this.moviePosterImageLargeSize = moviePosterImageLargeSize;
    }

    @Ignore
    public MoviesEntry(String movieName, String movieDescription, Integer userRating, String releaseDate, String imgID, String trailerLink, boolean isFavorited) {
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.imgID = imgID;
        this.trailerLink = trailerLink;
        this.isFavorited = isFavorited;

    }



    public MoviesEntry(String movieID, String movieName, String movieDescription, Integer userRating, String releaseDate, String imgID, String trailerLink, String moviePosterImageSmallSize, String moviePosterImageLargeSize) {
        this.movieID = movieID;
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.imgID = imgID;
        this.trailerLink = trailerLink;
        this.isFavorited = true;
        this.moviePosterImageSmallSize = moviePosterImageSmallSize;
        this.moviePosterImageLargeSize = moviePosterImageLargeSize;
    }




    public MovieInfo getMovieInfoEquivalent(){
        MovieInfo aMovie = new MovieInfo(imgID, movieName, movieDescription, userRating, releaseDate, movieID, trailerLink);
        aMovie.setFavorited(true);
        aMovie.setLargeMovieImgURL(moviePosterImageLargeSize);
        aMovie.setSmallMovieImgURL(moviePosterImageSmallSize);
        aMovie.setImgURLWithoutAddedURL(moviePosterImageSmallSize);

        return aMovie;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }
}
