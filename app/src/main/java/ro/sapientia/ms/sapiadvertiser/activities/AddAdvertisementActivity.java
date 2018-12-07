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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.adapters.AddAdvertisementRecycleAdapter;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;

// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.

public class AddAdvertisementActivity extends AppCompatActivity {

    private static final Integer PICK_IMAGE = 8;
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
    @BindView(R.id.imageButton2)
    ImageButton addImageBtn;
    @BindView(R.id.save_btn)
    Button postBtn;
    @BindView(R.id.image_recycle_view)
    RecyclerView images;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference storageReference;
    private DatabaseReference mAdvertisementsRef;
    private ArrayList<Uri> mainImageURI = new ArrayList<>();
    private String mUserId;
    private Advertisement mAdvertisement = new Advertisement();

    private List<Advertisement> advList;
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
        advList = new ArrayList<>();
        images.setHasFixedSize(true);
        images.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mUserId = mAuth.getCurrentUser().getUid();
        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);

        //addAdvertisement();
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    @OnClick(R.id.imageButton2)
    public void selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(AddAdvertisementActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(AddAdvertisementActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(AddAdvertisementActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

            mainImageURI.add(0, data.getData());
            advertisementsRecyclerAdapter = new AddAdvertisementRecycleAdapter(mainImageURI, AddAdvertisementActivity.this);
            images.setAdapter(advertisementsRecyclerAdapter);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            images.setLayoutManager(layoutManager);
            advertisementsRecyclerAdapter.notifyDataSetChanged();

        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @OnClick(R.id.save_btn)
    public void saveButton() {


        mAdvertisement.Title = title.getText().toString();
        mAdvertisement.ShortDescription = shortDescription.getText().toString();
        mAdvertisement.Location = location.getText().toString();
        mAdvertisement.LongDescription = longDescription.getText().toString();

    }


    private void addAdvertisement() {


        mAdvertisementsRef.child(mAdvertisement.Id).setValue(mAdvertisement);
        Toast.makeText(AddAdvertisementActivity.this, "The data is saved", Toast.LENGTH_LONG).show();


    }

}
