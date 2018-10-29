package com.example.kuba.raczejpiatek;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.login.LoginActivity;
import com.example.kuba.raczejpiatek.register.RegisterActivity;
import com.example.kuba.raczejpiatek.user.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ProfilActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView first_nameTextView;
    private TextView last_nameTextView;
    private ImageView profilURL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

    }


}
