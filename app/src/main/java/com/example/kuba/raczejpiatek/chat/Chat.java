
package com.example.kuba.raczejpiatek.chat;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.kuba.raczejpiatek.friends.FriendsActivity;
import com.example.kuba.raczejpiatek.friends.FriendsRecyclerViewAdapter;
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
    private String otherUserID;
    private FirebaseAuth mAuth;
    private static int SIGN_IN_REQUEST_CODE = 1;
    RelativeLayout activity_chat;
    FloatingActionButton fab;
    private RecyclerView messagesListRecyclerView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;
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
                displayChatMessage(userID,otherUserID);
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
        messagesListRecyclerView = findViewById(R.id.list_of_message);
        userID = user.getUid();
        otherUserID = getIntent().getStringExtra("key");
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("Users").child(userID).child(CHAT_TABLE).child(otherUserID).push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                FirebaseDatabase.getInstance().getReference("Users").child(otherUserID).child(CHAT_TABLE).child(userID).push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));

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
            displayChatMessage(userID,otherUserID);
        }
    }
    private void displayChatMessage(String userID, String currentUserID) {
        FirebaseDatabase firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Users");
        otherUserReference = myRef.child(userID);
        friendsOtherUserReference = otherUserReference.child(CHAT_TABLE).child(otherUserID);



        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(Chat.this,friendsOtherUserReference);
        messagesListRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        messagesListRecyclerView.setHasFixedSize(true);
        messagesListRecyclerView.setAdapter(chatRecyclerViewAdapter);
        chatRecyclerViewAdapter.notifyDataSetChanged();

    }
}