package ro.sapientia.ms.sapiadvertiser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.models.User;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;

// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SIGNUP ACTIVITY";
    @BindView(R.id.first_name_register_edittext)
    EditText firstNameText;
    @BindView(R.id.last_name_register_edittext)
    EditText lastNameText;
    @BindView(R.id.signup_progress)
    ProgressBar signupProgresBar;
    @BindView(R.id.sign_up_btn)
    Button signupBtn;
    @BindView(R.id.my_phone_input)
    IntlPhoneInput phoneInput;

    //firebase autintificate
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String mUserId;

    private User mUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        myRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);

        checkIfUserAllreadySignedup();

        //binding the id`s to the fields with butterknife
        ButterKnife.bind(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        preFillPhoneNumber();
    }


    private void checkIfUserAllreadySignedup() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User allready saved in database
                if (dataSnapshot.hasChild(mUserId)) {
                    Intent mainIntent = new Intent(SignUpActivity.this, AdvertisementListActivity.class);
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

    private void preFillPhoneNumber() {
        String userPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        phoneInput.setNumber(userPhoneNumber);
    }


    @OnClick(R.id.sign_up_btn)
    public void signup() {
        mUser.LastName = lastNameText.getText().toString();
        mUser.FirstName = firstNameText.getText().toString();
        mUser.PhoneNumber = phoneInput.getNumber();
        mUser.Id = mAuth.getCurrentUser().getUid();

        if (validateSignupData()) {

            saveUser();

            Intent myAdvertList = new Intent(SignUpActivity.this, AdvertisementListActivity.class);
            startActivity(myAdvertList);
            finish();

        }
    }

    private boolean validateSignupData() {

        if (mUser.FirstName.isEmpty()) {
            firstNameText.setError("First Name Required");
            firstNameText.requestFocus();
            return false;
        }
        if (mUser.LastName.isEmpty()) {
            lastNameText.setError("Last Name Required");
            lastNameText.requestFocus();
            return false;
        }
        if (!phoneInput.isValid()) {
            phoneInput.requestFocus();
            Toast.makeText(SignUpActivity.this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void saveUser() {
        myRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);
        myRef.child(mUser.Id).setValue(mUser);
    }

}
