package com.ncsuappdev.feddapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreList extends AppCompatActivity {


    static class ScoreEntry{
        String judge;
        int score;
    }

    ArrayList<ScoreEntry> entries = new ArrayList<ScoreEntry>();
    public ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);
        Bundle b = getIntent().getExtras();

        String project = b.getString("project");
        String teamName = b.getString("team");

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(new ScoreListAdapter(this));
    }

    public class ScoreListAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        Context context;

        public ScoreListAdapter(Context context){
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount(){
            return entries.size();
        }

        public Object getItem(int i){
            return entries.get(i);
        }

        public long getItemId(int i){
            return i;
        }

        public View getView(int position, View view, ViewGroup parent){
            if (view == null) view = inflater.inflate(R.layout.leaderboard_team, null);

            final ScoreEntry o = entries.get(position);
            ((TextView) ((RelativeLayout) view).getChildAt(0).findViewById(R.id.teamName)).setText(o.judge);
            ((TextView) ((RelativeLayout) view).getChildAt(0).findViewById(R.id.teamScore)).setText(o.score);

            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    //Intent to load score edit
                }
            });

            return view;
        }
    }
}
