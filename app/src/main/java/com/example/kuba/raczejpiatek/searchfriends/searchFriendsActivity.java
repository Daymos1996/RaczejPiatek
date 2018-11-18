package com.example.kuba.raczejpiatek.searchfriends;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class searchFriendsActivity extends AppCompatActivity {

    Button searchButton;
    EditText editTextSearch;
    RecyclerView result;
    DatabaseReference allUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        result = findViewById(R.id.result);
        result.setHasFixedSize(true);
        result.setLayoutManager( new LinearLayoutManager(this));

        searchButton = findViewById(R.id.buttonSearch);
        editTextSearch = findViewById(R.id.editTextSearch);

        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString();

                SearchFriends(searchText);
            }
        });
    }

    private void SearchFriends(String searchText) {
        Toast.makeText(this,"szukanie....",Toast.LENGTH_SHORT).show();

        Query searchFriends = allUserDatabaseRef.orderByChild("first_name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(
                        FindFriends.class,
                        R.layout.all_users_layout,
                        FindFriendsViewHolder.class,
                        searchFriends)
        {

            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, int position) {

                final String key = getRef(position).getKey();
                viewHolder.setFullname(model.getFirst_name());
                viewHolder.setProfileimage(getApplicationContext(), model.getProfilURl());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(searchFriendsActivity.this, ProfilActivity.class);
                        intent.putExtra("key", key);
                        startActivity(intent);
                    }
                });
            }
        };
        result.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context ctx, String profileimage){

            ImageView myImage = mView.findViewById(R.id.profileFriendPhoto);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(myImage);
        }
        public void setFullname(String fullname){
            TextView myName = mView.findViewById(R.id.txtFriendName);
            myName.setText(fullname);
        }
    }
}
