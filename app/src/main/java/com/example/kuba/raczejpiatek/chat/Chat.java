package com.example.kuba.raczejpiatek.chat;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
public class Chat extends AppCompatActivity {
    private  static final String CHAT_TABLE = "chat";
    private DatabaseReference myRef;
    private DatabaseReference otherUserReference;
    private DatabaseReference friendsOtherUserReference;
    private DatabaseReference myMessage;
    private String userID;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_chat;
    FloatingActionButton fab;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout_menu_button)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_chat,"You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profil,menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(requestCode == RESULT_OK)
            {
                Snackbar.make(activity_chat,"Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage(userID,currentUserID);
            }
            else
            {
                Snackbar.make(activity_chat,"We couldnt sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity_chat = findViewById(R.id.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        currentUserID = getIntent().getStringExtra("key");
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).child(CHAT_TABLE).push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                input.setText("");
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(activity_chat,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            displayChatMessage(userID,currentUserID);
        }
    }
    private void displayChatMessage(String userID, String currentUserID) {
        FirebaseDatabase firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Users");
        otherUserReference = myRef.child(currentUserID);
        friendsOtherUserReference = otherUserReference.child(CHAT_TABLE);
        Query query = friendsOtherUserReference;
        Toast.makeText(this, query.toString(), Toast.LENGTH_SHORT).show();
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>().setQuery(query, ChatMessage.class)
                .setLayout(R.layout.list_item).build();
        ListView listofMessage = findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v,ChatMessage model, int position) {
                TextView messageText,messageUser,messageTime;
                messageText = findViewById(R.id.message_text);
                messageUser = findViewById(R.id.message_user);
                messageTime = findViewById(R.id.message_time);
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-mm-yyyy (HH:mm:ss)",model.getMessageTime()));
            }
        };
        listofMessage.setAdapter(adapter);
    }
}