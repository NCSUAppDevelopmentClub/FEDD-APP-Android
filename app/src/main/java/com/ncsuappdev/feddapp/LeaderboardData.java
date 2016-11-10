package com.ncsuappdev.feddapp;

/**
 * Created by Duncan on 11/9/2016.
 */

public class LeaderboardData {
    private static LeaderboardData instance;

    public String[] projectNames;
    public String[][] teamNames;
    public String[][] scores;

    private LeaderboardData() {};

    public static LeaderboardData getInstance() {
        if (instance == null) instance = new LeaderboardData();
        return instance;
    }


}
