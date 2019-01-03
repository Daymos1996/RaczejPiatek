package com.example.kuba.raczejpiatek.InviteFriendList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.main.RequestsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.kuba.raczejpiatek.StaticVariables.KEY_FRIEND_ID;
import static com.example.kuba.raczejpiatek.StaticVariables.KEY_USER_ID;

public class InviteFriendListRecyclerViewArapter extends RecyclerView.Adapter<InviteFriendListRecyclerViewArapter.ItemViewHolder> {

    private List<FindFriends> InvitefriendsList = new ArrayList<>();
    private Context mContext;
    private DatabaseReference mRef;
    private ImageButton acceptButton;
    private ImageButton decelineButton;
    private String userID;

    public InviteFriendListRecyclerViewArapter(Context mContext, DatabaseReference ref, final ArrayList<String> InvitefriendsIdList, final String userID) {
        this.mContext = mContext;
        this.mRef = ref;
        this.userID=userID;
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InvitefriendsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (int i = 0; i < InvitefriendsIdList.size(); i++) {
                        if (postSnapshot.getKey().equals(InvitefriendsIdList.get(i))) {
                            FindFriends friend = new FindFriends();
                            String imie = postSnapshot.child("first_name").getValue().toString();
                            String zdjecie = postSnapshot.child("profilURl").getValue().toString();
                            String id = postSnapshot.getKey();
                            friend.setFirst_name(imie);
                            friend.setProfilURl(zdjecie);
                            friend.setId(id);
                            InvitefriendsList.add(friend);
                        }
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.all_users_layout, viewGroup, false);
        acceptButton = (ImageButton) view.findViewById(R.id.accept);
        decelineButton = (ImageButton) view.findViewById(R.id.decline);
        acceptButton.setVisibility(View.VISIBLE);
        decelineButton.setVisibility(View.VISIBLE);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteFriendListRecyclerViewArapter.ItemViewHolder itemViewHolder, final int i) {
        itemViewHolder.myName.setText(InvitefriendsList.get(i).getFirst_name());
        Picasso.with(mContext).load(InvitefriendsList.get(i).getProfilURl()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(itemViewHolder.myImage);

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = InvitefriendsList.get(i).getId();
                Intent intent = new Intent(mContext, ProfilActivity.class);
                intent.putExtra(KEY_FRIEND_ID, key);
                mContext.startActivity(intent);

            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child(userID).child("friends").child(InvitefriendsList.get(i).getId()).setValue("accept");
                mRef.child(InvitefriendsList.get(i).getId()).child("friends").child(userID).setValue("accept");
                InvitefriendsList.remove(i);
            }
        });
        decelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child(userID).child("friends").child(InvitefriendsList.get(i).getId()).removeValue();
                mRef.child(InvitefriendsList.get(i).getId()).child("friends").child(userID).removeValue();
                InvitefriendsList.remove(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return InvitefriendsList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView myName;
        ImageView myImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            myName = itemView.findViewById(R.id.txtFriendName);
            myImage = itemView.findViewById(R.id.profileFriendPhoto);
        }

    }
}
