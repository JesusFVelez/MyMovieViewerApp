package com.example.mymovieviewer;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovieviewer.database.MoviesEntry;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class movieAdapter extends RecyclerView.Adapter<movieAdapter.movieViewHolder> {

     private movieItemClicker movieClickListener;
    private ArrayList<MovieInfo> movies = new ArrayList<MovieInfo>();
    private static boolean Favorited = false;

    public void setMovies(ArrayList<MovieInfo> movies) {
        this.movies = movies;
        movies.add(new MovieInfo());
        notifyDataSetChanged();
    }

    public static boolean isFavorited() {
        return Favorited;
    }

    public static void setIsFavorited(boolean Favorited) {
        movieAdapter.Favorited = Favorited;
    }

    public void addMoreMovies(ArrayList<MovieInfo> moreMovies){
        movies.remove(movies.size() - 1);
        this.movies.addAll(moreMovies);
        movies.add(new MovieInfo());
        notifyDataSetChanged();
    }

    public movieAdapter(movieItemClicker clickListener){
        int viewHolderCount = 0;
        movieClickListener = clickListener;
    }


    public interface movieItemClicker{
//        void onMovieItemClick(String movieImgID, String movieName, String movieDesc, Integer movieRating, String release, String trailerURL, String movieID, Bitmap smallMovieImgURL, MovieInfo theMovie);
        void onMovieItemClick(MovieInfo theMovie);
        void loadMoreContent(ProgressBar loadMoreProgressBar, ImageView loadMoreImgView);
    }


// --Commented out by Inspection START (7/10/20, 1:41 AM):
//    public void addLoadMoreMoviesOption(){
//        boolean addingLoadMore = true;
//
//    }
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)



    @NonNull
    @Override
    public movieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new movieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final movieViewHolder holder, final int position) {
        if(movies.get(position).isEmpty() && !isFavorited()) {
            holder.movieImgV.setImageResource(R.drawable.loadmore);
        }else{
            String movieImgURL = movies.get(position).getMovieImageURL();
            Picasso.get().load(movieImgURL).into(holder.movieImgV);
            movies.get(position).setSmallMovieImgURL(movieImgURL);
            movies.get(position).changeToBiggerSizeImage();
            holder.parentLayout.setOnClickListener(holder);
        }
    }

    @Override
    public int getItemCount() {
        if(movies.isEmpty()){
            return 0;
        }else{
            return movies.size();
        }
    }

    //View Holder
    class movieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView movieImgV;
        public final RelativeLayout parentLayout;
        public final ProgressBar progressBar;
        public movieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImgV = itemView.findViewById(R.id.movieIW);
// --Commented out by Inspection START (7/10/20, 1:41 AM):
            parentLayout = itemView.findViewById(R.id.mainLayout);
            progressBar = itemView.findViewById(R.id.progress_circle);
// --Commented out by Inspection STOP (7/10/20, 1:41 AM)

        }

        public void allLoaded(){
            progressBar.setVisibility(View.INVISIBLE);
        }

        //sets on CLickListener for each movie in the screen
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            movies.get(adapterPosition).changeToBiggerSizeImage();
            if(movies.get(adapterPosition).isEmpty()) {
                movieClickListener.loadMoreContent(progressBar, movieImgV);
            }else{
                movieClickListener.onMovieItemClick(movies.get(adapterPosition));
            }
        }
    }




}
