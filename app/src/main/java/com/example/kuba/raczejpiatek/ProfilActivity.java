package com.example.kuba.raczejpiatek;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ProfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "ViewDatabase";
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private TextView emailTextView;
    private TextView first_nameTextView;
    private TextView last_nameTextView;
    private TextView genderTextView;
    private ImageView profilURL;
    private TextView nickNameTextView;
    private TextView phoneNumberTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        init();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference();
        FirebaseUser user=mAuth.getCurrentUser();
        userID=user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user !=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
            }

        };

            myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {



        for(DataSnapshot ds : dataSnapshot.getChildren()){

            User uInfo = new User();
            uInfo.setFirst_name(ds.child(userID).getValue(User.class).getFirst_name()); //set the name
            uInfo.setLast_name(ds.child(userID).getValue(User.class).getLast_name()); //set the name
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail()); //set the email
            uInfo.setGender(ds.child(userID).getValue(User.class).getGender());
            uInfo.setProfilURl(ds.child(userID).getValue(User.class).getProfilURl());
            uInfo.setUsername(ds.child(userID).getValue(User.class).getUsername());
            uInfo.setPhone(ds.child(userID).getValue(User.class).getPhone());


            //display all the information
            Log.d(TAG, "showData: name: " + uInfo.getFirst_name());
            Log.d(TAG, "showData: name: " + uInfo.getLast_name());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: phone_num: " + uInfo.getPhone());
            Log.d(TAG, "showData: profile: " +uInfo.getProfilURl());


            emailTextView.setText(uInfo.getEmail());
            if(uInfo.getGender()==null) {
                genderTextView.setText("Nie podano płci");
            }
            else {
                genderTextView.setText(uInfo.getGender());
            }
            if(uInfo.getFirst_name()==null) {
                first_nameTextView.setText("Nie podano imienia");
            }
            else {
                first_nameTextView.setText(uInfo.getFirst_name());
            }
            if(uInfo.getLast_name()==null) {
                last_nameTextView.setText("Nie podano nazwiska");
            }
            else {
                last_nameTextView.setText(uInfo.getLast_name());
            }
            if(uInfo.getUsername()==null) {
                nickNameTextView.setText("Nie podano nicku");
            }
            else {
                nickNameTextView.setText(uInfo.getUsername());
            }
            if(uInfo.getPhone()==null) {
                phoneNumberTextView.setText("Nie podano numeru");
            }
            else {
            phoneNumberTextView.setText(uInfo.getPhone());
        }
            if(uInfo.getProfilURl()!=null) {
                Picasso.with(this).load(uInfo.getProfilURl()).into(profilURL);
            }

        }
    }





    private void init() {
        emailTextView =  findViewById(R.id.txtEmail);
        first_nameTextView =  findViewById(R.id.txtFirstName);
        last_nameTextView = findViewById(R.id.txtLastName);
        nickNameTextView = findViewById(R.id.txtUsername);
        phoneNumberTextView =  findViewById(R.id.txtPhoneNumber);
        genderTextView=  findViewById(R.id.txtGender);
        profilURL=findViewById(R.id.avatar);

    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }





}
