package com.ncsuappdev.feddapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import org.w3c.dom.Text;

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
    double average = 0;
    String project;
    String teamName;
    boolean morning;
    TextView avScore;
    TextView scoreStatus;
    boolean published = false, dqed = false;
//
     @Override
     public boolean onCreateOptionsMenu(Menu menu){
         super.onCreateOptionsMenu(menu);
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.add_menu,  menu);
         return true;
     }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            //process your onClick here
            if (published) {
                cannotEdit();
                return true;
            }

            Intent i = new Intent(ScoreList.this, ScoreEdit.class);
            Bundle b = new Bundle();
            b.putString("project",project);
            b.putString("team", teamName);
            b.putBoolean("edit", false);
            i.putExtras(b);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score_list);

        project = getIntent().getStringExtra("project");
        teamName = getIntent().getStringExtra("team");
        morning = getIntent().getBooleanExtra("morning", false);

        ActionBar bar = this.getSupportActionBar();
        bar.setTitle(teamName);
        bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
//        bar.

        dq = (Button) findViewById(R.id.dq);
        publish = (Button) findViewById(R.id.publish);

        //TODO if  super user:
        dq.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ScoreList.this);
                builder1.setMessage((dqed ? "Undo disqualification of " : "Disqualify ") + teamName + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dqTeam();
                                dialog.dismiss();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        publish.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //popup to confirm?
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ScoreList.this);
                builder1.setMessage(published
                        ? "Hide " + teamName + "? This will remove their score from the public leaderboard."
                        : "Publish " + teamName + "? This will show their score on the public leaderboard.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                publishScore();
                                dialog.dismiss();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        // TODO else: toggle_menu if it wraps
       if(!MainActivity.signedIn) {
           dq.setVisibility(View.GONE);
           publish.setVisibility(View.GONE);
       }
        //TODO end else

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teams/" + project + "/" + teamName + "/Scores");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                entries.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ScoreEntry e = new ScoreEntry();
                    e.judge = ds.getKey();
                    e.score = 0;
                    for (DataSnapshot d : ds.getChildren()) e.score += (long) d.getValue();
                    entries.add(e);
                }
                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                calculateScore();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference(scoreTable() + project + "/" + teamName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String score = "" + dataSnapshot.getValue();
                        if (score.startsWith("-")) {
                            published = false;
                            publish.setText("publish score");
                        } else {
                            published = true;
                            publish.setText("hide team score");
                        }
                        if (score.equals("-2")) {
                            dqed = true;
                            publish.setEnabled(false);
                            dq.setText("undo dq");
                            scoreStatus.setText("Disqualified");
                        } else {
                            dqed = false;
                            publish.setEnabled(true);
                            dq.setText("disqualify team");
                            if (published) scoreStatus.setText("Score Published");
                            else scoreStatus.setText("Score Pending");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        avScore = (TextView) findViewById(R.id.average);
        scoreStatus = (TextView) findViewById(R.id.scoreStatus);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(new ScoreListAdapter(this));
        registerForContextMenu(list);


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
            ((TextView) ((RelativeLayout) view).findViewById(R.id.teamScore)).setText(o.score + "");

            if (Build.VERSION.SDK_INT >= 16)
                view.setBackground(obtainStyledAttributes(new int[] {R.attr.selectableItemBackground}).getDrawable(0));
            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if (published) {
                        cannotEdit();
                        return;
                    }

                    Intent i = new Intent(ScoreList.this, ScoreEdit.class);
                    Bundle b = new Bundle();
                    b.putString("project",project);
                    b.putString("team", teamName);
                    b.putString("judge", o.judge);
                    b.putBoolean("edit", true);
                    i.putExtras(b);
                    startActivity(i);
                    //Intent to load score edit
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    Log.e("LONG", "Long click performed!"); //DONT DELETE THIS! for somereason the context menu only works with this
                    return false;
                }
            });
            return view;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(entries.get(info.position).judge);
            menu.add(Menu.NONE, 0, 0, "Delete");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete")){
            if (published) cannotEdit();
            else deleteEntry(((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position);
        }
        return false;
    }
    public void calculateScore(){
        average = 0;
        if (entries.size() == 0) return;
        for(ScoreEntry e: entries){
            average += e.score;
        }
        average /= entries.size();
        avScore.setText("Current Score: " + average);
    }

    public void deleteEntry(int index) {
        FirebaseDatabase.getInstance()
                .getReference("Teams/" + project + "/" + teamName + "/Scores/" + entries.get(index).judge)
                .removeValue();
    }

    public void dqTeam() {
        if (dqed) {
            FirebaseDatabase.getInstance()
                    .getReference(scoreTable() + project + "/" + teamName)
                    .setValue(-1);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(scoreTable() + project + "/" + teamName)
                    .setValue(-2);
        }
    }

    public void publishScore() {
        if (!published) {
            calculateScore();
            FirebaseDatabase.getInstance()
                    .getReference(scoreTable() + project + "/" + teamName)
                    .setValue(average);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference(scoreTable() + project + "/" + teamName)
                    .setValue(-1);
        }
    }

    public String scoreTable() {
        return (morning ? "Morning" : "Afternoon") + " Scores/";
    }

    public void cannotEdit() {
        new AlertDialog.Builder(this)
                .setMessage("Cannot edit scores while team is published to the leaderboard. " +
                        "Please hide the team's score first.")
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .create().show();
    }
}
