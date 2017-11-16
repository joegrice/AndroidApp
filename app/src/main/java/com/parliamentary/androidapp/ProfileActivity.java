package com.parliamentary.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView textViewProfileTitle;
    private Button buttonLogout;
    private Button buttonListPage;
    private Button buttonMpPage;
    private Button buttonFavouritePage;
    private Button buttonProfilePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = mAuth.getCurrentUser();

        textViewProfileTitle = (TextView) findViewById(R.id.textViewProfileTitle);
        textViewProfileTitle.setText("Welcome " + user.getEmail());
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonListPage = (Button) findViewById(R.id.buttonListPage);
        buttonMpPage = (Button) findViewById(R.id.buttonMpPage);
        buttonFavouritePage = (Button) findViewById(R.id.buttonFavouritePage);
        buttonProfilePage = (Button) findViewById(R.id.buttonProfilePage);

        buttonLogout.setOnClickListener(this);
        buttonListPage.setOnClickListener(this);
        buttonMpPage.setOnClickListener(this);
        buttonFavouritePage.setOnClickListener(this);
        buttonProfilePage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout) {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == buttonListPage) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
