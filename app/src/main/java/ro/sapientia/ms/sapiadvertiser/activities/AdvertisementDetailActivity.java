package ro.sapientia.ms.sapiadvertiser.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ro.sapientia.ms.sapiadvertiser.R;

// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.


public class AdvertisementDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_detail);
// We get the data with the intent


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("Id");
            Toast.makeText(AdvertisementDetailActivity.this, "az id :" + id, Toast.LENGTH_SHORT).show();
        }
// and now we can retrieve the data from the database where the id is this what we got from the intent

    }
}
