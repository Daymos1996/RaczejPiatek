package com.example.kuba.raczejpiatek.searchfriends;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.friends.FriendsRecyclerViewAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class searchFriendsActivity extends AppCompatActivity {

    Button searchButton;
    EditText editTextSearch;
    RecyclerView result;
    DatabaseReference allUserDatabaseRef;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        result = findViewById(R.id.result);
        result.setHasFixedSize(true);
        result.setLayoutManager(new LinearLayoutManager(this));

        searchButton = findViewById(R.id.buttonSearch);
        editTextSearch = findViewById(R.id.editTextSearch);

        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        String searchText = editTextSearch.getText().toString();
        searchFriends(searchText);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void searchFriends(String searchText) {
        Query query = allUserDatabaseRef.orderByChild("first_name").startAt(searchText).endAt(searchText + "\uf8ff");

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
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_layout, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, FindFriends model) {
                final String key = getRef(position).getKey();

                holder.setFullname(model.getFirst_name());
                holder.setProfileimage(getApplicationContext(), model.getProfilURl());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(searchFriendsActivity.this, ProfilActivity.class);
                        intent.putExtra("key", key);
                        startActivity(intent);
                    }
                });
            }

        };

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
}