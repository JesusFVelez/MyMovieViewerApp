package com.example.mymovieviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {



   private ArrayList<Review> reviews = new ArrayList<Review>();


   public void addReviews(ArrayList<Review> newReviews){
       this.reviews.clear();
       this.reviews.addAll(newReviews);
       notifyDataSetChanged();
   }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImeadiately = false;
        View view = inflater.inflate(R.layout.movie_review_view,parent,shouldAttachToParentImeadiately);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {
       if(reviews != null || !reviews.isEmpty() ){
           holder.author.setText(reviews.get(position).getReviewAuthor() + ":");
           holder.content.setText(reviews.get(position).getActualReview());
           holder.currentReviewNum.setText(String.format("%d) ",position + 1));
       }else{
           holder.author.setText(":");
           holder.content.setText("No review Available To Display...");
           holder.currentReviewNum.setText("");
       }

    }

    @Override
    public int getItemCount() {
        if(reviews.isEmpty()){
            return 0;
        }else{
            return reviews.size();
        }
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        public final TextView author;
        public final TextView content;
        public final TextView currentReviewNum;


    public MovieReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.author);
        content = itemView.findViewById(R.id.contents);
        currentReviewNum = itemView.findViewById(R.id.numOfReview);
    }
}
}
