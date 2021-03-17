package com.example.mymovieviewer.database;

import android.content.DialogInterface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM FavoriteMovies ORDER BY movieID")
    List<MoviesEntry> loadAllSavedMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavMovie(MoviesEntry moviesEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MoviesEntry moviesEntry);

    @Delete
    void deleteFavMovie(MoviesEntry moviesEntry);

    @Query("SELECT * FROM FavoriteMovies WHERE movieID = :id")
    MoviesEntry loadMovieByID(int id);

    @Query("SELECT * FROM FavoriteMovies WHERE :condition")
    List<MoviesEntry> loadMovieByCondition(String condition);

    @Query("SELECT IsFavorited FROM FAVORITEMOVIES WHERE movieID = :ID")
    boolean loadIfChecked(String ID);

    @Query("SELECT COUNT(movieID) FROM FAVORITEMOVIES WHERE movieID = :id")
    Integer checkIfMovieInDataBase(String id);

    @Query("SELECT * FROM ReviewsPerMovie  WHERE MovieIDForReview = :id")
    List<ReviewsEntry> loadAllReviewsForMovieUsingID(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReviewForMovie(ReviewsEntry reviewsEntry);

}
