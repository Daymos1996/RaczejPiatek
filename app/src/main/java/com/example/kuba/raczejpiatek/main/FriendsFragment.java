package com.example.kuba.raczejpiatek.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.StaticVariables;
import com.example.kuba.raczejpiatek.friends.FriendsActivity;
import com.example.kuba.raczejpiatek.friends.FriendsRecyclerViewAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView friendsListRecyclerView;
    private DatabaseReference userDatabaseRef;
    private String userID;
    public static ArrayList<String> friendsIdList;
    private static ArrayList<FindFriends> friendsList;
    private FriendsRecyclerViewAdapter friendsRecyclerViewAdapter;
    private View mMainView;


    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListRecyclerView = (RecyclerView) mMainView.findViewById(R.id.friendsListRecyclerView);


        userID = getActivity().getIntent().getStringExtra(StaticVariables.KEY_FRIEND_ID);
        friendsIdList = (ArrayList<String>) getActivity().getIntent().getSerializableExtra(StaticVariables.FRIEND_ID_LIST);
        if(!friendsIdList.isEmpty()) {
            userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

            friendsRecyclerViewAdapter = new FriendsRecyclerViewAdapter(getActivity().getBaseContext(), userDatabaseRef, friendsIdList);
            friendsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            friendsListRecyclerView.setHasFixedSize(true);
            friendsListRecyclerView.setAdapter(friendsRecyclerViewAdapter);
            friendsRecyclerViewAdapter.notifyDataSetChanged();

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
