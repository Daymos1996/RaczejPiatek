package com.example.kuba.raczejpiatek.chatList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kuba.raczejpiatek.main.FriendsFragment;


class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ListMessageFromFriendsFragment listMessageFromFriendsFragment = new ListMessageFromFriendsFragment();
                return listMessageFromFriendsFragment;

            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;






            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Wiadomosci";

            case 1:
                return "Przyjaciele";



            default:
                return null;
        }
    }
}
