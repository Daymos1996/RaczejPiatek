package com.example.kuba.raczejpiatek.chatList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.StaticVariables;
import com.example.kuba.raczejpiatek.chat.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatFriendsRecyclerViewAdapter extends RecyclerView.Adapter<ChatFriendsRecyclerViewAdapter.ItemViewHolder> {
    private List<FindFriends> chatFriendlist = new ArrayList<>();
    private Context mContext;
    private DatabaseReference mRef;


    public ChatFriendsRecyclerViewAdapter(Context mContext, DatabaseReference ref, final ArrayList<String> chatFriendsIdList) {
        this.mContext = mContext;
        this.mRef = ref;
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatFriendlist.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (int i = 0; i < chatFriendsIdList.size(); i++) {
                        if (postSnapshot.getKey().equals(chatFriendsIdList.get(i))) {
                            FindFriends friend = new FindFriends();
                            String imie = postSnapshot.child("first_name").getValue().toString();
                            String zdjecie = postSnapshot.child("profilURl").getValue().toString();
                            String id = postSnapshot.getKey();
                            friend.setFirst_name(imie);
                            friend.setProfilURl(zdjecie);
                            friend.setId(id);
                            chatFriendlist.add(friend);
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
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {
        itemViewHolder.myName.setText(chatFriendlist.get(i).getFirst_name());
        Picasso.with(mContext).load(chatFriendlist.get(i).getProfilURl()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(itemViewHolder.myImage);
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = chatFriendlist.get(i).getId();
                Intent intent = new Intent(mContext, Chat.class);
                intent.putExtra(StaticVariables.KEY_CHAT, id);
                mContext.startActivity(intent);


            }
        });
        itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String id = chatFriendlist.get(i).getId();
                Intent intent = new Intent(mContext, ProfilActivity.class);
                intent.putExtra(StaticVariables.KEY_FRIEND_ID, id);
                mContext.startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatFriendlist.size();
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
