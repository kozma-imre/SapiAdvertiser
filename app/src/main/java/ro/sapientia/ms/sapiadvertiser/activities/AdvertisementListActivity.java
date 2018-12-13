package ro.sapientia.ms.sapiadvertiser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.sapientia.ms.sapiadvertiser.R;

import ro.sapientia.ms.sapiadvertiser.adapters.AdvertisementsRecyclerAdapter;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;


// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class AdvertisementListActivity extends AppCompatActivity {


    private static final String TAG = "ADVLIST ACTIVITY";

    @BindView(R.id.signout_btn)
    Button signoutButton;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.recyclerView)
    RecyclerView advertListView;

    //RecyclerView advertListView;

    private List<Advertisement> advList;
    private AdvertisementsRecyclerAdapter advertisementsRecyclerAdapter;

    private FirebaseAuth mAuth;
    private String mUserId;
    private DatabaseReference mAdvertisementsRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_list);
        // advertListView = findViewById(R.id.advert_list_view);

        ButterKnife.bind(AdvertisementListActivity.this);
        advList = new ArrayList<>();
        advertListView.setHasFixedSize(true);
        advertListView.setLayoutManager(new LinearLayoutManager(this));

        //mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);


        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);

        //checkIfUserSignedup();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AdvertisementListActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAdvertisements();
    }


    public void getAdvertisements() {

        Query recentList = mAdvertisementsRef.orderByChild("id");//.equalTo(mUserId);

        recentList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                advList.clear();
                for (DataSnapshot advertisementSnapshot : dataSnapshot.getChildren()) {

                    Advertisement adv = advertisementSnapshot.getValue(Advertisement.class);
                    if (adv.IsDeleted == false) {
                        advList.add(adv);
                    }
                    advertisementsRecyclerAdapter = new AdvertisementsRecyclerAdapter(advList, AdvertisementListActivity.this);
                    advertListView.setAdapter(advertisementsRecyclerAdapter);
                    advertisementsRecyclerAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    break;
                case R.id.navigation_profile:
                    Intent profileIntent = new Intent(AdvertisementListActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);


                    break;
                case R.id.navigation_new_blog:
                    Intent addAdvertisementIntent = new Intent(AdvertisementListActivity.this, AddAdvertisementActivity.class);
                    startActivity(addAdvertisementIntent);
                    finish();
                    break;
            }
            return false;
        }
    };
}

/*
    private void checkIfUserSignedup() {
        mAdvertisementsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User allready saved in database

                if (!dataSnapshot.hasChild(mUserId)) {
                    Intent mainIntent = new Intent(AdvertisementListActivity.this, SignUpActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

*/
//}