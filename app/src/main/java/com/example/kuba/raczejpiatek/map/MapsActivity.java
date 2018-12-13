package com.example.kuba.raczejpiatek.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kuba.raczejpiatek.BubbleTransformation;
import com.example.kuba.raczejpiatek.FindFriends;
import com.example.kuba.raczejpiatek.MyCallback;
import com.example.kuba.raczejpiatek.ProfilActivity;
import com.example.kuba.raczejpiatek.R;
import com.example.kuba.raczejpiatek.user.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private DatabaseReference reference;
    private DatabaseReference friendsReferences;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    public static final String KEY_MAPS = "KEY_MAPS";
    private double latitide, longitude;
    private double[] latLngDouble = new double[2];
    private FindFriends friend = new FindFriends();
    private String userIdString;
    private ArrayList<String> friendsIdFromDatabaseArrayList;
    private String userId;
    private Target mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userIdString = user.getUid();

        friendsIdFromDatabaseArrayList = getIdUsersFromTableFriendsInDatabase(userIdString);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent p = new Intent(MapsActivity.this, ProfilActivity.class);
                        startActivity(p);
                        break;
                    case R.id.navigation_dashboard:
                        return true;
                    case R.id.navigation_notifications:
                        Toast.makeText(MapsActivity.this, "może chat", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }

    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        latitide = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;

        //  Toast.makeText(this, latitide + " , " + longitude, Toast.LENGTH_SHORT).show();

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        shareLocation();

        for (int i = 0; i < friendsIdFromDatabaseArrayList.size(); i++) {
            setLocationFriendsInMap(friendsIdFromDatabaseArrayList.get(i));
            //  Toast.makeText(MapsActivity.this, friendsIdFromDatabaseArrayList.get(i), Toast.LENGTH_SHORT).show();

        }


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Obecna pozycja");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentUserLocationMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(cameraUpdate);

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void shareLocation() {
        reference.child(user.getUid()).child("is_sharing").setValue("true");
        reference.child(user.getUid()).child("lat").setValue(String.valueOf(latitide));
        reference.child(user.getUid()).child("lng").setValue(String.valueOf(longitude))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MapsActivity.this, "Błąd udostepnienia lokalizacji", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        reference.child(user.getUid()).child("is_sharing").setValue("false")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MapsActivity.this, "Udostepnianie lokalizacji zostało wstrzymane", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MapsActivity.this, "Udostepnianie lokalizacji nie zostało zatrzymane", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        super.onDestroy();
    }

    private ArrayList<String> getIdUsersFromTableFriendsInDatabase(String userID) {
        final ArrayList<String> usersIdArrayList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        friendsReferences = database.getReference().child("Users/" + userID + "/friends");
        friendsReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue().equals("accept")) {
                        String id = ds.getKey();
                        usersIdArrayList.add(id);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return usersIdArrayList;
    }

    private void setLocationFriendsInMap(String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        friendsReferences = database.getReference().child("Users/" + userID);
        latLngDouble = new double[2];
        friend = new FindFriends();
        userId = userID;
        readData(new FirebaseCallback() {
            @Override
            public void onCallback(double[] d, final FindFriends f) {
                if (d.length > 1) {
                    final LatLng latLng = new LatLng(d[0], d[1]);
                    setTargetOnImage(latLng, f);
                    setImageIconOnMarkerUsingPicasso(f);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            showActionsDialog(f.getId());
                            return false;
                        }

                    });

                }

            }
        });
    }


    private void readData(final FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("lat").exists() && dataSnapshot.child("lng").exists()) {
                    setLatAndLngToDoubleTable(dataSnapshot);
                    setFriendData(dataSnapshot);
                    firebaseCallback.onCallback(latLngDouble, friend);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        friendsReferences.addListenerForSingleValueEvent(valueEventListener);

    }

    private void setLatAndLngToDoubleTable(DataSnapshot dataSnapshot) {
        String lat = dataSnapshot.child("lat").getValue(String.class);
        String lng = dataSnapshot.child("lng").getValue(String.class);
        latLngDouble[0] = Double.parseDouble(lat);
        latLngDouble[1] = Double.parseDouble(lng);
    }

    private void setFriendData(DataSnapshot dataSnapshot) {
        String id = dataSnapshot.getKey();
        String name = dataSnapshot.child("first_name").getValue(String.class);
        String photo = dataSnapshot.child("profilURl").getValue(String.class);
        String sharing = dataSnapshot.child("is_sharing").getValue(String.class);
        boolean sharingB = Boolean.parseBoolean(sharing);
        friend.setFirst_name(name);
        friend.setProfilURl(photo);
        friend.setId(id);
        friend.setIs_sharing(sharingB);
    }

    private void setImageIconOnMarkerUsingPicasso(FindFriends f) {
        int color;
     //   Toast.makeText(MapsActivity.this, String.valueOf(f.isIs_sharing()), Toast.LENGTH_SHORT).show();
        if (f.isIs_sharing()) {
            color = Color.GREEN;
        } else {
            color = Color.RED;
        }
        Picasso.with(MapsActivity.this)
                .load(f.getProfilURl())
                .resize(250, 250)
                .centerCrop()
                .transform(new BubbleTransformation(10, color))
                .into(mTarget);

    }

    private void setTargetOnImage(final LatLng latLng, final FindFriends f) {
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                currentUserLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(f.getFirst_name())
                        .snippet("test address")
                );
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("picasso", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    private interface FirebaseCallback {
        void onCallback(double[] d, FindFriends friend);
    }

    private void showActionsDialog(final String id) {
        CharSequence colors[] = new CharSequence[]{"Wyświetl profil", "Rozpocznij czat"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showProfil(id);
                } else {
                    showChat();

                }
            }
        });
        builder.show();
    }


    private void showProfil(String id) {
        String key = id;
        Intent intent = new Intent(MapsActivity.this, ProfilActivity.class);
        intent.putExtra(KEY_MAPS, key);
        startActivity(intent);
    }

    private void showChat() {

    }
}
