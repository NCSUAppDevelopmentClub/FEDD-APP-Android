package com.ncsuappdev.feddapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.R.attr.label;
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

        getSupportActionBar().setTitle("Score Entry");

        if (edit)
            judge = oldKey = b.getString("judge");

        final EditText lab = ((EditText) findViewById(R.id.editText2));
//        Log.e("HERE", lab.getText().toString());
        if (!lab.getText().toString().equals(judge)) lab.setText(judge);
        lab.setImeOptions(EditorInfo.IME_ACTION_DONE);
        lab.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                judge = lab.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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





        FirebaseDatabase.getInstance().getReference("Projects/" + project)
                .addValueEventListener(new ValueEventListener() {
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

                if (!edit) return;

                FirebaseDatabase.getInstance().getReference("Teams/" + project + "/" + team + "/Scores/" + judge)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    int index = Integer.parseInt(ds.getKey());
                                    if (index >= 0 && index < entries.size())
                                        entries.get(index).value = (int) (long) ds.getValue();
                                }
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
        list.setItemsCanFocus(true);
    }

    private void submit() {
        if (edit) FirebaseDatabase.getInstance()
                .getReference("Teams/" + project + "/" + team + "/Scores/" + oldKey)
                .removeValue();
        ArrayList list = new ArrayList();
        for (int i = 0; i < entries.size(); i++)
            list.add(entries.get(i).value);
        FirebaseDatabase.getInstance()
                .getReference("Teams/" + project + "/" + team + "/Scores/" + judge.trim())
                .setValue(list);
        finish();
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


            final Object o = getItem(i);
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
                    final EditText et = ((EditText)view.findViewById(R.id.editText));

                    et.setText("" + ((Entry) o).value);

                    et.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                ((Entry) o).value = Integer.parseInt(et.getText().toString());
                                et.setTextColor(Color.BLACK);

                            }catch(Exception e){
                                et.setTextColor(Color.RED);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                }
            } else{

                    view = inflater.inflate(R.layout.score_edit_done, null);
                    ((Button) view.findViewById(R.id.saveScore)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(judge == null || judge.trim().isEmpty() ){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ScoreEdit.this);
                                builder1.setMessage("Please enter a score label at the top.");
                                builder1.setCancelable(false);
                                builder1.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            } else if(!edit){
                                FirebaseDatabase.getInstance().getReference("Teams/" + project + "/" + team + "/Scores").child(judge.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ScoreEdit.this);
                                            builder1.setMessage("Score label exists. Please change the label.");
                                            builder1.setCancelable(false);
                                            builder1.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                        } else{
                                            submit();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else submit();

                        }
                    });//Return the button, and add the listener to it.
                //}

            }
            return view;
        }
    }
}
