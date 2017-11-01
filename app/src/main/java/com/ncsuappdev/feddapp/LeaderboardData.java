package com.ncsuappdev.feddapp;

import android.util.Log;
import android.widget.BaseAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Duncan on 11/9/2016.
 */

public class LeaderboardData {
    private static LeaderboardData instance;
    private static final String tag = "leaderboard data";

    /** List with String headers and Team entries */
    public ArrayList morning, afternoon;

    private LeaderboardData() {
        morning = new ArrayList();
        afternoon = new ArrayList();
        FirebaseDatabase.getInstance().getReference("Morning Scores")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LeaderboardData.this.onDataChange(dataSnapshot, morning);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance().getReference("Afternoon Scores")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LeaderboardData.this.onDataChange(dataSnapshot, afternoon);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void initialize() {
        instance = new LeaderboardData();
    }

    public static LeaderboardData getInstance() {
        return instance;
    }

    public void onDataChange(DataSnapshot snap, ArrayList list) {
        list.clear();
        ArrayList<Team> cat = new ArrayList<Team>();
        for (DataSnapshot ds : snap.getChildren()) {
            list.add(ds.getKey());
            cat.clear();
            for (DataSnapshot d : ds.getChildren()) {
                String score = d.getValue().toString();
                try {
                    int s = Integer.parseInt(score);
                    if (s == -2) score = "DQ";
                    else if (s < 0) score = "----";
                } catch (Exception e) {}
                cat.add(new Team(d.getKey(), score, ds.getKey()));
            }
            Collections.sort(cat);
            list.addAll(cat);
        }
//        Log.e(tag, list.toString());
//        Log.e(tag, "data changed!");
        // tell the list to update
        Leaderboard3 l = Leaderboard3.instance;
        if (l != null) {
            ((BaseAdapter) l.list.getAdapter()).notifyDataSetChanged();
//            Log.e(tag, "updated list view");
        }
    }

    public class Team implements Comparable<Team> {
        public String name;
        public String score;
        public String project;

        public Team(String name, String score, String project) {
            this.name = name;
            this.score = score;
            this.project = project;
        }

        public String toString() {
            return name + ": " + score;
        }

        public int compareTo(Team t) {
            boolean ds = false, tds = false;
            double s = 0, ts = 0;
            try {
                s = Double.parseDouble(score);
                ds = true;
            } catch (Exception e) {}
            try {
                ts = Double.parseDouble(t.score);
                tds = true;
            } catch (Exception e) {}
            if (ds) {
                if (tds) return (int) Math.signum(ts - s);
                return -1;
            } else {
                if (tds) return 1;
                return score.compareTo(t.score);
            }
        }
    }

    public void onCancelled(DatabaseError databaseError) {
//        Log.e(tag, "leaderboard disconnected");
    }
}
