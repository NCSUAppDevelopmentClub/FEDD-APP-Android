package com.ncsuappdev.feddapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Leaderboard3 extends AppCompatActivity {
    public static String tag = "Leaderboard3";
    public static Leaderboard3 instance;
    public ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard3);

        ActionBar bar = this.getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        list = (ListView) findViewById(R.id.list3);
        list.setAdapter(new LeaderboardAdapter(this));

        instance = this;
    }

    public  class LeaderboardAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        Context context;

        public LeaderboardAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount(){
            return LeaderboardData.getInstance().list.size();
        }

        @Override
        public Object getItem(int i) {
            /*
            int sum = 0;
            LeaderboardData data = LeaderboardData.getInstance();
            int x = 0;
            for(; sum < i; x++){
                sum += data.teamNames[x].length;
            }
            sum -= data.teamNames[x].length;
            if(x == data.projectNames.length)return null;
            return data.scores[x-1][i - sum];
            */
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) view = inflater.inflate(R.layout.leaderboard_row, null);

            final Object o = LeaderboardData.getInstance().list.get(position);
            if (o instanceof LeaderboardData.Team) {
                ((TextView) ((RelativeLayout) view).getChildAt(0).findViewById(R.id.teamName)).setText(((LeaderboardData.Team) o).name);
                ((TextView) ((RelativeLayout) view).getChildAt(0).findViewById(R.id.teamScore)).setText(((LeaderboardData.Team) o).score);
                view.findViewById(R.id.team).setVisibility(View.VISIBLE);
                view.findViewById(R.id.title).setVisibility(View.GONE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO If authenticated, start actitivy
                        Log.e(tag, "clicked on " + ((LeaderboardData.Team) o).name);
                        Intent i = new Intent(Leaderboard3.this, ScoreList.class)
                                .putExtra("project", ((LeaderboardData.Team) o).project)
                                .putExtra("team", ((LeaderboardData.Team) o).name);
                        startActivity(i);
                    }
                });
            } else {
                ((TextView) ((RelativeLayout) view).getChildAt(1)).setText((String) o);
                view.findViewById(R.id.title).setVisibility(View.VISIBLE);
                view.findViewById(R.id.team).setVisibility(View.GONE);
            }
            return view;
        }
    }

}
