package ro.sapientia.ms.sapiadvertiser.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.models.User;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;
import ro.sapientia.ms.sapiadvertiser.utils.GlideApp;

// If you put your Activities files to another folder than the default one. You need to import the
// com.example.your project.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class ProfileActivity extends AppCompatActivity {

    private static final Integer PICK_IMAGE = 100;
    private static final String TAG = "PROFILE ACTIVITY";
    // Butterknife
    @BindView(R.id.logout_btn)
    Button signoutBtn;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.phone_input)
    IntlPhoneInput phoneInput;
    @BindView(R.id.first_name)
    EditText firstNameText;
    @BindView(R.id.last_name_input)
    EditText lastNameText;
    @BindView(R.id.email_input)
    EditText email;
    @BindView(R.id.address_input)
    EditText address;
    @BindView(R.id.setup_progress)
    ProgressBar setupProgress;
    @BindView(R.id.save_btn)
    Button saveProfileBtn;
    @BindView(R.id.my_list_btn)
    Button mListAdv;
    private Uri mainImageURI = null;
    // Firebase autentificate ...
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersRef;
    private DatabaseReference mAdvertisementsRef;
    private StorageReference storageReference;
    // User class
    private String mUserId;
    private User mUser = new User();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent mainScreenIntent = new Intent(ProfileActivity.this, AdvertisementListActivity.class);
                    startActivity(mainScreenIntent);
                    break;
                case R.id.navigation_profile:

                    return true;
                case R.id.navigation_new_blog:
                    Intent addAdvertisementIntent = new Intent(ProfileActivity.this, AddAdvertisementActivity.class);
                    startActivity(addAdvertisementIntent);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mUserId = mAuth.getCurrentUser().getUid();
        mUsersRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);
        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);

        ButterKnife.bind(ProfileActivity.this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @OnClick(R.id.logout_btn)
    public void logOutButton() {
        mAuth.signOut();
        Intent signOutIntent = new Intent(ProfileActivity.this, LogInActivity.class);
        startActivity(signOutIntent);
        finish();
    }

    @OnClick(R.id.my_list_btn)
    public void goToMyAdvertisements() {
        Intent myAdvertisementsIntent = new Intent(ProfileActivity.this, MyAdvertisementsActivity.class);
        startActivity(myAdvertisementsIntent);
    }

    @OnClick(R.id.save_btn)
    public void saveButton() {
        setupProgress.setVisibility(View.VISIBLE);

        mUser.LastName = lastNameText.getText().toString();
        mUser.FirstName = firstNameText.getText().toString();
        mUser.Address = address.getText().toString();
        mUser.Email = email.getText().toString();

        if (validateSignUpData()) {

            saveUser();


        }

    }

    private void updateAdvertisments() {


        Query recentList = mAdvertisementsRef.orderByChild("creator_user/id").equalTo(mUserId);


        recentList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot advertisementSnapshot : dataSnapshot.getChildren()) {

                    Advertisement adv = advertisementSnapshot.getValue(Advertisement.class);
                    adv.CreatorUser.Name = mUser.FirstName + " " + mUser.LastName;
                    adv.CreatorUser.ImageUrl = mUser.ImageUrl;
                    adv.PhoneNumber = mUser.PhoneNumber;

                    mAdvertisementsRef.child(advertisementSnapshot.getKey())
                            .setValue(adv);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

    }

    private void saveUser() {

        if (mainImageURI != null) {
            // Function upload image

            String uniqueID = UUID.randomUUID().toString();
            final StorageReference image_path = storageReference.child("ProfilePictures").child(uniqueID + "." + getFileExtension(mainImageURI));

            image_path.putFile(mainImageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "The image is uploaded", Toast.LENGTH_LONG).show();
                            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSucces =" + uri.toString());

                                    mainImageURI = null;
                                    mUser.ImageUrl = uri.toString();
                                    mUsersRef.child(mUser.Id).setValue(mUser);

                                    Toast.makeText(ProfileActivity.this, "The data is saved", Toast.LENGTH_LONG).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                    updateAdvertisments();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mUsersRef.child(mUser.Id).setValue(mUser);
            Toast.makeText(ProfileActivity.this, "The data is saved", Toast.LENGTH_LONG).show();
            setupProgress.setVisibility(View.INVISIBLE);
            updateAdvertisments();
        }

    }

    @OnClick(R.id.profile_image)
    public void selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(ProfileActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_IMAGE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            mainImageURI = data.getData();
            Log.d(TAG, "MAIN IMAGE" + mainImageURI.getPath());

            GlideApp.with(ProfileActivity.this)
                    .load(mainImageURI)
                    .error(R.drawable.default_image)
                    .override(300, 300)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
    }

    private boolean validateSignUpData() {

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

        return true;
    }

    private void getUser() {
        mUsersRef.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                preFillInputFields();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preFillInputFields() {

        firstNameText.setText(mUser.FirstName);
        lastNameText.setText(mUser.LastName);
        email.setText(mUser.Email);
        address.setText(mUser.Address);

        GlideApp.with(ProfileActivity.this)
                .load(mUser.ImageUrl)
                .override(300, 300)
                .error(R.drawable.default_image)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage);

        phoneInput.setNumber(mUser.PhoneNumber);
        phoneInput.setEnabled(false);
    }

}