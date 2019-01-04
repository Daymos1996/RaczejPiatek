package com.example.kuba.raczejpiatek.chatList;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.login.LoginActivity;
import com.example.kuba.raczejpiatek.main.MainActivity;
import com.example.kuba.raczejpiatek.map.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.kuba.raczejpiatek.StaticVariables.CHAT_FRIEND_ID_LIST;
import static com.example.kuba.raczejpiatek.StaticVariables.FRIEND_ID_LIST;
import static com.example.kuba.raczejpiatek.StaticVariables.INVITE_FRIEND_LIST;
import static com.example.kuba.raczejpiatek.StaticVariables.KEY_FRIEND_ID;
import static com.example.kuba.raczejpiatek.StaticVariables.KEY_USER_ID;

public class ChatFriendsListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private String userID, friendID;
    private ArrayList<String> friendsIdList;
    private ArrayList<String> InviteFriends;
    private ArrayList<String> chatsFriendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Raczej Piatek");



        mAuth = FirebaseAuth.getInstance();
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        userID = getIntent().getStringExtra(KEY_USER_ID);
        friendID = getIntent().getStringExtra(KEY_FRIEND_ID);
        friendsIdList = (ArrayList<String>)getIntent().getSerializableExtra(FRIEND_ID_LIST);
        InviteFriends=(ArrayList<String>) getIntent().getSerializableExtra(INVITE_FRIEND_LIST);
        chatsFriendsList=(ArrayList<String>) getIntent().getSerializableExtra(CHAT_FRIEND_ID_LIST);




        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(ChatFriendsListActivity.this, ProfilActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.navigation_dashboard:
                        Intent p = new Intent(ChatFriendsListActivity.this, MapsActivity.class);
                        p.putExtra(KEY_FRIEND_ID, userID);
                        p.putExtra(FRIEND_ID_LIST, friendsIdList);
                        p.putExtra(INVITE_FRIEND_LIST, InviteFriends);
                        p.putExtra(CHAT_FRIEND_ID_LIST, chatsFriendsList);
                        startActivity(p);
                        return true;
                    case R.id.navigation_notifications:
                        break;
                    case R.id.navigation_friends:
                        Intent f = new Intent(ChatFriendsListActivity.this, MainActivity.class);
                        f.putExtra(KEY_FRIEND_ID, userID);
                        f.putExtra(KEY_USER_ID, userID);
                        f.putExtra(FRIEND_ID_LIST, friendsIdList);
                        f.putExtra(INVITE_FRIEND_LIST, InviteFriends);
                        f.putExtra(CHAT_FRIEND_ID_LIST, chatsFriendsList);
                        startActivity(f);
                        return true;

                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){

            sendToStart();

        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(ChatFriendsListActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()== R.id.main_logout){
            // FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if(item.getItemId()== R.id.profile){
            Intent profileIntent = new Intent(ChatFriendsListActivity.this,ProfilActivity.class);
            startActivity(profileIntent);
        }

        return  true;
    }
}