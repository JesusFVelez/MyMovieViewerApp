package com.example.mymovieviewer.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.mymovieviewer.MovieInfo;
import com.example.mymovieviewer.Review;
import com.example.mymovieviewer.database.AppMovieDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public final static String MOVIE_IMG_BASE_URL = "https://image.tmdb.org/t/p/";
    public final static String MOVIE_SIZE = "w342/";
    public final static String TRAILER_BASE_URL = "https://api.themoviedb.org/3/movie";
    public final static String MOVIES_MOST_POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular?";
    public final static String MOVIES_TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated?";
    public final static String SORT_BY_PARAM = "sort_by";
    public final static String WITH_KEYWORDS = "with_keywords";
    public final static String LANGUAGE = "language";
    public final static String API_KEY = "api_key";
    public final static String PAGES_NUM = "page";
    public final static String VIDEOS_PATH = "videos";
    public final static String REVIEWS_BASE_URL = "https://api.themoviedb.org/3/movie/";




    public final static String descendingSorting = "popularity.desc";
    public final static String apiKey = "155e0701ff6776404902f770bffd0472";
    public final static String language = "en-US";
    public static Integer numOfPages = 1;



    public static URL buildUrl(String searchKeywords) {
        Uri builtUri = Uri.parse(MOVIES_MOST_POPULAR_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(SORT_BY_PARAM, descendingSorting)
                .appendQueryParameter(WITH_KEYWORDS, searchKeywords)
                .appendQueryParameter(PAGES_NUM, numOfPages.toString())
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);

        return url;
    }

    //TODO(11) Fix the Reviews making app crash
    private static ArrayList<Review> getReviews(String movieID){
        Uri builtUri = Uri.parse(REVIEWS_BASE_URL + movieID + "/reviews?").buildUpon()
                .appendQueryParameter(API_KEY,apiKey)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        ArrayList<Review> reviews = new ArrayList<Review>();
        try{
            String JSONResponse = getResponseFromHttpUrl(url);
            JSONObject theJason = new JSONObject(JSONResponse);
            JSONArray results = theJason.getJSONArray("results");

            for(int i = 0; i < results.length(); i++){
                JSONObject currentJSON = (JSONObject) results.get(i);
                String content = currentJSON.getString("content");
                String author = currentJSON.getString("author");
                String id = currentJSON.getString("id");

                Review aReview = new Review(content, author, id);
                reviews.add(aReview);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return reviews;
    }


    public static String buildAndParseTrailerURL(String theMovieID){
        Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                .appendPath(theMovieID)
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(LANGUAGE,language)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
       String trailerURL = "N/A";
        try {
            String JSONstring = getResponseFromHttpUrl(url);
            JSONObject ogJSON = new JSONObject(JSONstring);
            JSONArray resultsJSON = ogJSON.getJSONArray("results");
            for(int i = 0; i < resultsJSON.length(); i++) {
                JSONObject jsonArrayElement = resultsJSON.getJSONObject(i);
                String videoType = jsonArrayElement.getString("type");
                String siteURL = jsonArrayElement.getString("site");
                String trailerKey = jsonArrayElement.getString("key");
                boolean canSearchOnYoutube = (siteURL.equals("YouTube")) && ((videoType.equals("Trailer")) || (videoType.equals("Teaser")) || (videoType.equals("Clip")));
                if(canSearchOnYoutube){
                   trailerURL = "https://www.youtube.com/watch?v=" + trailerKey;
                    break;
                }

                Log.e(TAG, "buildAndParseTrailerURL: " + siteURL + " " + videoType );

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return trailerURL;
    }

    public static ArrayList<MovieInfo> parseMovieJSON(String JSONstring, Context context){
        try {
            JSONObject ogJSON = new JSONObject(JSONstring);
            JSONArray resultsJSON = ogJSON.getJSONArray("results");
            ArrayList<MovieInfo> movieInfoFromJSON = new ArrayList<>();
            for(int i = 0; i < resultsJSON.length(); i++){
                JSONObject jsonArrayElement = resultsJSON.getJSONObject(i);
                String posterID = jsonArrayElement.getString("poster_path");
                String overview = jsonArrayElement.getString("overview");
                String releaseDate = jsonArrayElement.getString("release_date");
                String Title = jsonArrayElement.getString("title");
                Integer userRating = jsonArrayElement.getInt("vote_average");
                String movieID = jsonArrayElement.getString("id");
                String trailerURL = buildAndParseTrailerURL(movieID);
                MovieInfo aMovie = new MovieInfo(posterID, Title, overview, userRating, releaseDate, movieID, trailerURL);
                aMovie.setReviews(getReviews(movieID));
                Integer isInDatabase = AppMovieDatabase.getInstance(context).moviesDao().checkIfMovieInDataBase(movieID);
                if(isInDatabase == 1){
                    aMovie.setFavorited(true);
                }else{
                    aMovie.setFavorited(false);
                }
                movieInfoFromJSON.add(aMovie);
            }
            return movieInfoFromJSON;


        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }



    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
