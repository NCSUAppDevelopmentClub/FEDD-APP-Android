package com.ncsuappdev.feddapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ResultCallback<GoogleSignInResult> {
    private static final int RC_SIGN_IN = 0;
    final String tag = "main activity";
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private Button signOutButton;
    private Button deleteThis;
    public static boolean signedIn = false;
    public static String email;
    private ArrayList<String> validEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21)
            this.getWindow().setStatusBarColor(Color.rgb(200,0,0));
        ActionBar bar = this.getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).setResultCallback(MainActivity.this);


        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ;
        }catch(Exception e){
            e.printStackTrace();
        }

     //   FirebaseApp.initializeApp(this);
        /*
        FirebaseDatabase.getInstance().getReference("Emails")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                validEmails = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    validEmails.add((String) ds.getValue());
                if (signedIn && !validEmails.contains(email)) {
//                    Log.e(tag, "undoing silent sign in");
                    signOut();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        LeaderboardData.initialize();

        signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);
        signInButton = (SignInButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        signOutButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.loginStatus).setVisibility(View.INVISIBLE);

        deleteThis = (Button) findViewById(R.id.button2);
        deleteThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Leaderboard3.class);
                startActivity(i);
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProjectSelect.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.signOutButton:
                new AlertDialog.Builder(this)
                        .setCancelable(true)
                        .setMessage("Are you sure you want to sign out?")
                        .setNeutralButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                            }
                        }).create().show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            handleSignIn(Auth.GoogleSignInApi.getSignInResultFromIntent(data), false);
        }
    }

    @Override
    public void onResult(@NonNull GoogleSignInResult result) {
        handleSignIn(result, true);
    }

    public void handleSignIn(GoogleSignInResult result, boolean silent) {
        if (result.isSuccess()) {
            String email = result.getSignInAccount().getEmail();
            if (validEmails == null && !silent) {
                signOut();
                new AlertDialog.Builder(this)
                        .setMessage("Failed to contact server.")
                        .setPositiveButton("OK", null)
                        .create().show();
            } else if (silent || validEmails.contains(email)) {
//                Log.e(tag, "logged in with " + result.getSignInAccount().getEmail());
                findViewById(R.id.signInButton).setVisibility(View.INVISIBLE);
                findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);
                findViewById(R.id.loginStatus).setVisibility(View.VISIBLE);
                signedIn = true;
                MainActivity.email = email;
                ((TextView) findViewById(R.id.loginStatus)).setText(result.getSignInAccount().getEmail());
            } else {
                signOut();
                new AlertDialog.Builder(this)
                        .setMessage(email + "\nis not authorized")
                        .setCancelable(true)
                        .setPositiveButton("OK", null)
                        .create().show();
            }
        } else {
//            Log.e(tag, "login failed");
            signOut();
        }
    }

    private void signOut() {
//        Log.e(tag, "sign out");
        findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
        findViewById(R.id.signOutButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.loginStatus).setVisibility(View.INVISIBLE);
        signedIn = false;
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.e(tag, "connection failed");
    }
}
