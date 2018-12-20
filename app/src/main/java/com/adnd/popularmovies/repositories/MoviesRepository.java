package com.adnd.popularmovies.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.adnd.popularmovies.api.MovieListConverterFactory;
import com.adnd.popularmovies.api.TMDbApi;
import com.adnd.popularmovies.database.AppDatabase;
import com.adnd.popularmovies.database.FavoriteMoviesDao;
import com.adnd.popularmovies.database.MoviesDao;
import com.adnd.popularmovies.models.FavoriteMovie;
import com.adnd.popularmovies.models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoviesRepository {
    private static final String TAG = MoviesRepository.class.getSimpleName();

    public static final String POPULAR_MOVIES_KEY = "popular";
    public static final String TOP_RATED_MOVIES_KEY = "top_rated";

    private MoviesDao moviesDao;
    private FavoriteMoviesDao favoriteMoviesDao;

    public MoviesRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        favoriteMoviesDao = db.favoriteMoviesDao();
        moviesDao = db.MoviesDao();
    }


    public void addToFavorites(Movie movie) {
        // when inserting favorite movie we also need to inserting the movie itself
        moviesDao.insert(movie);
        favoriteMoviesDao.insert(new FavoriteMovie(movie.getId()));
    }

    public void deleteFavoriteMovie(FavoriteMovie favoriteMovie) {
        favoriteMoviesDao.deleteFavoriteMovie(favoriteMovie);

        // for now when deleting a favorite movie we also delete the movie itself
        deleteMovie(favoriteMovie.getMovie_id());
    }

    public void deleteMovie(Movie movie) {
        moviesDao.deleteMovie(movie);
    }

    public void deleteMovie(int movieId) {
        moviesDao.deleteMovieById(movieId);
    }

    public LiveData<Boolean> isFavorite(int movieId) {
        return favoriteMoviesDao.isFavorite(movieId);
    }

    public LiveData<List<Movie>> loadFavoriteMovies() {
        return favoriteMoviesDao.getFavoriteMovies();
    }

    public LiveData<List<Movie>> loadTopRatedMovies() {
        MutableLiveData<List<Movie>> topRatedMoviesLiveData = new MutableLiveData<>();

        loadMoviesFromWebService(TOP_RATED_MOVIES_KEY, topRatedMoviesLiveData);

        return topRatedMoviesLiveData;
    }

    public LiveData<List<Movie>> loadPopularMovies() {
        MutableLiveData<List<Movie>> popularMoviesLiveData = new MutableLiveData<>();

        loadMoviesFromWebService(POPULAR_MOVIES_KEY, popularMoviesLiveData);

        return popularMoviesLiveData;
    }

    private void loadMoviesFromWebService(String key, final MutableLiveData<List<Movie>> moviesListLiveData) {
        Retrofit fit = new Retrofit.Builder()
                .baseUrl(TMDbApi.BASE_URL)
                .addConverterFactory(new MovieListConverterFactory())
                .build();

        TMDbApi api = fit.create(TMDbApi.class);

        Call<List<Movie>> moviesCall = (key.equals(POPULAR_MOVIES_KEY)) ? api.getPopularMovies() : api.getTopRatedMovies();

        moviesCall.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    moviesListLiveData.setValue(response.body());
                } else {
                    onMovieListCallError();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                onMovieListCallError();
            }
        });
    }

    private void onMovieListCallError() {
        // TODO how to notify activity that an error occurred?
    }
}
