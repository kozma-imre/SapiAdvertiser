package ro.sapientia.ms.sapiadvertiser.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.
import ro.sapientia.ms.sapiadvertiser.R;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }
}
