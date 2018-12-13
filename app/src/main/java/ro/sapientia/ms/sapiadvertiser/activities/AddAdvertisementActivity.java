package ro.sapientia.ms.sapiadvertiser.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.adapters.AddAdvertisementRecycleAdapter;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.models.User;
import ro.sapientia.ms.sapiadvertiser.models.UserPreview;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;


// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class AddAdvertisementActivity extends AppCompatActivity {

    private static final Integer PICK_IMAGE_MULTIPLE = 8;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    @BindView(R.id.title_text)
    EditText title;
    @BindView(R.id.location_text)
    EditText location;
    @BindView(R.id.short_desc_text)
    EditText shortDescription;
    @BindView(R.id.long_description_text)
    EditText longDescription;
    @BindView(R.id.add_image_btn)
    ImageButton addImageBtn;
    @BindView(R.id.save_btn)
    Button postBtn;
    @BindView(R.id.image_recycle_view)
    RecyclerView images;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.phone_input)
    IntlPhoneInput phoneInput;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference storageReference;
    private DatabaseReference mAdvertisementsRef;
    private DatabaseReference mUserRef;

    private String mUserId;
    private String advId;
    private Advertisement mAdvertisement = new Advertisement();
    private User mUser = new User();
    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private AddAdvertisementRecycleAdapter advertisementsRecyclerAdapter;



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

        images.setHasFixedSize(true);
        images.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mUserId = mAuth.getCurrentUser().getUid();
        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);
        mUserRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);

        getUser();

        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @OnClick(R.id.add_image_btn)
    public void selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(AddAdvertisementActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(AddAdvertisementActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(AddAdvertisementActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                mAdvertisement.ImageUrls = new ArrayList<String>();
                if (data.getData() != null) {
                    mArrayUri.add(0, data.getData());
                    callRecycleAdapter(mArrayUri);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(i, uri);
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        callRecycleAdapter(mArrayUri);
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void callRecycleAdapter(ArrayList<Uri> mArrayUri) {

        advertisementsRecyclerAdapter = new AddAdvertisementRecycleAdapter(mArrayUri, AddAdvertisementActivity.this);
        images.setAdapter(advertisementsRecyclerAdapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        images.setLayoutManager(layoutManager);
        advertisementsRecyclerAdapter.notifyDataSetChanged();
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @OnClick(R.id.save_btn)
    public void saveButton() {

        saveAdvertisements();

        if (validateSignUpData()) {

            progressBar.setVisibility(View.VISIBLE);

            for (int i = 0; i < mArrayUri.size(); i++) {

                final String randomName = UUID.randomUUID().toString();
                final StorageReference image_path = storageReference.child(getString(R.string.advertisement_pictures_location))
                        .child(randomName + "." + getFileExtension(mArrayUri.get(i)));

                image_path.putFile(mArrayUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mAdvertisement.ImageUrls.add(uri.toString());
                                mAdvertisementsRef.child(advId).setValue(mAdvertisement);
                                if (mAdvertisement.ImageUrls.size() == mArrayUri.size()) {
                                    Toast.makeText(AddAdvertisementActivity.this, getString(R.string.saved_data), Toast.LENGTH_LONG).show();

                                    progressBar.setVisibility(View.INVISIBLE);
                                    Intent home = new Intent(AddAdvertisementActivity.this, AdvertisementListActivity.class);
                                    startActivity(home);
                                }

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAdvertisementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(AddAdvertisementActivity.this, R.string.saved_data, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void saveAdvertisements() {

        mAdvertisement.CreatorUser = new UserPreview();
        // Get the advertisement id
        advId = mAdvertisementsRef.push().getKey();

        mAdvertisement.CreatorUser.ImageUrl = mUser.ImageUrl;
        mAdvertisement.CreatorUser.Name = mUser.FirstName + " " + mUser.LastName;
        mAdvertisement.CreatorUser.Id = mUserId;
        mAdvertisement.Title = title.getText().toString();
        mAdvertisement.ShortDescription = shortDescription.getText().toString();
        mAdvertisement.Location = location.getText().toString();
        mAdvertisement.LongDescription = longDescription.getText().toString();
        mAdvertisement.Id = advId;
        mAdvertisement.PhoneNumber = phoneInput.getNumber();
        mAdvertisement.CreatedTime = new Date();
    }

    private void getUser() {
        mUserRef.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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
        phoneInput.setNumber(mUser.PhoneNumber);
        phoneInput.setEnabled(false);
    }


    private boolean validateSignUpData() {

        if (mAdvertisement.Title.isEmpty()) {
            title.setError(getString(R.string.error_title));
            title.requestFocus();
            return false;
        }
        if (mAdvertisement.ShortDescription.isEmpty()) {
            shortDescription.setError(getString(R.string.error_short_description));
            shortDescription.requestFocus();
            return false;
        }
        if (mAdvertisement.LongDescription.isEmpty()) {
            longDescription.setError(getString(R.string.error_long_description));
            longDescription.requestFocus();
            return false;
        }
        if (mAdvertisement.Location.isEmpty()) {
            location.setError(getString(R.string.error_location));
            location.requestFocus();
            return false;
        }
        if (mAdvertisement.ImageUrls == null) {
            images.requestFocus();
            return false;
        }

        return true;
    }
}