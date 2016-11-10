package com.ncsuappdev.feddapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Duncan on 11/9/2016.
 */

public class LeaderboardData implements ValueEventListener {
    private static LeaderboardData instance;
    private static final String tag = "leaderboard data";

    public String[] projectNames;
    public String[][] teamNames;
    public String[][] scores;

    private DatabaseReference ref;

    private LeaderboardData() {
        ref = FirebaseDatabase.getInstance().getReference("Scores");
        ref.addValueEventListener(this);
    }

    public static void initialize() {
        instance = new LeaderboardData();
    }

    public static LeaderboardData getInstance() {
        return instance;
    }

    public void onDataChange(DataSnapshot snap) {
        projectNames = new String[(int) snap.getChildrenCount()];
        teamNames = new String[projectNames.length][];
        scores = new String[projectNames.length][];
        int i = 0;
        for (DataSnapshot ds : snap.getChildren()) {
            projectNames[i] = ds.getKey();
//            Log.e(tag, projectNames[i]);
            teamNames[i] = new String[(int) ds.getChildrenCount()];
            scores[i] = new String[teamNames[i].length];
            int j = 0;
            for (DataSnapshot d : ds.getChildren()) {
                teamNames[i][j] = d.getKey();
                scores[i][j] = d.getValue().toString();
//                Log.e(tag, teamNames[i][j] + ": " + scores[i][j]);
            }
            i++;
        }
    }

    public void onCancelled(DatabaseError databaseError) {
        Log.e(tag, "leaderboard disconnected");
    }
}
