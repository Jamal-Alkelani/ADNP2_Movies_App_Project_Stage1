package com.example.gamal.adnp2_movies_app_project_stage1.Utilities;


import com.example.gamal.adnp2_movies_app_project_stage1.Models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Movies_JSONHandler {
    private String json;

    public Movies_JSONHandler(String json) {
        this.json = json;
    }

    public List<Movie> extractFromJSON() {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                Movie movie = new Movie();
                JSONObject movieObj = results.getJSONObject(i);
                movie.setVoteAvg(movieObj.get("vote_average").toString());
                movie.setTitle(movieObj.get("title").toString());
                movie.setMoviePoster("https://image.tmdb.org/t/p/w500" + movieObj.get("poster_path").toString());
                movie.setCategory(movieObj.get("adult").toString());
                movie.setDesc(movieObj.get("overview").toString());
                movie.setReleaseDate(movieObj.get("release_date").toString());
                movies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
