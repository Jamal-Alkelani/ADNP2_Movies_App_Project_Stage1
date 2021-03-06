package com.example.gamal.adnp2_movies_app_project_stage1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gamal.adnp2_movies_app_project_stage1.Adapters.MovieAdapter;
import com.example.gamal.adnp2_movies_app_project_stage1.Models.Movie;
import com.example.gamal.adnp2_movies_app_project_stage1.Utilities.Movies_JSONHandler;
import com.example.gamal.adnp2_movies_app_project_stage1.Utilities.NetworkHandler;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity implements MovieAdapter.OnItemClickListener {
    RecyclerView rv;
    private int isSearchLineVisible = 4;
    MovieAdapter.OnItemClickListener listener;
    List<Movie> moviesArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Movies App");
        listener = this;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        moviesArr = new ArrayList<>();
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        rv = findViewById(R.id.movies_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        Slide anim=new Slide();
        anim.setSlideEdge(Gravity.BOTTOM);
        getWindow().setReenterTransition(anim);
        getWindow().setExitTransition(anim);
        getWindow().setAllowReturnTransitionOverlap(false);
        }


        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute("");
    }

    @SuppressLint("RestrictedApi")
    private void applySearchAnimation() {
        ObjectAnimator searchLeftAnimator = null;
        ActionMenuItemView search = findViewById(R.id.search_menu_item);
        EditText editText = findViewById(R.id.searchLine);

        switch (isSearchLineVisible) {
            case View.INVISIBLE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    search.setIcon(getDrawable(R.drawable.ic_search_black_24dp));
                    searchLeftAnimator = ObjectAnimator.ofFloat(search, "alpha", 1f, 0f);
                    searchLeftAnimator.setDuration(1000);

                    search.setIcon(getDrawable(R.drawable.ic_close_black_24dp));
                    searchLeftAnimator = ObjectAnimator.ofFloat(search, "alpha", 0f, 1f);
                    searchLeftAnimator.setDuration(1500);
                    editText.setVisibility(View.VISIBLE);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocusFromTouch();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    isSearchLineVisible = View.VISIBLE;
                }
                break;

            case View.VISIBLE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    search.setIcon(getDrawable(R.drawable.ic_close_black_24dp));
                    searchLeftAnimator = ObjectAnimator.ofFloat(search, "alpha", 1f, 0f);
                    searchLeftAnimator.setDuration(1000);

                    search.setIcon(getDrawable(R.drawable.ic_search_black_24dp));
                    searchLeftAnimator = ObjectAnimator.ofFloat(search, "alpha", 0f, 1f);
                    searchLeftAnimator.setDuration(1500);
                    editText.setText("");
                    editText.setVisibility(View.INVISIBLE);
                    editText.setFocusableInTouchMode(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    isSearchLineVisible = View.INVISIBLE;
                }
                break;
        }


        ObjectAnimator eidtEditTextAnimator = ObjectAnimator.ofFloat(editText, "alpha", 0f, 1f);
        eidtEditTextAnimator.setDuration(1000);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(searchLeftAnimator).with(eidtEditTextAnimator);
        animatorSet.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.search_menu_item == item.getItemId()) {
            applySearchAnimation();
        } else if (R.id.rated == item.getItemId()) {
            NetworkHandler.SORTBY = NetworkHandler.SORT_BY_HIGHEST_RATED;
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute("");
        } else if (R.id.pop == item.getItemId()) {
            NetworkHandler.SORTBY = NetworkHandler.SORT_BY_POPULARITY;
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute("");
        } else if (R.id.doSearch == item.getItemId()) {
            EditText editText = findViewById(R.id.searchLine);
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(editText.getText().toString());
        }
        return true;
    }

    public void setMainMovieInfo(Movie movie) {
        TextView mainName = findViewById(R.id.mainName);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ImageView imageView = findViewById(R.id.movieOfDay);
        Picasso.with(this).load(movie.getMoviePoster()).into(imageView);

        mainName.setText(movie.getTitle());
        ratingBar.setRating(Float.parseFloat(movie.getVoteAvg()));

    }

    @Override
    public void onItemClick(int pos) {
        openMovieDetailsView(pos);
    }

    private void openMovieDetailsView(int pos) {
        Intent intent = new Intent(Home.this, Details.class);
        intent.putExtra(Details.TITLE_TAG, moviesArr.get(pos).getTitle());
        intent.putExtra(Details.VOTEAVG_TAG, moviesArr.get(pos).getVoteAvg());
        intent.putExtra(Details.DATE_TAG, moviesArr.get(pos).getReleaseDate());
        intent.putExtra(Details.DESC_TAG, moviesArr.get(pos).getDesc());
        intent.putExtra(Details.IMAGE_TAG, moviesArr.get(pos).getMoviePoster());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        }

    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            NetworkHandler networkHandler = new NetworkHandler();
            URL url = networkHandler.buildURL(strings[0], NetworkHandler.SORT_BY_POPULARITY);
            String jsonResponse = networkHandler.getResultFromURL(url);
            Movies_JSONHandler jsonHandler = new Movies_JSONHandler(jsonResponse);
            List<Movie> arr = jsonHandler.extractFromJSON();

            return arr;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            MovieAdapter movieAdapter = new MovieAdapter(movies, getApplicationContext(), listener);
            moviesArr = movies;
            rv.setAdapter(movieAdapter);
            int randomIndex = (int) (Math.random() * movies.size()) - 1;
            setMainMovieInfo(movies.get(randomIndex));
        }
    }

}
