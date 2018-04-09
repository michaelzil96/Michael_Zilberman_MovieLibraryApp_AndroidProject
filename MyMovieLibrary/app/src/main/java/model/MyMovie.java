package model;

// this is our movie model:
public class MyMovie {
    // the movie components:
    private String movieName;
    private String movieDescription;
    private String moviePoster;
    private String dateMovieAdded;
    private int movieId;
    private String watchedOrNot;

    // the movie ctor:
    public MyMovie(String movieName, String movieDescription, String moviePoster, int movieId) {
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.moviePoster = moviePoster;
        this.movieId = movieId;
    }


    // getters and setters:

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getDateMovieAdded() {
        return dateMovieAdded;
    }

    public void setDateMovieAdded(String dateMovieAdded) {
        this.dateMovieAdded = dateMovieAdded;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getWatchedOrNot() {
        return watchedOrNot;
    }

    public void setWatchedOrNot(String watchedOrNot) {
        this.watchedOrNot = watchedOrNot;
    }
}