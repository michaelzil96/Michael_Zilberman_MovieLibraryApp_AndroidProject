package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import model.MyMovie;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final ArrayList<MyMovie> moviesList = new ArrayList<>();

    // ctor
    public DatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        onCreate(this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MOVIES_TABLE = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME +
                "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.MOVIE_NAME +
                " TEXT, " + Constants.MOVIE_DESCRIPTION + " TEXT, " + Constants.MOVIE_POSTER_URL +
                " TEXT, " + Constants.MOVIE_WATCHED_OR_NOT + " TEXT, " + Constants.DATE_MOVIE_ADDED +
                " LONG);";

        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        // create a new one
        onCreate(db);
    }

    public void deleteAllMovies(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);
        db.close();
    }

    // add content to the movies table
    public void addMovieToDB(MyMovie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.MOVIE_NAME, movie.getMovieName());
        values.put(Constants.MOVIE_DESCRIPTION, movie.getMovieDescription());
        values.put(Constants.MOVIE_POSTER_URL, movie.getMoviePoster());
        values.put(Constants.MOVIE_WATCHED_OR_NOT, movie.getWatchedOrNot());
        values.put(Constants.DATE_MOVIE_ADDED, java.lang.System.currentTimeMillis());

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();
    }

    // get all movies
    public ArrayList<MyMovie> getMoviesListFromDB(){
        String  selectQuery= "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID, Constants.MOVIE_NAME,
                        Constants.MOVIE_DESCRIPTION, Constants.MOVIE_POSTER_URL, Constants.MOVIE_WATCHED_OR_NOT,
                        Constants.DATE_MOVIE_ADDED},null, null, null, null,
                        Constants.DATE_MOVIE_ADDED + " DESC");

        // loop through cursor
        if(cursor.moveToFirst()){
            do{
                MyMovie movie = new MyMovie(cursor.getString(cursor.getColumnIndex(Constants.MOVIE_NAME)),
                        cursor.getString(cursor.getColumnIndex(Constants.MOVIE_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(Constants.MOVIE_POSTER_URL)),
                        cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));

                movie.setWatchedOrNot(cursor.getString(cursor.getColumnIndex(Constants.MOVIE_WATCHED_OR_NOT)));

                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String dateData = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.DATE_MOVIE_ADDED))).getTime());
                movie.setDateMovieAdded(dateData); // adding the time to the movie as a string

                moviesList.add(movie);

            }while(cursor.moveToNext());        }

        db.close();
        return moviesList;
    }

    // when a movie is edited
    public void updateMovieInDB(String name, String desc, String poster, String watchedOrNot, int movieId){

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.MOVIE_NAME, name);
        contentValues.put(Constants.MOVIE_DESCRIPTION, desc);
        contentValues.put(Constants.MOVIE_POSTER_URL, poster);
        contentValues.put(Constants.MOVIE_WATCHED_OR_NOT, watchedOrNot);

        db.update(Constants.TABLE_NAME, contentValues, Constants.KEY_ID + "=" + movieId, null);
        db.close();
    }

    public void deleteAMovieInDB(int movieId){
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=" + movieId, null);

        db.close();
    }
}
