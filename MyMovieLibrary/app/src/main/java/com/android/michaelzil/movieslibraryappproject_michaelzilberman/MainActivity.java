package com.android.michaelzil.movieslibraryappproject_michaelzilberman;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import data.DatabaseHandler;
import model.MyMovie;

// the main activity is our movies list, the library:
public class MainActivity extends AppCompatActivity{

    private DatabaseHandler dba;
    private ArrayList<MyMovie> dbMovies = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private ListView moviesList;

    static final int REQUEST_CODE = 1;

    private MenuItem menuItemAdd;
    private MenuItem menuItemExit;

    private Dialog dialogEditOrDelete;
    private Button editButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // when the app starts we set the list, that will contain all the movies, with our adapter:
        moviesList = (ListView) findViewById(R.id.moviesList);
        ArrayList<MyMovie> data = new ArrayList<>();
        MovieAdapter adapter = new MovieAdapter(this, R.layout.movie_row,data); // movie row is an XML that sets layout for each row for each movie
        moviesList.setAdapter(adapter);
        // and we use this method (written lower) to get our current data:
        refreshData();

        // if movie in the list is clicked the AddOrEditMovieActivity starts, in order to edit the movie:
        moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, AddOrEditMovieActivity.class);
                intent.putExtra("name", dbMovies.get(i).getMovieName());
                intent.putExtra("desc", dbMovies.get(i).getMovieDescription());
                intent.putExtra("poster", dbMovies.get(i).getMoviePoster());
                intent.putExtra("movieId", dbMovies.get(i).getMovieId());
                intent.putExtra("watchedOrNot", dbMovies.get(i).getWatchedOrNot());
                intent.putExtra("whichIntent", 1);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        // a dialog of choice "edit or delete" appears if a long-click was done on the movies list:
        moviesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                choiceEditOrDelete(i);
                return true;
            }
        });
    }

    // every time that we resume to the main activity we refresh our data, so it changes if a movie was added or edited.
    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    /*______________________________________________________  Methods  _____________________________________________________________*/

    // this method is getting our data about movies from the database:
    private void refreshData(){

        dbMovies.clear();
        dba = new DatabaseHandler(getApplicationContext());

        ArrayList<MyMovie> moviesFromDB = dba.getMoviesListFromDB();

        for(int i = 0; i < moviesFromDB.size(); i++){

            MyMovie movie = new MyMovie(moviesFromDB.get(i).getMovieName(),
                    moviesFromDB.get(i).getMovieDescription(), moviesFromDB.get(i).getMoviePoster(), moviesFromDB.get(i).getMovieId());
                    movie.setDateMovieAdded(moviesFromDB.get(i).getDateMovieAdded()); // to add the date
            movie.setWatchedOrNot(moviesFromDB.get(i).getWatchedOrNot());
            dbMovies.add(movie);
        }
        dba.close();

        // setup adapter:
        movieAdapter = new MovieAdapter(MainActivity.this, R.layout.movie_row, dbMovies);
        moviesList.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }

    // when we get back to the main activity from adding or editing a movie:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            // this will we true and done when a movie is added or edited:
            if(resultCode == RESULT_OK){
                refreshData();
            }
        }
    }

    // this method is for the alert dialog of long-click on a movie in the list. you get choice to edit or delete
    public void choiceEditOrDelete(final int i){

        dialogEditOrDelete = new Dialog(MainActivity.this);
        dialogEditOrDelete.setContentView(R.layout.dialog_edit_or_delete);
        dialogEditOrDelete.setTitle(R.string.dialogEditOrDeleteTitle);

        editButton = dialogEditOrDelete.findViewById(R.id.editButton);
        deleteButton = dialogEditOrDelete.findViewById(R.id.deleteButton);

        editButton.setEnabled(true);
        deleteButton.setEnabled(true);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditOrDelete.dismiss();
                Intent intent = new Intent(MainActivity.this, AddOrEditMovieActivity.class);
                intent.putExtra("name", dbMovies.get(i).getMovieName());
                intent.putExtra("desc", dbMovies.get(i).getMovieDescription());
                intent.putExtra("poster", dbMovies.get(i).getMoviePoster());
                intent.putExtra("movieId", dbMovies.get(i).getMovieId());
                intent.putExtra("watchedOrNot", dbMovies.get(i).getWatchedOrNot());
                intent.putExtra("whichIntent", 1);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditOrDelete.dismiss();
                dba.deleteAMovieInDB(dbMovies.get(i).getMovieId());
                refreshData();
            }
        });

        dialogEditOrDelete.show();
    }

    /*______________________________________________________  MENU  _____________________________________________________________*/

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        menuItemAdd = menu.findItem(R.id.menuItemAdd);
        menuItemExit = menu.findItem(R.id.menuItemExitOrDelete);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menuItemAdd:
                return true;
            case R.id.addInternet:
                Intent intentSearch = new Intent(this, SearchMoviesByInternetActivity.class);
                startActivityForResult(intentSearch, REQUEST_CODE);
                return true;
            case R.id.addManually:
                Intent intentAdd = new Intent(this, AddOrEditMovieActivity.class);
                startActivityForResult(intentAdd, REQUEST_CODE);
                return true;
            case R.id.deleteAllMovies:
                dba.deleteAllMovies();
                refreshData();
                return true;
            case R.id.exitApp:
                finish();
                return true;
        }
        return false;
    }
}