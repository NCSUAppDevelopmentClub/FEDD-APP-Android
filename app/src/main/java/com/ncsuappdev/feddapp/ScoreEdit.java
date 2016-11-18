package com.ncsuappdev.feddapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.ncsuappdev.feddapp.Leaderboard3.tag;

public class ScoreEdit extends AppCompatActivity {

    public static class Entry{
        int max;
        String label;
        int value;

        public String toString() {
            return label + ": " + value;
        }
    }
    ArrayList<Entry> entries = new ArrayList<Entry>();

    String project;
    String team;
    String judge = "";
    ListView list;
    boolean edit;
    String oldKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_edit);

        ActionBar bar = this.getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        Bundle b = getIntent().getExtras();
        project = b.getString("project");
        team = b.getString("team");
        edit = b.getBoolean("edit");

        if (edit)
            judge = oldKey = b.getString("judge");

        FirebaseDatabase.getInstance().getReference("Projects/" + project)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                entries.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Entry e = new Entry();
                    e.max = (int) (long) ds.child("max").getValue();
                    e.label = (String) ds.child("name").getValue();
                    entries.add(e);
                }
                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();

                Log.e(tag, "Teams/" + project + "/" + team + "/Scores/" + judge);
                FirebaseDatabase.getInstance().getReference("Teams/" + project + "/" + team + "/Scores/" + judge)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    int index = Integer.parseInt(ds.getKey());
                                    if (index >= 0 && index < entries.size())
                                        entries.get(index).value = (int) (long) ds.getValue();
                                }
                                Log.e(tag, entries.toString());
                                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //list of entry rows, with size equal to the size of labels, which is the list of subcategories retrieved.
        list = (ListView) findViewById(R.id.entryList);
        list.setAdapter(new EditAdapter(this));
    }

    //TODO add save button to end of list
    public class EditAdapter extends BaseAdapter{
        LayoutInflater inflater;
        Context context;
        public EditAdapter(Context context){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
        }
        @Override
        public int getCount() {
            return entries.size() + 2;
        }

        @Override
        public Object getItem(int i) {
            if(i == 0)return null;//Label
            if(i == entries.size()+1)return null;//TODO save button
            return entries.get(i - 1);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            Object o = getItem(i);
            if(o instanceof Entry){
                if(((Entry) o).max > 0) {
                    view = inflater.inflate(R.layout.score_edit_row, null);
                    ((TextView) view.findViewById(R.id.category)).setText(((Entry) o).label);
                    NumberPicker picker = (NumberPicker) view.findViewById(R.id.score);
                    picker.setMinValue(0);
                    picker.setMaxValue(((Entry) o).max);
                    picker.setValue(((Entry) o).value);
                    final Entry e = (Entry) o;
                    picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            e.value = newVal;
                        }
                    });
                } else{
                    view = inflater.inflate(R.layout.score_edit_bonus, null);
                    ((TextView) view.findViewById(R.id.category)).setText("" + ((Entry) o).label);
                    ((EditText)view.findViewById(R.id.editText)).setText("" + ((Entry) o).value);

                }
            } else{
                if(i == 0){
                    view = inflater.inflate(R.layout.score_edit_label, null);
                    EditText lab = ((EditText) view.findViewById(R.id.editText));
                    Log.e("HERE", lab.getText().toString());
                    if (!lab.getText().toString().equals(judge)) lab.setText(judge);
                    lab.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    lab.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if(i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_UNSPECIFIED){
                                judge = textView.getText().toString();
//                            return true;
                            }
                            return false;
                            //return true;
                        }
                    });
                    lab.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            judge = ((EditText)view).getText().toString();
                        }
                    });

                    //TODO set the text to append a number if multiple of the same
                } else{
                    view = inflater.inflate(R.layout.score_edit_done, null);
                    ((Button) view.findViewById(R.id.saveScore)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO do stuff
                        }
                    });//Return the button, and add the listener to it.
                }

            }
            return view;
        }
    }
}
