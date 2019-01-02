package ro.sapientia.ms.sapiadvertiser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.fragments.AddAdvertisementFragment;
import ro.sapientia.ms.sapiadvertiser.fragments.HomeFragment;
import ro.sapientia.ms.sapiadvertiser.fragments.ProfileFragment;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;

// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class AdvertisementListActivity extends AppCompatActivity {


    private static final String TAG = "ADVLIST ACTIVITY";


    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private AddAdvertisementFragment addAdvertisementFragment;
    private FirebaseAuth mAuth;
    private String mUserId;
    private DatabaseReference mUsersRef;
    private FirebaseDatabase mFirebaseDatabase;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(homeFragment);
                    return true;
                case R.id.navigation_profile:
                    replaceFragment(profileFragment);
                    return true;
                case R.id.navigation_new_blog:
                    replaceFragment(addAdvertisementFragment);
                    return true;
                default:
                    return false;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_list);


        ButterKnife.bind(AdvertisementListActivity.this);

        // FRAGMENTS
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        addAdvertisementFragment = new AddAdvertisementFragment();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String myParam = extras.getString("edit");

            Bundle bundle = new Bundle();
            bundle.putString("AdvId", myParam);
            addAdvertisementFragment.setArguments(bundle);
            replaceFragment(addAdvertisementFragment);
        } else {
            replaceFragment(homeFragment);
        }







        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mUsersRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);

        checkIfUserSignedup();


        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    private void checkIfUserSignedup() {
        mUsersRef.addValueEventListener(new ValueEventListener() {
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

    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }
}
