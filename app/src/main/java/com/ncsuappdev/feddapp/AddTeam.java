package com.ncsuappdev.feddapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTeam extends AppCompatActivity {
    Spinner timeSpinner, projectSpinner;
    EditText teamName;
    List<CharSequence> projects = new ArrayList<CharSequence>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        bar.setTitle("Create Team");

        timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
        timeSpinner.setSelection(getIntent().getBooleanExtra("morning", false) ? 0 : 1);

        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, projects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner = (Spinner) findViewById(R.id.projectSpinner);
        projectSpinner.setAdapter(adapter);

        teamName = (EditText) findViewById(R.id.teamName);

        FirebaseDatabase.getInstance().getReference("Projects")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        projects.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                            projects.add(ds.getKey());
                        ((BaseAdapter) projectSpinner.getAdapter()).notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        ((Button) findViewById(R.id.createTeam)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String time = (String) timeSpinner.getSelectedItem();
                final String project = (String) projectSpinner.getSelectedItem();
                final String name = teamName.getText().toString().trim();
                if (project == null) {
                    new AlertDialog.Builder(AddTeam.this)
                            .setMessage("Please select a project.")
                            .setPositiveButton("OK", null)
                            .create().show();
                } else if (name.length() == 0) {
                    new AlertDialog.Builder(AddTeam.this)
                            .setMessage("Team name cannot be blank.")
                            .setPositiveButton("OK", null)
                            .create().show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference(time + " Scores/" + project)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(name).exists()) {
                                        new AlertDialog.Builder(AddTeam.this)
                                                .setMessage("Team name already exists.")
                                                .setPositiveButton("OK", null)
                                                .create().show();
                                    } else {
                                        FirebaseDatabase.getInstance()
                                                .getReference(time + " Scores/" + project + "/" + name)
                                                .setValue(-1);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
    }
}
