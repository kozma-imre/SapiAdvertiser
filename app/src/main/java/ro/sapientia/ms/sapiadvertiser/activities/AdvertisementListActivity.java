package ro.sapientia.ms.sapiadvertiser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;
import ro.sapientia.ms.sapiadvertiser.utils.ViewHolder;


// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class AdvertisementListActivity extends AppCompatActivity {


    private static final String TAG = "ADVLIST ACTIVITY";

    @BindView(R.id.signout_btn)
    Button signoutButton;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private FirebaseAuth mAuth;
    private String mUserId;
    private DatabaseReference mUsersRef;
    private DatabaseReference mUsersRefAdv;
    private FirebaseDatabase mFirebaseDatabase;
    RecyclerView mRecyclerView;
   // final String blogPostId = blog_list.get(position).BlogPostId;
    //final String currentUserId = FirebaseAuth.getCurrentUser().getUid();


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

                    break;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_list);

        ButterKnife.bind(AdvertisementListActivity.this);



        //send Query to Firebase

        mFirebaseDatabase = FirebaseDatabase.getInstance();

       mUsersRefAdv = mFirebaseDatabase.getReference("advertisements");
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
       mUsersRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);

        checkIfUserSignedup();

        //Actionar
        ActionBar actionBar = getSupportActionBar();
        //set title
        actionBar.setTitle("Posts  Lists");

        //RecycleView

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        //set layout as LinearLayout

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Advertisement,ViewHolder>  firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Advertisement, ViewHolder>(Advertisement.class,R.layout.row,ViewHolder.class,mUsersRefAdv) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Advertisement model, int position) {
                        viewHolder.setDetails(getApplicationContext(),model.getTitle(),model.getShortDescription(),model.getImageUrls().get(0) );

                     
                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
