package com.example.kuba.raczejpiatek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.chat.Chat;
import com.example.kuba.raczejpiatek.friends.FriendsActivity;
import com.example.kuba.raczejpiatek.login.LoginActivity;
import com.example.kuba.raczejpiatek.map.MapsActivity;
import com.example.kuba.raczejpiatek.searchfriends.searchFriendsActivity;
import com.example.kuba.raczejpiatek.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "ViewDatabase";
    private static final String FRIENDS_TABLE = "friends";
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference otherUserReference;
    private DatabaseReference friendsOtherUserReference;
    private DatabaseReference userReference;
    private DatabaseReference friendsUserReference;
    private StorageReference mStorage;
    private TextView emailTextView;
    private TextView first_nameTextView;
    private TextView last_nameTextView;
    private TextView genderTextView;
    private ImageView profilURL;
    private TextView nickNameTextView;
    private TextView phoneNumberTextView;
    private Button goToMapBtn;
    private Button password;
    private Button deleteUser;
    private Button goToFindFriendsBtn;
    private Button inviteUserToFriends;
    private Button goToFriends;
    private String userID;
    private ProgressDialog mProgresDiaolog;
    public static final int PICK_IMAGE = 1;
    private Button chatButton;
    private Uri mImageProfileUri;
    private String currentUserID;
    private ArrayList<String> friendsIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setTitle("Profil");
        init();
        friendsIdList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (!(user.getUid().isEmpty())) {
            userID = user.getUid();
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgresDiaolog = new ProgressDialog(this);


        if (getIntent().hasExtra("key")) {
            userID = getIntent().getStringExtra("key");

            currentUserID = user.getUid();
            goToMapBtn.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            deleteUser.setVisibility(View.GONE);
            goToFindFriendsBtn.setVisibility(View.GONE);
            goToFriends.setVisibility(View.GONE);
            inviteUserToFriends.setVisibility(View.VISIBLE);

            inviteUserToFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addOtherUserToFriendsButtonOnClick(currentUserID, userID);
                    inviteUserToFriends.setText("Zaproszenie wysłane");
                }
            });


        } else {
            userID = user.getUid();
            friendsIdFromDatabase();
        }


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
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

        goToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, FriendsActivity.class);
                intent.putExtra("USER_ID", userID);
                intent.putExtra("FRIEND_ID_LIST", friendsIdList);
                startActivity(intent);
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(userID);
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        first_nameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UpdateFirstName(userID);
                return false;
            }
        });

        emailTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UpdateEmail(userID);
                return false;
            }
        });

        nickNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UpdateUsername(userID);
                return false;
            }
        });

        last_nameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UpdateLastName(userID);
                return false;
            }
        });

        genderTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UpdateGender(userID);
                return false;
            }
        });

        phoneNumberTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UpdatePhone(userID);
                return false;
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword(userID);
            }
        });
        profilURL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openFileChooser(userID);
                return false;
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this,Chat.class);
                startActivity(intent);
            }
        });



    }

    private void showData(DataSnapshot dataSnapshot) {


        for (DataSnapshot ds : dataSnapshot.getChildren()) {

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
            Log.d(TAG, "showData: profile: " + uInfo.getProfilURl());


            emailTextView.setText(uInfo.getEmail());
            if (uInfo.getGender() == null) {
                genderTextView.setText("Nie podano płci");
            } else {
                genderTextView.setText(uInfo.getGender());
            }
            if (uInfo.getFirst_name() == null) {
                first_nameTextView.setText("Nie podano imienia");
            } else {
                first_nameTextView.setText(uInfo.getFirst_name());
            }
            if (uInfo.getLast_name() == null) {
                last_nameTextView.setText("Nie podano nazwiska");
            } else {
                last_nameTextView.setText(uInfo.getLast_name());
            }
            if (uInfo.getUsername() == null) {
                nickNameTextView.setText("Nie podano nicku");
            } else {
                nickNameTextView.setText(uInfo.getUsername());
            }
            if (uInfo.getPhone() == null) {
                phoneNumberTextView.setText("Nie podano numeru");
            } else {
                phoneNumberTextView.setText(uInfo.getPhone());
            }
            if (uInfo.getProfilURl() != null) {
                Picasso.with(this).load(uInfo.getProfilURl()).into(profilURL);

            }

        }
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


    private boolean UpdateFirstName(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    dR.child("first_name").setValue(name);
                    toastMessage("First Name update");
                    b.dismiss();
                }
            }
        });

        return true;
    }


    private boolean UpdateEmail(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(name);
                    dR.child("email").setValue(name);
                    toastMessage("email update");
                    b.dismiss();
                }
            }
        });
        return true;
    }


    private boolean UpdateUsername(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    dR.child("username").setValue(name);
                    toastMessage("Username update");
                    b.dismiss();
                }
            }
        });

        return true;
    }

    private boolean UpdatePassword(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(name);
                    dR.child("password").setValue(name);
                    toastMessage("password update");
                    b.dismiss();
                }
            }
        });
        return true;
    }

    private boolean UpdateLastName(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    dR.child("last_name").setValue(name);
                    toastMessage("Last name update");
                    b.dismiss();
                }
            }
        });

        return true;
    }

    private boolean UpdateGender(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    dR.child("gender").setValue(name);
                    toastMessage("Gender update");
                    b.dismiss();
                }
            }
        });

        return true;
    }

    private boolean UpdatePhone(final String userID) {
        //getting the specified artist reference

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    dR.child("phone").setValue(name);
                    toastMessage("Phone number update");
                    b.dismiss();
                }
            }
        });

        return true;
    }

    private void openFileChooser(final String userID) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri mImageProfileUri = data.getData();
            /*
            CropImage.activity(imagePath)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setAspectRatio(1, 1)
                    .start(ProfilActivity.this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                mImageProfileUri = result.getUri();

                Picasso.with(this).load(mImageProfileUri).into(profilURL);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
            */

            // DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
            // dR.child("profilURl").setValue(mImageProfileUri);
            // toastMessage("Username update");

            mProgresDiaolog.setMessage("Uploading...");
            mProgresDiaolog.show();

            final StorageReference filepath = mStorage.child("profile_img").child(userID).child("profile_picture.jpg");
            filepath.putFile(mImageProfileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgresDiaolog.dismiss();
                    toastMessage("Zaladowano zdjecie ");
                }
            });


            //dodanie do bazy danych
            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUri) {
                    String uploadId = downloadUri.toString();
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    dR.child("profilURl").setValue(uploadId);
                }
            });
        }
    }


    private void addOtherUserToFriendsButtonOnClick(String userID, String otherUserID) {
        FirebaseDatabase firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Users");

        otherUserReference = myRef.child(otherUserID);
        friendsOtherUserReference = otherUserReference.child(FRIENDS_TABLE);
        friendsOtherUserReference.child(userID).setValue("received");

        userReference = myRef.child(userID);
        friendsUserReference = userReference.child(FRIENDS_TABLE);
        friendsUserReference.child(otherUserID).setValue("sent");

        Toast.makeText(ProfilActivity.this, "Wysłano zaprszenie do znajomych", Toast.LENGTH_LONG).show();
    }

    private void setFriendsListFromFriendsTable(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            friendsIdList.add(ds.getKey());
        }
    }

    private void friendsIdFromDatabase() {
        DatabaseReference allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/friends");
        allUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setFriendsListFromFriendsTable(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    private void init() {
        emailTextView = findViewById(R.id.txtEmail);
        first_nameTextView = findViewById(R.id.txtFirstName);
        last_nameTextView = findViewById(R.id.txtLastName);
        nickNameTextView = findViewById(R.id.txtUsername);
        phoneNumberTextView = findViewById(R.id.txtPhoneNumber);
        genderTextView = findViewById(R.id.txtGender);
        profilURL = findViewById(R.id.avatar);
        goToMapBtn = (Button) findViewById(R.id.go_to_map_btn);
        goToFindFriendsBtn = findViewById(R.id.go_to_find_friends_btn);
        deleteUser = (Button) findViewById(R.id.deleteUser);
        password = (Button) findViewById(R.id.changePassword);
        goToFriends = (Button) findViewById(R.id.go_to_friends_btn);
        inviteUserToFriends = (Button) findViewById(R.id.invite_user_to_friends_btn);
        chatButton = findViewById(R.id.chat_button);

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
