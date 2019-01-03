package com.example.kuba.raczejpiatek.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.login.LoginActivity;
import com.example.kuba.raczejpiatek.map.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private String userID;
    private ArrayList<String> friendsIdList;
    private ArrayList<String> InviteFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Raczej Piatek");



        mAuth = FirebaseAuth.getInstance();
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        userID = getIntent().getStringExtra("USER_ID");
        friendsIdList = (ArrayList<String>)getIntent().getSerializableExtra("FRIEND_ID_LIST");
        InviteFriends=(ArrayList<String>) getIntent().getSerializableExtra("Invite_FRIEND_LIST");



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.navigation_dashboard:
                        Intent p = new Intent(MainActivity.this, MapsActivity.class);
                        p.putExtra("USER_ID", userID);
                        p.putExtra("FRIEND_ID_LIST", friendsIdList);
                        p.putExtra("Invite_FRIEND_LIST", InviteFriends);
                        startActivity(p);
                        return true;
                    case R.id.navigation_notifications:
                        Toast.makeText(MainActivity.this, "może chat", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_friends:
                        break;

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
        Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
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
             Intent profileIntent = new Intent(MainActivity.this,ProfilActivity.class);
             startActivity(profileIntent);
         }

         return  true;
    }
}
