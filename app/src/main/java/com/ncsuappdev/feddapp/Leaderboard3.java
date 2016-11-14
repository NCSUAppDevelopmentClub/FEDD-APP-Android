package com.ncsuappdev.feddapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Leaderboard3 extends AppCompatActivity {
    public static class LeaderboardAdapter extends BaseAdapter {
        private static LayoutInflater inflater;
        Context context;

        public LeaderboardAdapter(Context context) {

            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount(){
            int sum = 0;
            LeaderboardData data = LeaderboardData.getInstance();
            for(int i = 0; i < data.projectNames.length; i++){
                sum += data.teamNames[i].length;
            }
            return sum;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if(vi == null) vi = inflater.inflate(R.layout.row, null);



            TextView txt = (TextView) vi.findViewById(R.id.text);

            int sum = 0;
            LeaderboardData data = LeaderboardData.getInstance();
            int x = 0;
            for(; sum < position; x++){
                sum += data.teamNames[x].length;
            }

            sum -= data.teamNames[x].length;
            if(x == data.projectNames.length)return null;
            String s = data.teamNames[x][position - sum - 1];
            s += " "+data.scores[x][position - sum - 1];

            txt.setText(s);
//
//            ViewGroup.LayoutParams params = vi.getLayoutParams();
//
//            // Set the height of the Item View
//            params.height = 40;

            return vi;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard3);

        ListView list = (ListView) findViewById(R.id.list3);
        list.setAdapter(new LeaderboardAdapter(this));
    }
}
