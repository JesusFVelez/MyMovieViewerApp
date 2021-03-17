package com.example.mymovieviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.example.mymovieviewer.database.AppMovieDatabase;
import com.example.mymovieviewer.database.ImageConverter;
import com.example.mymovieviewer.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MovieInfo implements Parcelable {

    private String movieImageURL;
    private String movieNames;
    private String movieDescription;
    private Integer userRating;
    private String releaseDate;
    private String imgID;
    private boolean isEmpty;
    private String trailer_link;
    private String movieID;
    private boolean isFavorited;
    private Bitmap smallMovieImgBitmap;
    private Bitmap largeMovieImgBitmap;
    private String largeMovieImgURL;
    private String smallMovieImgURL;
    private ArrayList<Review> reviews;

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieImageURL);
        parcel.writeString(movieNames);
        parcel.writeString(movieDescription);
        if (userRating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(userRating);
        }
        parcel.writeString(releaseDate);
        parcel.writeString(imgID);
        parcel.writeByte((byte) (isEmpty ? 1 : 0));
        parcel.writeString(trailer_link);
        parcel.writeString(movieID);
        parcel.writeByte((byte) (isFavorited? 1 : 0));
        parcel.writeString(largeMovieImgURL);
        parcel.writeString(smallMovieImgURL);
        parcel.writeList(reviews);
    }

    protected MovieInfo(Parcel in) {
        movieImageURL = in.readString();
        movieNames = in.readString();
        movieDescription = in.readString();
        if (in.readByte() == 0) {
            userRating = null;
        } else {
            userRating = in.readInt();
        }
        releaseDate = in.readString();
        imgID = in.readString();
        isEmpty = in.readByte() != 0;
        trailer_link = in.readString();
        movieID = in.readString();
        isFavorited = in.readByte() != 0;
        largeMovieImgURL = in.readString();
        smallMovieImgURL = in.readString();
        reviews = in.readArrayList(MovieInfo.class.getClassLoader());
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    public Bitmap getLargeMovieImgBitmap() {
        return largeMovieImgBitmap;
    }

    public void setLargeMovieImgBitmap(Bitmap largeMovieImgBitmap) {
        this.largeMovieImgBitmap = largeMovieImgBitmap;
    }

    public String getLargeMovieImgURL() {
        return largeMovieImgURL;
    }

    public void setLargeMovieImgURL(String largeMovieImgURL) {
        this.largeMovieImgURL = largeMovieImgURL;
    }

    public String getSmallMovieImgURL() {
        return smallMovieImgURL;
    }

    public void setSmallMovieImgURL(String smallMovieImgURL) {
        this.smallMovieImgURL = smallMovieImgURL;
    }

    public MovieInfo(){
        imgID = "N/A";
        movieImageURL = "N/A";
        movieNames = "N/A";
        movieDescription = "N/A";
        userRating = 0;
        releaseDate = "N/A";
        isEmpty = true;
        movieID = "";
        trailer_link = "";
        isFavorited = false;
    }

    public void loadImageInto(Target aTarget){
        Picasso.get().load(this.movieImageURL);
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public void setImgURLWithoutAddedURL(String IMGURL){
        this.movieImageURL = IMGURL;
    }

    public MovieInfo(String MI, String MN, String MD, Integer UR, String RD, String id, String trailerLink){
        imgID = MI;
        movieImageURL = NetworkUtils.MOVIE_IMG_BASE_URL + NetworkUtils.MOVIE_SIZE + MI;
        movieNames = MN;
        movieDescription = "Overview: " + MD;
// --Commented out by Inspection START (7/10/20, 1:41 AM):
        userRating = UR;
        releaseDate = "Release Date: " + RD;
        isEmpty = false;
        movieID = id;
        trailer_link = trailerLink;
        isFavorited = false;
    }

    public Bitmap getSmallMovieImgBitmap() {
        return smallMovieImgBitmap;
    }

    public void setSmallMovieImgBitmap(Bitmap smallMovieImgBitmap) {
        this.smallMovieImgBitmap = smallMovieImgBitmap;
    }

    //
//// --Commented out by Inspection START (7/10/20, 1:41 AM):
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)
    public void changeToBiggerSizeImage(){
        movieImageURL = NetworkUtils.MOVIE_IMG_BASE_URL + "original/" + imgID;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    largeMovieImgBitmap = Picasso.get().load(movieImageURL).get();
                    largeMovieImgURL = movieImageURL;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }
    ////
////
////    public String getMovieImageURL() {
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)
//        return movieImageURL;
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)


    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

// --Commented out by Inspection START (7/10/20, 1:41 AM):
//    public void setReleaseDate(String releaseDate) {
//        this.releaseDate = releaseDate;
//    }
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)

    public void setMovieImageURL(String movieImageURL) {
        this.movieImageURL = NetworkUtils.MOVIE_IMG_BASE_URL + NetworkUtils.MOVIE_SIZE + movieImageURL;
    }

//    public Integer getUserRating() {
// --Commented out by Inspection START (7/10/20, 1:41 AM):
//        return userRating;
//    }
//
//    public void setUserRating(Integer userRating) {
//        this.userRating = userRating;
//    }
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)

    public String getMovieImageURL() {
        return movieImageURL;
    }

    public String getMovieNames() {
        return movieNames;
    }

    public void setMovieNames(String movieNames) {
        this.movieNames = movieNames;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public String getTrailer_link() {
        return trailer_link;
    }

    public void setTrailer_link(String trailer_link) {
        this.trailer_link = trailer_link;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }




}
