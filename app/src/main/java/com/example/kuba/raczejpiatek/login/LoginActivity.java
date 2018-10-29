package com.example.kuba.raczejpiatek.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.main.MainActivity;
import com.example.kuba.raczejpiatek.register.RegisterActivity;
import com.example.kuba.raczejpiatek.user.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView registerTextView;
    private Button logInButton;
    private Button fblogInButton;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;

    private static final String TAG = "FACELOG";
    private static Activity activity;


    // Initialize Facebook Login button


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        init();

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback(){

                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {

                                // Getting FB User Data
                                Bundle facebookData = getFacebookData(jsonObject);
                            }
                        });
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
/*
    // BEDZIE TRZEBA POPRAWIC
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        //if(currentUser != null ){
            updateUI(currentUser);
       // }

    }
*/
    private void updateUI(FirebaseUser currentUser) {
        Toast.makeText(LoginActivity.this,"Udalo sie zalogowac " ,Toast.LENGTH_LONG).show();
       // Intent intent = new Intent(LoginActivity.this, ProfilActivity.class);
       // startActivity(intent);
      //  finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }






    private void init() {
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        logInButton = findViewById(R.id.buttunLogIn);
        registerTextView = findViewById(R.id.textViewRegister);
    }

    private void LoginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Błąd logowania", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static void saveFacebookUserInfo(String first_name, String last_name, String email, String gender, String profileURL){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_first_name", first_name);
        editor.putString("fb_last_name", last_name);
        editor.putString("fb_email", email);
        editor.putString("fb_gender", gender);
        editor.putString("fb_profileURL", profileURL);
        editor.apply(); // This line is IMPORTANT !!!
        Log.d("MyApp", "Shared Name : "+first_name+"\nLast Name : "+last_name+"\nEmail : "+email+"\nGender : "+gender+"\nProfile Pic : "+profileURL);
    }

    /*
    User user = new User(email,gender,profileURL,first_name,last_name);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                //  Toast.makeText(LoginActivity.this,"Dodano do bazy",Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(LoginActivity.this,"fb nie dodano",Toast.LENGTH_LONG).show();
            }
        }
    });
    */

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));

                        //Test ....
                     Toast.makeText(LoginActivity.this,object.getString("first_name"),Toast.LENGTH_LONG).show();

                    LoginActivity.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"),
                    object.getString("gender"), profile_pic.toString());

        } catch (Exception e) {
            Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }



}
