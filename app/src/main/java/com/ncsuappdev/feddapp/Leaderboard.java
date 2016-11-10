package com.ncsuappdev.feddapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class Leaderboard extends AppCompatActivity {

    public static class LeaderboardAdapter extends BaseAdapter {
        public LeaderboardAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public LeaderboardAdapter(Context context, int resource, List<Leaderboard> items) {
            super(context, resource, items);
        }

        public int getCount(){
            return LeaderboardData.getInstance().scores.length;
        }

        @Override
        public Object getItem(int i) {
            return LeaderboardData.getInstance().scores[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ListView list;
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(new LeaderboardAdapter(this, R.layout.content_leaderboard, ));
    }

}
