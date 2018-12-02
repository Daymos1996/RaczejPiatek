package com.example.kuba.raczejpiatek.friends;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    private DatabaseReference allUserDatabaseRef;
    private DatabaseReference userDatabaseRef;
    private String userID;
    private ArrayList<String> friendsIdList;
    private static ArrayList<FindFriends> friendsList;
    private FriendsRecyclerViewAdapter friendsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        init();
        friendsIdList = new ArrayList<>();
        friendsList = new ArrayList<>();

        friendsListRecyclerView.setHasFixedSize(true);
        friendsListRecyclerView.setLayoutManager( new LinearLayoutManager(this));


        //   userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userID = getIntent().getStringExtra("USER_ID");
        //   getIfFriends();
        // Toast.makeText(FriendsActivity.this, friendsIdList.get(0), Toast.LENGTH_SHORT).show();


        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/friends");
        allUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setFriendsListFromFriendsTable(dataSnapshot);
               // showAllFriends();
                aaa();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Toast.makeText(FriendsActivity.this, friendsList.get(0).getProfilURl() + friendsList.get(0).getFirst_name(), Toast.LENGTH_SHORT).show();

    }

    private void init() {
        friendsListRecyclerView = (RecyclerView) findViewById(R.id.friendsListRecyclerView);
    }

    private void setFriendsListFromFriendsTable(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            friendsIdList.add(ds.getKey());
        }
    }

    /*
        private void getIfFriends() {
            allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/friends");
            allUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    setFriendsListFromFriendsTable(dataSnapshot);
                    //
                    // aaa();
                    showAllFriends();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
        */

    private void aaa() {
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (int i = 0; i < friendsIdList.size(); i++) {
                        if (ds.getKey().equals(friendsIdList.get(i))) {
                            FindFriends friend = new FindFriends();
                            String imie = ds.child("first_name").getValue().toString();
//
                            friend.setFirst_name(imie);
                            friend.setProfilURl("https://graph.facebook.com/1874028232680936/picture?type=large");
                            //  if (friend.getFirst_name() != null && friend.getProfilURl() != null) {
                            friendsList.add(friend);
                            // }
                            Toast.makeText(FriendsActivity.this, friendsList.get(i).getProfilURl() + friendsList.get(i).getFirst_name(), Toast.LENGTH_SHORT).show();

                        //    friendsList.add(new FindFriends("https://graph.facebook.com/1874028232680936/picture?type=large", "fdjf"));
                            //     friendsListRecyclerView.setHasFixedSize(true);
                            //     friendsListRecyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));


                        }

                    }

                }
                friendsRecyclerViewAdapter = new FriendsRecyclerViewAdapter(FriendsActivity.this, friendsList);
                friendsListRecyclerView.setAdapter(friendsRecyclerViewAdapter);
                friendsRecyclerViewAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void bb() {

    }

    private void showAllFriends() {
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/friends");
        FirebaseRecyclerAdapter<FindFriends, FriendsActivity.FindFriendsViewHolder> firebaseRecyclerAdapter;


        Query searchFriends = userDatabaseRef.orderByKey().equalTo("FgIR0jBH4Mdbs48HuOPNp9pteH83");
      //  firebaseRecyclerAdapter.onBindViewHolder(vie);
        firebaseRecyclerAdapter = new Firebase
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FriendsActivity.FindFriendsViewHolder>(
                FindFriends.class,
                R.layout.all_users_layout,
                FriendsActivity.FindFriendsViewHolder.class,
                searchFriends) {

            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, int position) {
                final String key = getRef(position).getKey();

                viewHolder.setFullname(model.getFirst_name());
                viewHolder.setProfileimage(getApplicationContext(), model.getProfilURl());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FriendsActivity.this, ProfilActivity.class);
                        intent.putExtra("key", key);
                        startActivity(intent);
                    }
                });
            }
        };
        friendsListRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context ctx, String profileimage) {

            ImageView myImage = mView.findViewById(R.id.profileFriendPhoto);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myName = mView.findViewById(R.id.txtFriendName);
            myName.setText(fullname);
        }
    }
}
