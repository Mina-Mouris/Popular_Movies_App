package com.example.popular_movies.models;

/**
 * Created by geekpro on 9/4/15.
 */
public class detailsModel {
    private String id;
    private String Title;
    private String posterImage_url;
    private String backImage_url;
    private String Vote;
    private String Date;
    private String overView;
    private String RunTime;

    public String getRunTime() {
        return RunTime;
    }

    public void setRunTime(String runTime) {
        RunTime = runTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPosterImage_url() {
        return posterImage_url;
    }

    public void setPosterImage_url(String posterImage_url) {
        this.posterImage_url = posterImage_url;
    }

    public String getBackImage_url() {
        return backImage_url;
    }

    public void setBackImage_url(String backImage_url) {
        this.backImage_url = backImage_url;
    }

    public String getVote() {
        return Vote;
    }

    public void setVote(String vote) {
        Vote = vote;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }
}
