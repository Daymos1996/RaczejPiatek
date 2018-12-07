package com.example.kuba.raczejpiatek.friends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView friendsListRecyclerView;
    private DatabaseReference userDatabaseRef;
    private String userID;
    public static ArrayList<String> friendsIdList;
    private static ArrayList<FindFriends> friendsList;
    private FriendsRecyclerViewAdapter friendsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        init();
        friendsList = new ArrayList<>();

        userID = getIntent().getStringExtra("USER_ID");
        friendsIdList = (ArrayList<String>) getIntent().getSerializableExtra("FRIEND_ID_LIST");

        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsRecyclerViewAdapter = new FriendsRecyclerViewAdapter(FriendsActivity.this, userDatabaseRef,friendsIdList);
        friendsListRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        friendsListRecyclerView.setHasFixedSize(true);
        friendsListRecyclerView.setAdapter(friendsRecyclerViewAdapter);
        friendsRecyclerViewAdapter.notifyDataSetChanged();

    }



    private void init() {
        friendsListRecyclerView = (RecyclerView) findViewById(R.id.friendsListRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        friendsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        friendsRecyclerViewAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        friendsRecyclerViewAdapter.notifyDataSetChanged();
    }


}
