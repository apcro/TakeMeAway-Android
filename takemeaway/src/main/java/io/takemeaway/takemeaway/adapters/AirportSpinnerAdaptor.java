package io.takemeaway.takemeaway.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.takemeaway.takemeaway.datamodels.SingleAirport;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class AirportSpinnerAdaptor extends ArrayAdapter<SingleAirport> {

    private Context context;
    private ArrayList<SingleAirport> airports;

    public AirportSpinnerAdaptor(Context context, int textViewResourceId, ArrayList<SingleAirport> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.airports = values;
    }

    public int getCount(){
        return airports.size();
    }

    public SingleAirport getItem(int position){
        return airports.get(position);
    }

    public long getItemId(int position){
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        TextView label;

        if (convertView instanceof TextView) {
            label = (TextView) convertView;
        } else {
            label = new TextView(context);
            String labelName = airports.get(position).getCity() + ", " + airports.get(position).getName();
            label.setText(labelName);
            label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label;

        if (convertView instanceof TextView) {
            label = (TextView) convertView;
        } else {
            label = new TextView(context);
            label.setTextColor(Color.BLACK);
            String labelName = airports.get(position).getCity() + " " + airports.get(position).getName();
            label.setText(labelName);
            label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            label.setPadding(pxFromDp(10f), pxFromDp(5f), pxFromDp(10f), pxFromDp(5f));
        }

        return label;
    }

    private int pxFromDp(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public void setAirport(ArrayList<SingleAirport> airports) {
        this.airports = airports;
    }
}