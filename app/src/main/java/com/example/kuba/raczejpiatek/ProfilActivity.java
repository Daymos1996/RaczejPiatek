package com.example.kuba.raczejpiatek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.chatList.ChatFriendsListActivity;
import com.example.kuba.raczejpiatek.login.LoginActivity;
import com.example.kuba.raczejpiatek.main.MainActivity;
import com.example.kuba.raczejpiatek.map.MapsActivity;
import com.example.kuba.raczejpiatek.register.Security;
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

import static com.example.kuba.raczejpiatek.StaticVariables.FRIEND_ID_LIST;
import static com.example.kuba.raczejpiatek.StaticVariables.INVITE_FRIEND_LIST;
import static com.example.kuba.raczejpiatek.StaticVariables.KEY_FRIEND_ID;
import static com.example.kuba.raczejpiatek.StaticVariables.KEY_USER_ID;
import static com.example.kuba.raczejpiatek.StaticVariables.CHAT_FRIEND_ID_LIST;


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
    private DatabaseReference mNotificationDatabase;
    private StorageReference mStorage;
    private TextView emailTextView;
    private TextView first_nameTextView;
    private TextView last_nameTextView;
    private TextView genderTextView;
    private ImageView profilURL;
    private TextView nickNameTextView;
    private TextView phoneNumberTextView;
    private Button inviteUserToFriends;
    private String userID;
    private ProgressDialog mProgresDiaolog;
    public static final int PICK_IMAGE = 1;
    private Uri mImageProfileUri;
    private String currentUserID;
    private ArrayList<String> friendsIdList;
    private ArrayList<String> InviteFriends;
    private ArrayList<String> chatsFriendsList;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setTitle("Profil");
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Raczej Piatek");


        init();
        friendsIdList = new ArrayList<>();
        InviteFriends = new ArrayList<>();
        chatsFriendsList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgresDiaolog = new ProgressDialog(this);


        if (getIntent().hasExtra(StaticVariables.KEY_USER_ID)) {
            userID = getIntent().getStringExtra(StaticVariables.KEY_USER_ID);

            if (user != null) {
                currentUserID = user.getUid();
            }
            inviteUserToFriends.setVisibility(View.VISIBLE);
            inviteUserToFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addOtherUserToFriendsButtonOnClick(currentUserID, userID);
                    inviteUserToFriends.setText("Invitation Sent");
                }
            });

        } else if (getIntent().hasExtra(KEY_FRIEND_ID)) {
            userID = getIntent().getStringExtra(KEY_FRIEND_ID);
            friendsIdList = (ArrayList<String>)getIntent().getSerializableExtra(FRIEND_ID_LIST);
            InviteFriends=(ArrayList<String>) getIntent().getSerializableExtra(INVITE_FRIEND_LIST);
            chatsFriendsList=(ArrayList<String>) getIntent().getSerializableExtra(CHAT_FRIEND_ID_LIST);
        } else {
            if (user != null) {
                userID = user.getUid();
            }
            friendsIdFromDatabase();
            chatFriendsIdFromDatabase();
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


        myRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        profilURL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openFileChooser(userID);
                return false;
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_dashboard:
                        Intent p = new Intent(ProfilActivity.this, MapsActivity.class);
                        p.putExtra(KEY_FRIEND_ID, userID);
                        p.putExtra(FRIEND_ID_LIST, friendsIdList);
                        p.putExtra(INVITE_FRIEND_LIST, InviteFriends);
                        p.putExtra(CHAT_FRIEND_ID_LIST, chatsFriendsList);
                        startActivity(p);
                        return true;
                    case R.id.navigation_notifications:
                        Intent ch = new Intent(ProfilActivity.this, ChatFriendsListActivity.class);
                        ch.putExtra(KEY_FRIEND_ID, userID);
                        ch.putExtra(KEY_USER_ID, userID);
                        ch.putExtra(FRIEND_ID_LIST, friendsIdList);
                        ch.putExtra(INVITE_FRIEND_LIST, InviteFriends);
                        ch.putExtra(CHAT_FRIEND_ID_LIST, chatsFriendsList);
                        startActivity(ch);
                        return true;

                    case R.id.navigation_friends:
                        Intent intent = new Intent(ProfilActivity.this, MainActivity.class);
                        intent.putExtra(KEY_FRIEND_ID, userID);
                        intent.putExtra(KEY_USER_ID, userID);
                        intent.putExtra(FRIEND_ID_LIST, friendsIdList);
                        intent.putExtra(INVITE_FRIEND_LIST, InviteFriends);
                        intent.putExtra(CHAT_FRIEND_ID_LIST, chatsFriendsList);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    private void showData(DataSnapshot ds) {
        User uInfo = new User();
        if (ds.child("first_name").exists()) {
            uInfo.setFirst_name(ds.child("first_name").getValue().toString());
        }
        if (ds.child("last_name").exists()) {
            uInfo.setLast_name(ds.child("last_name").getValue().toString()); //set the name
        }
        if (ds.child("email").exists()) {
            uInfo.setEmail(ds.child("email").getValue().toString()); //set the email
        }

        if (ds.child("profilURl").exists()) {
            uInfo.setProfilURl(ds.child("profilURl").getValue().toString());
        }
        if (ds.child("username").exists()) {
            uInfo.setUsername(ds.child("username").getValue().toString());
        }
        if (ds.child("phone").exists()) {
            uInfo.setPhone(ds.child("phone").getValue().toString());
        }

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
                    try {
                        dR.child("password").setValue(Security.encrypt(name));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            if (ds.getValue().equals("accept")) {
                friendsIdList.add(ds.getKey());
            }
        }
    }

    private void setChatFriendsList(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                chatsFriendsList.add(ds.getKey());
        }
    }
    private void chatFriendsIdFromDatabase() {
        DatabaseReference allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/chat");
        allUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setChatFriendsList(dataSnapshot);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }
    private void friendsIdFromDatabase() {
        DatabaseReference allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/friends");
        allUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setFriendsListFromFriendsTable(dataSnapshot);
                setFriendRequstFromFriendTable(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    private void setFriendRequstFromFriendTable(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue().equals("received")) {
                InviteFriends.add(ds.getKey());
            }
        }
    }

    private void init() {
        emailTextView = findViewById(R.id.txtEmail);
        first_nameTextView = findViewById(R.id.txtFirstName);
        last_nameTextView = findViewById(R.id.txtLastName);
        nickNameTextView = findViewById(R.id.txtUsername);
        phoneNumberTextView = findViewById(R.id.txtPhoneNumber);
        genderTextView = findViewById(R.id.txtGender);
        profilURL = findViewById(R.id.avatar);
        inviteUserToFriends = findViewById(R.id.invite_user_to_friends_btn);


    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.profil_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.changePassword) {
            UpdatePassword(userID);
        }
        if (item.getItemId() == R.id.deleteUser) {
            deleteUser(userID);
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
        }
        if (item.getItemId() == R.id.main_logout) {
            //  mAuth.getInstance().signOut();
            sendToStart();
        }


        return true;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(ProfilActivity.this, LoginActivity.class);
        startActivity(startIntent);
    }
}
