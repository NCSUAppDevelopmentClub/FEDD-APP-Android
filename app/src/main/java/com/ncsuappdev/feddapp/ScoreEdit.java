package com.ncsuappdev.feddapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScoreEdit extends AppCompatActivity {

    public static class Entry{
        int max;
        String label;
        int value;
    }
    ArrayList<Entry> entries = new ArrayList<Entry>();

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
        Entry e = new Entry();
        e.max = 10;
        e.value = 2;
        e.label = "Im a category!";
        Entry e2 = new Entry();
        e2.max  = -1;
        e2.value = 1;
        e2.label = "Im a bonus category!";

        entries.add(e);
        entries.add(e2);
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
            return entries.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            if(i == entries.size())return null;//TODO save button
            return entries.get(i);
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
                    if (view == null) view = inflater.inflate(R.layout.score_edit_row, null);
                    ((TextView) view.findViewById(R.id.category)).setText(((Entry) o).label);
                    NumberPicker picker = (NumberPicker) view.findViewById(R.id.score);
                    picker.setMinValue(0);
                    picker.setValue(((Entry) o).value);
                    picker.setMaxValue(((Entry) o).max);
                } else{
                    //TODO not a number picker
                    if (view == null) view = inflater.inflate(R.layout.score_edit_bonus, null);
                    ((TextView) view.findViewById(R.id.category)).setText("" + ((Entry) o).label);
                    ((EditText)view.findViewById(R.id.editText)).setText("" + ((Entry) o).value);

                }
            } else{
                if(view == null) view = inflater.inflate(R.layout.score_edit_done, null);
                ((Button) view.findViewById(R.id.saveScore)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO do stuff
                    }
                });//Return the button, and add the listener to it.
            }
            return view;
        }
    }
}
