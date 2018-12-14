package com.example.kuba.raczejpiatek.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
            return requestsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            case 3:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;

                default:
                    return null;

        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Zaproszenia";

            case 1:
                return "Wiadomosci";

            case 2:
                return "Przyjaciele";

            case 3:
                return "Mapa";

            default:
                return null;
        }
    }
}
