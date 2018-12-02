package com.example.kuba.raczejpiatek.friends;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.user.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ItemViewHolder> {
    private List<FindFriends> friendsList;
    private Context mContext;
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view =  mInflater.inflate(R.layout.all_users_layout,viewGroup,false);
        return new ItemViewHolder(view);
    }
    public FriendsRecyclerViewAdapter(Context mContext,List<FindFriends> mUserLsit) {
        this.mContext=mContext;
        this.friendsList = mUserLsit;
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.myName.setText(friendsList.get(i).getFirst_name());
       // itemViewHolder.setProfileimage(FriendsActivity.class, friend.getProfilURl());
        Picasso.with(mContext).load(friendsList.get(i).getProfilURl()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(itemViewHolder.myImage);

    }

    @Override
    public int getItemCount() {
//        return friendsList.size();
        return 0;
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView myName;
        ImageView myImage;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            myName = itemView.findViewById(R.id.txtFriendName);
            myImage = itemView.findViewById(R.id.profileFriendPhoto);
        }

    }
}
