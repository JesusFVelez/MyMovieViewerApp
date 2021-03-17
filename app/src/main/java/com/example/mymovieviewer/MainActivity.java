package com.example.mymovieviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mymovieviewer.database.AppMovieDatabase;
import com.example.mymovieviewer.database.ImageConverter;
import com.example.mymovieviewer.database.MoviesEntry;
import com.example.mymovieviewer.database.ReviewsEntry;
import com.example.mymovieviewer.utils.NetworkUtils;
import com.livefront.bridge.Bridge;
import com.livefront.bridge.SavedStateHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements movieAdapter.movieItemClicker {


    private RecyclerView recyclerView;
    private movieAdapter AmovieAdapter;
    private ProgressBar mLoadingIndicator;
    private Menu menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView sortingType;
    private boolean isMostPop;
    private boolean isTopRated;
    private ProgressBar loadMoreProgressBar;
    private ImageView loadMoreImageView;
    private AppMovieDatabase appDB;
    private static String MOSTPOP = "Most Popular Movies";
    private static String TOPRATE = "Top Rated Movies";
    private static String FAV = "Favorites";
    /*
COMPLETE(1) Add more movies to the view (Currently Working on it)
TODO(2) Improve UI
COMPLETE(3) Fix movie ratings

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bridge.initialize(this, new SavedStateHandler() {
            @Override
            public void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {

            }

            @Override
            public void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {

            }
        });
        Bridge.restoreInstanceState(this,savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_movies);
        Context context = this;
        GridLayoutManager LayoutManager = new GridLayoutManager(context,3);
        LayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        appDB = AppMovieDatabase.getInstance(getApplicationContext());
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setHasFixedSize(true);
        AmovieAdapter = new movieAdapter(this);
        recyclerView.setAdapter(AmovieAdapter);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        isMostPop = true;
        isTopRated = false;
        sortingType = findViewById(R.id.SortingType);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isMostPop){
                    sortingType.setText(MOSTPOP);
                }else if(isTopRated){
                    sortingType.setText(TOPRATE);
                }
                new FetchMovieInfo().execute();
                AmovieAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadMovies();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void loadMoreContent(ProgressBar loadMoreProgressBar, ImageView loadMoreImgView) {
        this.loadMoreProgressBar = loadMoreProgressBar;
        this.loadMoreImageView = loadMoreImgView;
        new FetchMoreMovies().execute();
    }




    @Override
    public void onMovieItemClick(MovieInfo theMovie){
        Context context = MainActivity.this;
        Class destinationClass = movieDetail.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie", theMovie);
        startActivity(intentToStartDetailActivity);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    public void loadMovies(){

        new FetchMovieInfo().execute();
    }


    public void sortingMovies(MenuItem v){
        MenuItem[] otherMenuItem = new MenuItem[2];
        String sortingTypeString;
        switch (v.getItemId()){
            case R.id.most_pop:
                swipeRefreshLayout.setEnabled(true);
                isMostPop = true;
                isTopRated = false;
                otherMenuItem[0] = menu.findItem(R.id.top_rate);
                otherMenuItem[1] = menu.findItem(R.id.favorites);
                movieAdapter.setIsFavorited(false);
                sortingTypeString = MOSTPOP;
                swipeRefreshLayout.setRefreshing(true);

                break;

            case R.id.top_rate:
                swipeRefreshLayout.setEnabled(true);
                isMostPop = false;
                isTopRated = true;
                otherMenuItem[0] = menu.findItem(R.id.most_pop);
                otherMenuItem[1] = menu.findItem(R.id.favorites);
                movieAdapter.setIsFavorited(false);
                sortingTypeString = TOPRATE;
                swipeRefreshLayout.setRefreshing(true);
                break;

            default:
                swipeRefreshLayout.setEnabled(false);
                isMostPop = false;
                isTopRated = false;
                otherMenuItem[0] = menu.findItem(R.id.most_pop);
                otherMenuItem[1] = menu.findItem(R.id.top_rate);
                movieAdapter.setIsFavorited(true);
                sortingTypeString = FAV;

                break;
        }

        otherMenuItem[0].setChecked(false);
        otherMenuItem[1].setChecked(false);
        v.setChecked(true);
        sortingType.setText(sortingTypeString);
        new FetchMovieInfo().execute();

    }




    public class FetchMovieInfo extends AsyncTask<String, Void, ArrayList<MovieInfo>>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<MovieInfo> doInBackground(String... keywords) {

                boolean isFavorite = !isMostPop && !isTopRated;

                if(!isFavorite) {
                    URL moviesURL = null;
                    String urlToUseForSearch;
                    if (isMostPop) {
                        urlToUseForSearch = NetworkUtils.MOVIES_MOST_POPULAR_BASE_URL;
                    } else {
                        urlToUseForSearch = NetworkUtils.MOVIES_TOP_RATED_BASE_URL;
                    }


                    if (keywords.length == 0) {
                        try {
                            moviesURL = new URL(Uri.parse(urlToUseForSearch).buildUpon().appendQueryParameter(NetworkUtils.API_KEY, NetworkUtils.apiKey).appendQueryParameter(NetworkUtils.SORT_BY_PARAM, NetworkUtils.descendingSorting).build().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        moviesURL = NetworkUtils.buildUrl(keywords[0]);
                    }

                    try {
                        String httpResponse = NetworkUtils.getResponseFromHttpUrl(moviesURL);
                        return NetworkUtils.parseMovieJSON(httpResponse, MainActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }


                }else{

                    try {
                        //TODO(10) Fix the images not appearing in favorites tab


                        List<MoviesEntry> moviesEntryList = appDB.moviesDao().loadAllSavedMovies();
                        ArrayList<MovieInfo> moreMovies = new ArrayList<>();

                        for (MoviesEntry MovieEntries: moviesEntryList) {
                            ArrayList<Review> reviews = new ArrayList<>();
                            MovieInfo newMovieBeingAdded = MovieEntries.getMovieInfoEquivalent();
                            List<ReviewsEntry> reviewEntries = appDB.moviesDao().loadAllReviewsForMovieUsingID(newMovieBeingAdded.getMovieID());
                            for(ReviewsEntry Rentries : reviewEntries){

                                reviews.add(Rentries.getMovieReviewEquivalent());
                            }
                            newMovieBeingAdded.setReviews(reviews);
                            moreMovies.add(newMovieBeingAdded);                        }
                        return moreMovies;

                    }catch(Exception e){
                        e.printStackTrace();

                        return null;
                    }
                }

        }


        @Override
        protected void onPostExecute(ArrayList<MovieInfo> movieInfos) {
            recyclerView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);



            if(movieInfos != null){
                AmovieAdapter.setMovies(movieInfos);
            }else{
                //error occured
                sortingType.setText("No internet Detected :(");
                Log.e("Null error", "movieIfo empty");

            }
        }
    }






    public class FetchMoreMovies extends AsyncTask<String, Void, ArrayList<MovieInfo>>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadMoreProgressBar.setVisibility(View.VISIBLE);
            loadMoreImageView.setVisibility(View.INVISIBLE);

        }

        @Override
        protected ArrayList<MovieInfo> doInBackground(String... keywords) {
            URL moviesURL = null;
                String urlToUseForSearch;
            if(isMostPop){
                urlToUseForSearch = NetworkUtils.MOVIES_MOST_POPULAR_BASE_URL;
            }else{
                urlToUseForSearch = NetworkUtils.MOVIES_TOP_RATED_BASE_URL;
            }


            if(keywords.length == 0){
                try {
                    NetworkUtils.numOfPages++;
                    moviesURL = new URL(Uri.parse(urlToUseForSearch).buildUpon().appendQueryParameter(NetworkUtils.API_KEY, NetworkUtils.apiKey).appendQueryParameter(NetworkUtils.SORT_BY_PARAM, NetworkUtils.descendingSorting).appendQueryParameter(NetworkUtils.PAGES_NUM, NetworkUtils.numOfPages.toString()).build().toString());

                }catch(Exception e){
                    e.printStackTrace();
                }

            }else{
                moviesURL = NetworkUtils.buildUrl(keywords[0]);
            }

            try {
                String httpResponse = NetworkUtils.getResponseFromHttpUrl(moviesURL);
                return NetworkUtils.parseMovieJSON(httpResponse, MainActivity.this);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

        }



        @Override
        protected void onPostExecute(ArrayList<MovieInfo> movieInfos) {
//            super.onPostExecute(movieInfos);

            loadMoreProgressBar.setVisibility(View.INVISIBLE);
            loadMoreImageView.setVisibility(View.VISIBLE);
            String sortingTypeString;
            swipeRefreshLayout.setRefreshing(false);
            if(isMostPop){
                movieAdapter.setIsFavorited(false);
                sortingTypeString = "Most Popular Movies";
            }else if(isTopRated){
                movieAdapter.setIsFavorited(false);
                sortingTypeString = "Top Rated Movies";
            }else{
                movieAdapter.setIsFavorited(true);
                sortingTypeString = "Favorites";
            }
            sortingType.setText(sortingTypeString);
            if(movieInfos != null){
                AmovieAdapter.addMoreMovies(movieInfos);
            }else{
                //error occured
                sortingType.setText("No internet Detected :(");
                Log.e("Null error", "movieIfo empty");

            }
        }
    }



}