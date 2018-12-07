package com.example.kuba.raczejpiatek.InviteFriendList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.R;
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
        userID = getIntent().getStringExtra("USER_ID");
        InvitefriendsIdList = (ArrayList<String>) getIntent().getSerializableExtra("Invite_FRIEND_LIST");
        if(!InvitefriendsIdList.isEmpty()){


        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        inviteFriendListRecyclerViewArapter = new InviteFriendListRecyclerViewArapter(InviteFriendList.this, userDatabaseRef,InvitefriendsIdList);
        friendsListRecyclerView.setLayoutManager( new LinearLayoutManager(InviteFriendList.this));
        friendsListRecyclerView.setHasFixedSize(true);
        friendsListRecyclerView.setAdapter(inviteFriendListRecyclerViewArapter);
        inviteFriendListRecyclerViewArapter.notifyDataSetChanged();

        }



        /*
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptFriendRequest();
            }
        });
        */

    }

    @Override
    protected void onStart() {
        super.onStart();
//        inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }


    /*
    private void AcceptFriendRequest(){
        Query query = FriendsReference.orderByChild("Users").startAt(receiver_user_id).endAt(receiver_user_id + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options =
                new FirebaseRecyclerOptions.Builder<FindFriends>()
                        .setQuery(query, new SnapshotParser<FindFriends>() {
                            @NonNull
                            @Override
                            public FindFriends parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new FindFriends(snapshot.child("profilURl").getValue().toString(), snapshot.child("first_name").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<FindFriends, ViewHolder>(options) {
            @Override
            public InviteFriendList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_layout, parent, false);

                return new InviteFriendList.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(InviteFriendList.ViewHolder holder, final int position, FindFriends model) {
                final String key = getRef(position).getKey();

                holder.setFullname(model.getFirst_name());
                holder.setProfileimage(getApplicationContext(), model.getProfilURl());
            }

        };
        result.setAdapter(adapter);
        Toast.makeText(this,receiver_user_id,Toast.LENGTH_LONG).show();


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView myImage;
        TextView myName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            myImage = mView.findViewById(R.id.profileFriendPhoto);
            myName = mView.findViewById(R.id.txtFriendName);

        }

        public void setProfileimage(Context ctx, String profileimage) {
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(myImage);
        }

        public void setFullname(String fullname) {
            myName.setText(fullname);
        }
    }
    */
}