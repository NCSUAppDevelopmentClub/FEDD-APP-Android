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

public class LeaderboardData implements ValueEventListener {
    private static LeaderboardData instance;
    private static final String tag = "leaderboard data";

    /** List with String headers and Team entries */
    public ArrayList list;

    private DatabaseReference ref;

    private LeaderboardData() {
        ref = FirebaseDatabase.getInstance().getReference("Scores");
        ref.addValueEventListener(this);
        list = new ArrayList();
    }

    public static void initialize() {
        instance = new LeaderboardData();
    }

    public static LeaderboardData getInstance() {
        return instance;
    }

    public void onDataChange(DataSnapshot snap) {
        list.clear();
        ArrayList<Team> cat = new ArrayList<Team>();
        for (DataSnapshot ds : snap.getChildren()) {
            list.add(ds.getKey());
            cat.clear();
            for (DataSnapshot d : ds.getChildren())
                cat.add(new Team(d.getKey(), d.getValue().toString(), ds.getKey()));
            Collections.sort(cat);
            list.addAll(cat);
        }
//        Log.e(tag, list.toString());
        Log.e(tag, "data changed!");
        // tell the list to update
        Leaderboard3 l = Leaderboard3.instance;
        if (l != null) {
            ((BaseAdapter) l.list.getAdapter()).notifyDataSetChanged();
            Log.e(tag, "updated list view");
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
        Log.e(tag, "leaderboard disconnected");
    }
}
