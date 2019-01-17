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
import com.example.kuba.raczejpiatek.InviteFriendList.InviteFriendList;
import com.example.kuba.raczejpiatek.InviteFriendList.InviteFriendListRecyclerViewArapter;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.StaticVariables;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView friendsListRecyclerView;
    private DatabaseReference userDatabaseRef;
    private String userID;
    public static ArrayList<String> InvitefriendsIdList;
    private static ArrayList<FindFriends> InvitefriendsList;
    private InviteFriendListRecyclerViewArapter inviteFriendListRecyclerViewArapter;
    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);


       friendsListRecyclerView = (RecyclerView) mMainView.findViewById(R.id.friendsListRecyclerView);

        InvitefriendsList = new ArrayList<>();
        userID = getActivity().getIntent().getStringExtra(StaticVariables.KEY_FRIEND_ID);
        InvitefriendsIdList = (ArrayList<String>) getActivity().getIntent().getSerializableExtra(StaticVariables.INVITE_FRIEND_LIST);
        if(InvitefriendsIdList != null && (!InvitefriendsIdList.isEmpty())){


            userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

            inviteFriendListRecyclerViewArapter = new InviteFriendListRecyclerViewArapter(getActivity().getBaseContext(), userDatabaseRef,InvitefriendsIdList,userID);friendsListRecyclerView.setHasFixedSize(true);
            friendsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            friendsListRecyclerView.setHasFixedSize(true);
            friendsListRecyclerView.setAdapter(inviteFriendListRecyclerViewArapter);
            inviteFriendListRecyclerViewArapter.notifyDataSetChanged();

        }


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        //       inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //       inviteFriendListRecyclerViewArapter.notifyDataSetChanged();
    }

}
