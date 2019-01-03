package com.example.kuba.raczejpiatek.InviteFriendList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.StaticVariables;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class InviteFriendList extends AppCompatActivity {

    private RecyclerView friendsListRecyclerView;
    private DatabaseReference userDatabaseRef;
    private String userID;
    public static ArrayList<String> InvitefriendsIdList;
    private static ArrayList<FindFriends> InvitefriendsList;
    private InviteFriendListRecyclerViewArapter inviteFriendListRecyclerViewArapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friendsListRecyclerView = (RecyclerView) findViewById(R.id.friendsListRecyclerView);

        InvitefriendsList = new ArrayList<>();
        userID = getIntent().getStringExtra(StaticVariables.KEY_FRIEND_ID);
        InvitefriendsIdList = (ArrayList<String>) getIntent().getSerializableExtra(StaticVariables.INVITE_FRIEND_LIST);
        if(!InvitefriendsIdList.isEmpty()){


        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        inviteFriendListRecyclerViewArapter = new InviteFriendListRecyclerViewArapter(InviteFriendList.this, userDatabaseRef,InvitefriendsIdList,userID);
        friendsListRecyclerView.setLayoutManager( new LinearLayoutManager(InviteFriendList.this));
        friendsListRecyclerView.setHasFixedSize(true);
        friendsListRecyclerView.setAdapter(inviteFriendListRecyclerViewArapter);
        inviteFriendListRecyclerViewArapter.notifyDataSetChanged();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
//        inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
 //       inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
 //       inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }

}