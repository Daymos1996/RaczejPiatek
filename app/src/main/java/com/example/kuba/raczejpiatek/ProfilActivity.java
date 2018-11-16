package com.example.kuba.raczejpiatek;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.login.LoginActivity;
import com.example.kuba.raczejpiatek.main.MainActivity;
import com.example.kuba.raczejpiatek.map.MapsActivity;
import com.example.kuba.raczejpiatek.register.RegisterActivity;
import com.example.kuba.raczejpiatek.searchfriends.searchFriendsActivity;
import com.example.kuba.raczejpiatek.user.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private TextView emailTextView;
    private TextView first_nameTextView;
    private TextView last_nameTextView;
    private TextView genderTextView;
    private ImageView profilURL;
    private TextView nickNameTextView;
    private TextView phoneNumberTextView;
    private Button goToMapBtn;
    private Button deleteUser;
    private Button goToFindFriendsBtn;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setTitle("Profil");
        init();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference();
        final FirebaseUser user=mAuth.getCurrentUser();
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

            goToMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfilActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            });

             goToFindFriendsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfilActivity.this, searchFriendsActivity.class);
                    startActivity(intent);
                }
            });

             deleteUser.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     deleteUser(userID);
                     Intent intent = new Intent(ProfilActivity.this,LoginActivity.class);
                     startActivity(intent);
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


    public void updateProfile() {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        // [END update_profile]
    }

    public void updateEmail() {
        // [START update_email]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail("user@example.com")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
        // [END update_email]
    }


    public void updatePassword() {
        // [START update_password]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = "SOME-SECURE-PASSWORD";

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
        // [END update_password]
    }

    public void deleteUser(String userID) {

       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        userData.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");
                }
            }
        });


       user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });

    }

    public void reauthenticate() {
        // [START reauthenticate]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential("jakub@jakub.pl", "jakub1");

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                    }
                });
        // [END reauthenticate]
    }

    private boolean updateArtist(String id, String name) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userID);

        //updating artist
        User user = new User();
        user.setFirst_name(name);
        Toast.makeText(getApplicationContext(), "Artist Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private void showUpdateDeleteDialog(final String userID, String First_name) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);

        dialogBuilder.setTitle(First_name);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    updateArtist(userID, name);
                    b.dismiss();
                }
            }
        });
    }




    private void init() {
        emailTextView =  findViewById(R.id.txtEmail);
        first_nameTextView =  findViewById(R.id.txtFirstName);
        last_nameTextView = findViewById(R.id.txtLastName);
        nickNameTextView = findViewById(R.id.txtUsername);
        phoneNumberTextView =  findViewById(R.id.txtPhoneNumber);
        genderTextView=  findViewById(R.id.txtGender);
        profilURL=findViewById(R.id.avatar);
        goToMapBtn = (Button) findViewById(R.id.go_to_map_btn);
        goToFindFriendsBtn = findViewById(R.id.go_to_find_friends_btn);
        deleteUser =(Button) findViewById(R.id.deleteUser);
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }






}
