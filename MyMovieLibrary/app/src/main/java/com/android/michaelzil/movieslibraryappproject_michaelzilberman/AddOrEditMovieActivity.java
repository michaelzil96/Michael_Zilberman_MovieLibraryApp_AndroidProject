package com.android.michaelzil.movieslibraryappproject_michaelzilberman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import data.DatabaseHandler;
import model.MyMovie;

// this activity is adding or editing a movie in the database:
public class AddOrEditMovieActivity extends AppCompatActivity {
    private EditText movieNameEditText;
    private EditText descriptionEditText;
    private EditText URLEditText;
    private Button okButton;
    private Button cancelButton;
    private Button showButton;
    private NetworkImageView posterNetworkImageView;
    private DatabaseHandler dba;

    private RadioGroup radioGroupWatchedOrNot;
    private RadioButton radioButtonWatched, radioButtonDidntWatch;

    private int whichIntent;
    private String watchedOrNot = ""; // if the user is not choosing watched/didn't watch - this will be by default.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_movie);
        // 0 is when a movie is added. if we come from the main activity for editing, it will become 1.
        whichIntent = 0;

        dba = new DatabaseHandler(AddOrEditMovieActivity.this);

        movieNameEditText = (EditText) findViewById(R.id.movieNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        URLEditText = (EditText) findViewById(R.id.URLEditText);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        showButton = (Button) findViewById(R.id.showButton);
        posterNetworkImageView = (NetworkImageView) findViewById(R.id.posterNetworkImageView);

        radioGroupWatchedOrNot = (RadioGroup) findViewById(R.id.radioGroupWatchedOrNot);
        radioButtonWatched = (RadioButton) findViewById(R.id.radioButtonWatched);
        radioButtonDidntWatch = (RadioButton) findViewById(R.id.radioButtonDidntWatch);

        // if movie added by internet this will get the info:
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        int movieId = -1; // this will be used only if the intent is to edit, we need to give it something
        if(bundle != null){
            whichIntent = bundle.getInt("whichIntent");
            if(whichIntent == 1){ // if the intent is for editing the movie
                movieId = bundle.getInt("movieId");

                watchedOrNot = bundle.getString("watchedOrNot");
                if(watchedOrNot.equals(getResources().getString(R.string.watched_radiobutton))){
                    radioButtonWatched.setChecked(true);
                }
                if(watchedOrNot.equals(getResources().getString(R.string.didnt_watch_radiobutton))){
                    radioButtonDidntWatch.setChecked(true);
                }
            }
            movieNameEditText.setText(bundle.getString("name"));
            descriptionEditText.setText(bundle.getString("desc"));
            URLEditText.setText(bundle.getString("poster"));
        }

        // loading an image with the URL using Volley Library:
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                posterNetworkImageView.setImageUrl(URLEditText.getText().toString(), imageLoader);
            }
        });

        // CANCEL button:
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = getIntent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });


        final int finalMovieId = movieId;
        // OK button, in order to add or edit:
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checking if the movie name field is not empty:
                if(movieNameEditText.getText().toString().isEmpty()){
                    Toast.makeText(AddOrEditMovieActivity.this,  getString(R.string.empty_movieName), Toast.LENGTH_SHORT).show();
                }else{
                    // if adding a new movie:
                    if(whichIntent == 0){
                        saveToDB(); // method is written lower.
                        finish();
                    }
                    // if editing a movie:
                    if(whichIntent == 1){
                        if(radioGroupWatchedOrNot.getCheckedRadioButtonId() == radioButtonWatched.getId()) {
                            watchedOrNot = getResources().getString(R.string.watched_radiobutton);
                        }
                        if(radioGroupWatchedOrNot.getCheckedRadioButtonId() == radioButtonDidntWatch.getId()) {
                            watchedOrNot = getResources().getString(R.string.didnt_watch_radiobutton);
                        }
                        Intent returnIntent = getIntent();
                        // updating to the database with updateMovieInDB method that is written in the DatabaseHandler:
                        dba.updateMovieInDB(movieNameEditText.getText().toString(), descriptionEditText.getText().toString(),
                                URLEditText.getText().toString(), watchedOrNot, finalMovieId);

                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }

                }

            }
        });
    }

    // we get all the info from the fields and adding a new movie to the database:
    private void saveToDB() {
        MyMovie movie = new MyMovie(movieNameEditText.getText().toString().trim(),
                descriptionEditText.getText().toString().trim(),
                URLEditText.getText().toString().trim(), 0);
        if(radioGroupWatchedOrNot.getCheckedRadioButtonId() == radioButtonWatched.getId()) {
            movie.setWatchedOrNot(getResources().getString(R.string.watched_radiobutton));
        }else{
            if(radioGroupWatchedOrNot.getCheckedRadioButtonId() == radioButtonDidntWatch.getId()) {
                movie.setWatchedOrNot(getResources().getString(R.string.didnt_watch_radiobutton));
            }else
                movie.setWatchedOrNot("");
        }

        dba.addMovieToDB(movie);

        Intent returnIntent = getIntent();
        setResult(RESULT_OK, returnIntent);
    }
}
