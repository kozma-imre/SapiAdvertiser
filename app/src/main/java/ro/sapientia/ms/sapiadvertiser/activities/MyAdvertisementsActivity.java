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

public class MyAdvertisementsActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent mainScreenIntent = new Intent(MyAdvertisementsActivity.this, AdvertisementListActivity.class);
                    startActivity(mainScreenIntent);
                    finish();
                    break;
                case R.id.navigation_profile:
                    Intent profileScreenIntent = new Intent(MyAdvertisementsActivity.this, ProfileActivity.class);
                    startActivity(profileScreenIntent);
                    finish();
                    break;
                case R.id.navigation_new_blog:
                    Intent addAdvertisementIntent = new Intent(MyAdvertisementsActivity.this, AddAdvertisementActivity.class);
                    startActivity(addAdvertisementIntent);
                    finish();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_advertisements);

        ButterKnife.bind(MyAdvertisementsActivity.this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
