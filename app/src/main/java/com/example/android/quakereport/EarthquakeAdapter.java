package com.example.android.quakereport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Earthquake eq = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_item, parent, false);
        }

        // Lookup view for data population
        TextView magnitude = (TextView) convertView.findViewById(R.id.magnitude_text);
        TextView location = (TextView) convertView.findViewById(R.id.location_text);
        TextView date = (TextView) convertView.findViewById(R.id.date_text);

        //Populate data
        magnitude.setText(String.format(Locale.ENGLISH, "%.1f", eq.mMagnitude));
        location.setText(eq.mLocation);
        date.setText(eq.mDate);

        // Return the completed view
        return convertView;
    }
}
