package com.adnd.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adnd.popularmovies.R;
import com.adnd.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private List<Movie> movies;

    public PosterAdapter(List<Movie> movies) {
        if (movies == null) {
            this.movies = new ArrayList<>();
        } else {
            this.movies = movies;
        }
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.poster_list_item, viewGroup, false);
        PosterViewHolder posterViewHolder = new PosterViewHolder(view);

        return posterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder posterViewHolder, int position) {
        Movie movie = movies.get(position);
        // TODO: replace poster path with poster url
        posterViewHolder.bind(movie.getPoster_path());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class PosterViewHolder extends RecyclerView.ViewHolder {

        // TODO: replace with ImageView
        private TextView posterTextView;

        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterTextView = itemView.findViewById(R.id.tv_poster_url);
        }

        void bind(String posterUrl) {
            posterTextView.setText(posterUrl);
        }
    }
}
