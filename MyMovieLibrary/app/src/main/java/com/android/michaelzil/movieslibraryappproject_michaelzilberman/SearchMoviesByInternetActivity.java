package com.android.michaelzil.movieslibraryappproject_michaelzilberman;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import model.MyMovie;

public class SearchMoviesByInternetActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ListView searchMoviesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movies_by_internet);
    }


    // method for the CANCEL button, back to the main activity of the movies list:
    public void onClickCancel(View v){
        Intent returnIntent = getIntent(); // we get the intent!
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    // method for the GO button that will search for the movies:
    public void onClickGo(View v){
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchMoviesListView = (ListView) findViewById(R.id.searchMoviesListView);

        // a progress dialog is appearing while the movies load:
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Movies...");
                // checking if the search field is not empty:
                if(searchEditText.getText().toString().equals("")) {
                    Toast.makeText(SearchMoviesByInternetActivity.this, getString(R.string.empty_search), Toast.LENGTH_LONG).show();
                }else {
                    String searchedMovieName = searchEditText.getText().toString();

                    String tag_json_obj = "json_obj_req"; // Tag used to cancel the request

                    String url = "https://api.themoviedb.org/3/search/movie?api_key=a76877bb6cc720e8564ffd0f13d6279a&language=en-US&query="
                            + searchedMovieName + "&page=1&include_adult=false";

                    pDialog.show();

                    // using the Volley Library to get the JSON object:
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            url, (String) null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            final ArrayList<String> searchedMoviesNames = new ArrayList<>(); // the searched movies gonna be here
                            final ArrayList<MyMovie> searchedMovies = new ArrayList<>(); // the movies objects here
                            try {
                                JSONArray searchedMoviesJSONArray = response.getJSONArray("results");

                                for (int i = 0; i < searchedMoviesJSONArray.length(); i++) {
                                    try {
                                        JSONObject movieJSONObject = searchedMoviesJSONArray.getJSONObject(i);
                                        String movieName = movieJSONObject.getString("title");
                                        searchedMoviesNames.add(movieName);
                                        String movieDesc = movieJSONObject.getString("overview");
                                        String poster = movieJSONObject.getString("poster_path");

                                        MyMovie movie = new MyMovie(movieName, movieDesc, poster, 0);
                                        searchedMovies.add(movie);

                                        pDialog.hide();
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                                // using built-in adapter for the searched movies list:
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, searchedMoviesNames) {
                                    // changing the getView method of this adapter to set the TextColor to black:
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK); // here.
                                        return view;
                                    }
                                };

                                searchMoviesListView.setAdapter(adapter); // setting the adapter.


                                // setting onClick on item in the list, so we take all the data of this movie to the AddOrEditMovieActivity:
                                searchMoviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        String name = searchedMovies.get(i).getMovieName();
                                        String desc = searchedMovies.get(i).getMovieDescription();
                                        String poster = searchedMovies.get(i).getMoviePoster();
                                        poster = "https://image.tmdb.org/t/p/w600_and_h900_bestv2" + poster;

                                        Intent intent = new Intent(SearchMoviesByInternetActivity.this, AddOrEditMovieActivity.class);
                                        intent.putExtra("name", name);
                                        intent.putExtra("desc", desc);
                                        intent.putExtra("poster", poster);
                                        intent.putExtra("whichIntent", 0);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.hide();
                        }
                    });

                    AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

                }
    }
}








