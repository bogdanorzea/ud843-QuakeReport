/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    ListView earthquakeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        new AddEartquakesUSGS().execute();
    }

    class AddEartquakesUSGS extends AsyncTask<Void, Void, List<Earthquake>> {
        ArrayList<Earthquake> earthquakes = null;

        @Override
        protected void onPostExecute(final List<Earthquake> earthquakes) {
            // Find a reference to the {@link ListView} in the layout
            earthquakeListView = (ListView) findViewById(R.id.list);

            // Create a new {@link ArrayAdapter} of earthquakes
            EarthquakeAdapter adapter = new EarthquakeAdapter(getApplicationContext(), earthquakes);

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(adapter);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(earthquakes.get(i).getUrl()));
                    startActivity(openUrlIntent);
                }
            });
        }

        @Override
        protected List<Earthquake> doInBackground(Void... voids) {
            return QueryUtils.extractEarthquakes();
        }
    }
}
