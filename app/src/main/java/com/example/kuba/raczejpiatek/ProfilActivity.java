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

import com.example.kuba.raczejpiatek.register.RegisterActivity;
import com.example.kuba.raczejpiatek.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class ProfilActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView first_nameTextView;
    private TextView last_nameTextView;
    private ImageView profilURL;
    private User user;
    private Activity activity;


         @Override
        protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_profil);
        }

        // Constructor
        public ProfilActivity(Activity activity) {
            this.activity = activity;
        }

        public void saveAccessToken(String token) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("fb_access_token", token);
            editor.apply(); // This line is IMPORTANT !!!
        }


        public String getToken() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            return prefs.getString("fb_access_token", null);
        }

        public void clearToken() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply(); // This line is IMPORTANT !!!
        }

        public void saveFacebookUserInfo(String first_name, String last_name, String email, String gender, String profileURL) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("fb_first_name", first_name);
            editor.putString("fb_last_name", last_name);
            editor.putString("fb_email", email);
            editor.putString("fb_gender", gender);
            editor.putString("fb_profileURL", profileURL);
            editor.apply(); // This line is IMPORTANT !!!

            user = new User(email,gender,profileURL,first_name,last_name);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfilActivity.this," pobralo dane do bazy",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ProfilActivity.this," Nie pobralo",Toast.LENGTH_LONG).show();
                    }
                }
            });
            Log.d("MyApp", "Shared Name : " + first_name + "\nLast Name : " + last_name + "\nEmail : " + email + "\nGender : " + gender + "\nProfile Pic : " + profileURL);
        }

        public void getFacebookUserInfo() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            Log.d("MyApp", "Name : " + prefs.getString("fb_name", null) + "\nEmail : " + prefs.getString("fb_email", null));
        }
        private void getUser(){
            profilURL=(ImageView)findViewById(R.id.avatar);
            first_nameTextView=(TextView)findViewById(R.id.txtFirstName);
            last_nameTextView=(TextView)findViewById(R.id.txtLastName);
            emailTextView=(TextView)findViewById(R.id.txtEmail);
    }
        private void seeUser(){
            first_nameTextView.setText(user.getFirst_name());
            last_nameTextView.setText(user.getLast_name());
            emailTextView.setText(user.getEmail());
            try{
                URL profile_picture= new URL("https://graph.facebook.com/"+user.getProfilURl()+"/picture?width=250&height=250");
                Picasso.with(this).load(String.valueOf(profile_picture)).into(profilURL);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
}
