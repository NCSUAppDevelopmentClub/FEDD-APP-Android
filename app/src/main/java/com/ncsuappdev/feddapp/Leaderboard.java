package com.ncsuappdev.feddapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class Leaderboard extends AppCompatActivity {

    public static class LeaderboardAdapter extends BaseAdapter {
        Context context;

        public LeaderboardAdapter(Context context) {
            this.context = context;
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


        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(new LeaderboardAdapter(this));
    }

}
