package com.example.mymovieviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovieviewer.database.AppMovieDatabase;
import com.example.mymovieviewer.database.MoviesEntry;
import com.example.mymovieviewer.database.ReviewsEntry;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

public class movieDetail extends AppCompatActivity {

    private MovieInfo movieBeingDisplayed;
    private MenuItem favoritesStar;
    private AppMovieDatabase appDB;
    private ImageView movieImg;
    private TextView movieTitle;
    private TextView releaseDate;
    private ImageView[] ratingStars;
    private TextView movieOverView;
    private List<MoviesEntry> favMoviesDatabaseEntries;
    private Button trailerBttn;
    private RecyclerView reviewsRV;
    private MovieReviewsAdapter reviewsAdapter;
    private List<ReviewsEntry> favMovieQuerriedReviews;
    private TextView MovieReviewLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        appDB = AppMovieDatabase.getInstance(getApplicationContext());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favMoviesDatabaseEntries = appDB.moviesDao().loadAllSavedMovies();

            }
        });
        movieImg = findViewById(R.id.movieIMGV);
        movieTitle = findViewById(R.id.movieTittle);
        releaseDate = findViewById(R.id.releaseDate);
        ratingStars = new ImageView[8];
        ratingStars[0] = findViewById(R.id.ratingStar1);
        ratingStars[1] = findViewById(R.id.ratingStar2);
        ratingStars[2] = findViewById(R.id.ratingStar3);
        ratingStars[3] = findViewById(R.id.ratingStar4);
        ratingStars[4] = findViewById(R.id.ratingStar5);
        ratingStars[5] = findViewById(R.id.ratingStar6);
        ratingStars[6] = findViewById(R.id.ratingStar7);
        ratingStars[7] = findViewById(R.id.ratingStar8);
        TextView movieOverView = findViewById(R.id.movieOV);
        LinearLayout userRatingLayout = findViewById(R.id.userRatingLayout);
        trailerBttn = findViewById(R.id.watch_trailer_btn);
        reviewsRV = findViewById(R.id.reviewsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reviewsRV.setLayoutManager(layoutManager);
        reviewsAdapter = new MovieReviewsAdapter();
        reviewsRV.setAdapter(reviewsAdapter);
        reviewsRV.setHasFixedSize(true);
        MovieReviewLabel = findViewById(R.id.reviewTV);


   // getSupportActionBar().setDisplayHomeAsUpEnabled(true);// This line of code adds a back arrow in the details page -> commented for purposes of Project 1
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
                movieBeingDisplayed = (MovieInfo) intentThatStartedThisActivity.getParcelableExtra("movie");
                movieBeingDisplayed.changeToBiggerSizeImage();
                Picasso.get().load(movieBeingDisplayed.getLargeMovieImgURL()).into(movieImg);
                movieTitle.setText(movieBeingDisplayed.getMovieNames());
                releaseDate.setText(movieBeingDisplayed.getReleaseDate());
                movieOverView.setText(movieBeingDisplayed.getMovieDescription());
                reviewsAdapter.addReviews(movieBeingDisplayed.getReviews());
                if(movieBeingDisplayed.getUserRating() != 0) {
                    for (int i = 0; i < movieBeingDisplayed.getUserRating(); i++) {
                        ratingStars[i].setImageResource(android.R.drawable.star_on);
                    }
                }

            }
            if(movieBeingDisplayed.getReviews().isEmpty()){
                MovieReviewLabel.setVisibility(View.INVISIBLE);
            }else{
                MovieReviewLabel.setVisibility(View.VISIBLE);
        }

        setOnClickListenerForTrailerBtn();
    }

    private void setOnClickListenerForTrailerBtn(){
        trailerBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  COMPLETE(1) Implement the intent to open the youtube app with the selected trailer
                if(getIntent() != null) {
                    Log.e("ATAG", "onClick: " + getIntent().getStringExtra("trailer"));
                    if(getIntent().getStringExtra("trailer") == "N/A"){
                        final AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                        alertDialog.setTitle("Uh-Oh, There seems to be a problem :/");
                        alertDialog.setMessage("We're sorry \n the trailer you are searching for could not be found \n :(");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();

                    }else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movieBeingDisplayed.getTrailer_link()) ) );
                    }
                }
            }
        });
    }

    public void addMovieToFav(final MenuItem v) {
        int itemID = v.getItemId();
        if (itemID == R.id.addToFavs) {
              if (favoritesStar.isChecked()) {
                // COMPLETE(8) Code for the movie is already in Favorites
                  final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                  alertDialog.setTitle("Remove Favorited Movie?");
                  alertDialog.setMessage("Are you sure you want to un-favorite this movie? Doing so will remove it from the local storage and will no longer be available offline");
                  alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                  alertDialog.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          movieBeingDisplayed.setFavorited(false);
                          if(movieBeingDisplayed.getLargeMovieImgURL() == null){
                              movieBeingDisplayed.changeToBiggerSizeImage();
                          }
                         final MoviesEntry movie = new MoviesEntry(movieBeingDisplayed.getMovieID(), movieBeingDisplayed.getMovieNames(), movieBeingDisplayed.getMovieDescription(), movieBeingDisplayed.getUserRating(), movieBeingDisplayed.getReleaseDate(), movieBeingDisplayed.getImgID(), movieBeingDisplayed.getTrailer_link(),  movieBeingDisplayed.getSmallMovieImgURL(), movieBeingDisplayed.getLargeMovieImgURL());
                          AppExecutors.getInstance().diskIO().execute(new Runnable() {
                              @Override
                              public void run() {
                                  appDB.moviesDao().deleteFavMovie(movie);
                              }
                          });
                          v.setIcon(android.R.drawable.btn_star_big_off);
                          v.setChecked(false);
                      }
                  });

                  alertDialog.setNegativeButton( "No", new DialogInterface.OnClickListener(){

                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          v.setChecked(true);
                      }
                  });
                  alertDialog.show();
                } else {

                    final MoviesEntry movie = new MoviesEntry(movieBeingDisplayed.getMovieID(), movieBeingDisplayed.getMovieNames(), movieBeingDisplayed.getMovieDescription(), movieBeingDisplayed.getUserRating(), movieBeingDisplayed.getReleaseDate(), movieBeingDisplayed.getMovieImageURL(), movieBeingDisplayed.getTrailer_link(),movieBeingDisplayed.getSmallMovieImgURL(), movieBeingDisplayed.getLargeMovieImgURL());
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            appDB.moviesDao().insertFavMovie(movie);
                            for(Review aReview : movieBeingDisplayed.getReviews()){
                                ReviewsEntry anEntry = new ReviewsEntry(aReview.getReviewID(), aReview.getReviewAuthor(), aReview.getActualReview(), movieBeingDisplayed.getMovieID());
                                appDB.moviesDao().insertReviewForMovie(anEntry);
                            }


                        }
                    });
                  Toast.makeText(getApplicationContext(), "Movie Added Successfuly to Favorites!", Toast.LENGTH_LONG).show();
                  v.setIcon(android.R.drawable.btn_star_big_on);
                  v.setChecked(true);


            }

        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_menu_bar,menu);
        favoritesStar = (MenuItem) menu.findItem(R.id.addToFavs);
        if(movieBeingDisplayed.isFavorited()){
            favoritesStar.setIcon(android.R.drawable.btn_star_big_on);
            favoritesStar.setChecked(true);
        }else {
            favoritesStar.setIcon(android.R.drawable.btn_star_big_off);
            favoritesStar.setChecked(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }
        return super.onOptionsItemSelected(item);
    }
}