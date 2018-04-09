package com.android.michaelzil.movieslibraryappproject_michaelzilberman;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import model.MyMovie;

// the adapter for the movies list in the main activity:
public class MovieAdapter extends ArrayAdapter<MyMovie> {
    Activity activity;
    int layoutResource;
    ArrayList<MyMovie> mData = new ArrayList<>();
    public MovieAdapter(@NonNull Activity act, int resource, ArrayList<MyMovie> data) {
        super(act, resource, data);
        activity = act;
        layoutResource = resource;
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Nullable
    @Override
    public MyMovie getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getPosition(@Nullable MyMovie item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // this are the views that's gonna be in each row of the main movie list:
    class ViewHolder{
        MyMovie myMovie;
        TextView mMovieName;
        TextView mWatchedOrNot;
        TextView mDate;
        com.android.volley.toolbox.NetworkImageView mMoviePoster;
    }

    // now we'll set everything with this Overwritten method of getView:
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;

        if ( row == null || (row.getTag()) == null){
            LayoutInflater inflater = LayoutInflater.from(activity);

            row = inflater.inflate(layoutResource, null);
            holder = new ViewHolder();

            holder.mMovieName = (TextView) row.findViewById(R.id.movieNameTextView);
            holder.mWatchedOrNot = (TextView) row.findViewById(R.id.watchedOrNotTextView);
            holder.mMoviePoster = (NetworkImageView) row.findViewById(R.id.movieThumbnailImageView);
            holder.mDate = (TextView) row.findViewById(R.id.dateMovieAddedTextView);

            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        holder.myMovie = getItem(position);
        holder.mMovieName.setText(holder.myMovie.getMovieName());
        holder.mMovieName.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        holder.mWatchedOrNot.setText(holder.myMovie.getWatchedOrNot());
        // dynamic color for watchedOrNot: watched = blue , did'nt watch = red:
        if (holder.myMovie.getWatchedOrNot().equals(getContext().getResources().getString(R.string.watched_radiobutton))) {
            holder.mWatchedOrNot.setTextColor(Color.BLUE);
        }else
            holder.mWatchedOrNot.setTextColor(Color.RED);
        holder.mDate.setText(holder.myMovie.getDateMovieAdded());

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        holder.mMoviePoster.setImageUrl(holder.myMovie.getMoviePoster(), imageLoader);

        return row;
    }
}
