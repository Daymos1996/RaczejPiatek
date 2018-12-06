package com.example.kuba.raczejpiatek.friends;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.searchfriends.searchFriendsActivity;
import com.example.kuba.raczejpiatek.user.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

}
