package com.ncsuappdev.feddapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.ncsuappdev.feddapp.Leaderboard3.tag;

public class ScoreList extends AppCompatActivity {


    static class ScoreEntry{
        String judge;
        int score;
    }

    ArrayList<ScoreEntry> entries = new ArrayList<ScoreEntry>();
    public ListView list;
    public Button dq;
    public Button publish;
    String project;
    String teamName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);

        project = getIntent().getStringExtra("project");
        teamName = getIntent().getStringExtra("team");

        dq = (Button) findViewById(R.id.dq);
        publish = (Button) findViewById(R.id.publish);

        //TODO if  super user:
        dq.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //Popup to confirm?
            }
        });
        publish.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //popup to confirm?
            }
        });
        // TODO else: test if it wraps
        dq.setVisibility(View.GONE);
        publish.setVisibility(View.GONE);
        //TODO end else

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teams/" + project + "/" + teamName + "/Scores");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ScoreEntry e = new ScoreEntry();
                    e.judge = ds.getKey();
                    e.score = 0;
                    for (DataSnapshot d : ds.getChildren()) e.score += (long) d.getValue();
                    entries.add(e);
                }
                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            ((TextView) ((RelativeLayout) view).findViewById(R.id.teamName)).setText(o.judge);
            ((TextView) ((RelativeLayout) view).findViewById(R.id.teamScore)).setText(o.score+"");

            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ScoreList.this, ScoreEdit.class);
                    Bundle b = new Bundle();
                    b.putString("project",project);
                    b.putString("team", teamName);
                    b.putString("judge", o.judge);
                    i.putExtras(b);
                    startActivity(i);
                    //Intent to load score edit
                }
            });

            return view;
        }
    }
}