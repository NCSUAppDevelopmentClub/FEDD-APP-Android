package com.ncsuappdev.feddapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ScoreEdit extends AppCompatActivity {

    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<Integer> scores = new ArrayList<Integer>();

    String project;
    String team;
    String judge;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_edit);
        Bundle b = getIntent().getExtras();
        project = b.getString("project");
        team = b.getString("team");
        judge = b.getString("judge");
        //list of entry rows, with size equal to the size of labels, which is the list of subcategories retrieved.
    }
}
