package com.example.android.popularmovies;

/**
 * Created by robert on 9/14/16.
 *
 * This class is designed to track all of TMDB movie attributes
 *
 */
public class MovieObject {


    private String movie_poster_path;
    private Boolean movie_adult;
    private String movie_overview;
    private String movie_release_date;
    private String movie_genre_ids;
    private long movie_id;
    private String movie_original_title;
    private String movie_original_language;
    private String movie_title;
    private String movie_backdrop_path;
    private float movie_popularity;
    private long movie_vote_count;
    private Boolean movie_video;
    private float movie_vote_average;
    private String movie_result_type;
    private String movie_most_popular;
    private String movie_top_rated;
    private String movie_favorite;

    public String getMovie_most_popular() {
        return movie_most_popular;
    }

    public void setMovie_most_popular(String movie_most_popular) {
        this.movie_most_popular = movie_most_popular;
    }

    public String getMovie_top_rated() {
        return movie_top_rated;
    }

    public void setMovie_top_rated(String movie_top_rated) {
        this.movie_top_rated = movie_top_rated;
    }

    public String getMovie_favorite() {
        return movie_favorite;
    }

    public void setMovie_favorite(String movie_favorite) {
        this.movie_favorite = movie_favorite;
    }

    public String getMovie_result_type() {
        return movie_result_type;
    }

    public void setMovie_result_type(String movie_result_type) {
        this.movie_result_type = movie_result_type;
    }

    public String getMovie_poster_path() {
        return movie_poster_path;
    }

    public Boolean getMovie_adult() {
        return movie_adult;
    }

    public String getMovie_overview() {
        return movie_overview;
    }

    public String getMovie_release_date() {
        return movie_release_date;
    }

    public String getMovie_genre_ids() {
        return movie_genre_ids;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public String getMovie_original_title() {
        return movie_original_title;
    }

    public String getMovie_original_language() {
        return movie_original_language;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public String getMovie_backdrop_path() {
        return movie_backdrop_path;
    }

    public float getMovie_popularity() {
        return movie_popularity;
    }

    public long getMovie_vote_count(){
        return movie_vote_count;
    }

    public Boolean getMovie_video() {
        return movie_video;
    }

    public float getMovie_vote_average() {
        return movie_vote_average;
    }

    public void setMovie_poster_path(String new_value) {
        movie_poster_path = new_value;
    }

    public void setMovie_adult(Boolean new_value) {
        movie_adult = new_value;
    }

    public void setMovie_overview(String new_value) {
        movie_overview = new_value;
    }

    public void setMovie_release_date(String new_value) {
        movie_release_date = new_value;
    }

    public void setMovie_genre_ids(String new_value) {
        movie_genre_ids = new_value;
    }

    public void setMovie_id(long new_value) {
        movie_id = new_value;
    }

    public void setMovie_original_title(String new_value) {
        movie_original_title = new_value;
    }

    public void setMovie_original_language(String new_value) {
        movie_original_language = new_value;
    }

    public void setMovie_title(String new_value) {
        movie_title = new_value;
    }

    public void setMovie_backdrop_path(String new_value) {
        movie_backdrop_path = new_value;
    }

    public void setMovie_popularity(float new_value) {
        movie_popularity = new_value;
    }

    public void setMovie_vote_count(long new_value){
        movie_vote_count = new_value;
    }

    public void setMovie_video(Boolean new_value) {
        movie_video = new_value;
    }

    public void setMovie_vote_average(float new_value) {
        movie_vote_average = new_value;
    }

}
