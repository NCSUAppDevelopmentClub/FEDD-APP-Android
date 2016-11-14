package com.ncsuappdev.feddapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ResultCallback<GoogleSignInResult> {
    private static final int RC_SIGN_IN = 0;
    final String tag = "main activity";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    private Button signOutButton;
    private Button deleteThis;
    private boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).setResultCallback(this);

        LeaderboardData.initialize();

        signOutButton = (Button) findViewById(R.id.signout);
        signOutButton.setOnClickListener(this);
        signInButton = (SignInButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        deleteThis = (Button) findViewById(R.id.button2);
        deleteThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Leaderboard3.class);
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
            case R.id.signout:
                signOut();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            onResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    @Override
    public void onResult(@NonNull GoogleSignInResult result) {
        if (result.isSuccess()) {
            Log.e(tag, "logged in with " + result.getSignInAccount().getEmail());
            findViewById(R.id.signInButton).setEnabled(false);
            findViewById(R.id.signout).setEnabled(true);
            signedIn = true;
            ((TextView) findViewById(R.id.loginstatus)).setText("Logged in as " + result.getSignInAccount().getEmail());
        } else {
            Log.e(tag, "login failed");
            signOut();
        }
    }

    private void signOut() {
        Log.e(tag, "sign out");
        findViewById(R.id.signInButton).setEnabled(true);
        findViewById(R.id.signout).setEnabled(false);
        signedIn = false;
        ((TextView) findViewById(R.id.loginstatus)).setText("Logged out");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(tag, "connection failed");
    }
}
