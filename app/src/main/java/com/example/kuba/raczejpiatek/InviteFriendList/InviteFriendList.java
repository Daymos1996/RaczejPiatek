package com.example.kuba.raczejpiatek.InviteFriendList;

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
import com.example.kuba.raczejpiatek.searchfriends.searchFriendsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InviteFriendList extends AppCompatActivity {

    RecyclerView result;
    private DatabaseReference FriendsReference;
    private FirebaseAuth mAuth;
    private Button acceptButton;
    String sender_user_id;
    String receiver_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend_list);

        result = findViewById(R.id.result);
        result.setHasFixedSize(true);
        result.setLayoutManager( new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(sender_user_id).child("friends");
        receiver_user_id = FriendsReference.toString();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptFriendRequest();
            }
        });

    }

    private void AcceptFriendRequest(){
        Toast.makeText(this,receiver_user_id,Toast.LENGTH_LONG).show();


    }

    private void init() {
        acceptButton = (Button) findViewById(R.id.accept);
    }



    public static class FindInviteFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindInviteFriendsViewHolder(@NonNull View itemView) {
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
