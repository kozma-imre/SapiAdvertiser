package ro.sapientia.ms.sapiadvertiser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.sapientia.ms.sapiadvertiser.R;

// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class AddAdvertisementActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent mainScreenIntent = new Intent(AddAdvertisementActivity.this, AdvertisementListActivity.class);
                    startActivity(mainScreenIntent);
                    break;
                case R.id.navigation_profile:
                    Intent profileIntent = new Intent(AddAdvertisementActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    break;

                case R.id.navigation_new_blog:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advertisement);
        ButterKnife.bind(AddAdvertisementActivity.this);

        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }
}
