package com.example.kuba.raczejpiatek.chatList;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.StaticVariables;
import com.example.kuba.raczejpiatek.friends.FriendsRecyclerViewAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListMessageFromFriendsFragment extends Fragment {

    private RecyclerView chatFriendsListRecyclerView;
    private DatabaseReference userDatabaseRef;
    private String userID;
    public static ArrayList<String> chatFriendsIdList;
    private static ArrayList<FindFriends> friendsList;
    private ChatFriendsRecyclerViewAdapter chatFriendsRecyclerViewAdapter;
    private View mMainView;


    public ListMessageFromFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        chatFriendsListRecyclerView = (RecyclerView) mMainView.findViewById(R.id.friendsListRecyclerView);


        userID = getActivity().getIntent().getStringExtra(StaticVariables.KEY_FRIEND_ID);
        chatFriendsIdList = (ArrayList<String>) getActivity().getIntent().getSerializableExtra(StaticVariables.CHAT_FRIEND_ID_LIST);
        if(!chatFriendsIdList.isEmpty()) {
            userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

            chatFriendsRecyclerViewAdapter = new ChatFriendsRecyclerViewAdapter(getActivity().getBaseContext(), userDatabaseRef, chatFriendsIdList);
            chatFriendsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            chatFriendsListRecyclerView.setHasFixedSize(true);
            chatFriendsListRecyclerView.setAdapter(chatFriendsRecyclerViewAdapter);
            chatFriendsRecyclerViewAdapter.notifyDataSetChanged();

        }

        return  mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //  friendsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        //friendsRecyclerViewAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // friendsRecyclerViewAdapter.notifyDataSetChanged();
    }




}
