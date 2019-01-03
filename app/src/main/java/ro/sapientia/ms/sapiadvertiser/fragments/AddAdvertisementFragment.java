package ro.sapientia.ms.sapiadvertiser.fragments;


import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ro.sapientia.ms.sapiadvertiser.activities.AdvertisementListActivity;
import ro.sapientia.ms.sapiadvertiser.adapters.AddAdvRecycleAdapter;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.models.User;
import ro.sapientia.ms.sapiadvertiser.models.UserPreview;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAdvertisementFragment extends Fragment {

    private static final Integer PICK_IMAGE = 100;

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

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.phone_input)
    IntlPhoneInput phoneInput;

    @BindView(R.id.image_recycle_view)
    RecyclerView images;

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
    private AddAdvRecycleAdapter advListRecycleAdapter;
    private String id = null;

    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    public AddAdvertisementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_advertisement, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(R.string.title_activity_add_advertisements);
        images.setHasFixedSize(true);
        images.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mUserId = mAuth.getCurrentUser().getUid();
        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);
        mUserRef = mFirebaseDatabase.getReference().child(Constants.USERS_CHILD);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getString("AdvId");
        }

        getUser();
        if (id != null) {
            getAdvertisement();
        }


        return view;
    }

    @OnClick(R.id.add_image_btn)
    public void selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
                if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                    final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PICK_IMAGE_CAMERA);
                            } else if (options[item].equals("Choose From Gallery")) {
                                dialog.dismiss();
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                                pickPhoto.setType("image/*");
                                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                } else
                    Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                // When an Image is picked
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    // Get the Image from data
                    mAdvertisement.ImageUrls = new ArrayList<String>();
                    if (data.getData() != null) {
                        mArrayUri.add(0, data.getData());
                        callRecycleAdapter(mArrayUri);

                    }
                } else {
                    Toast.makeText(getActivity(), "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == getActivity().RESULT_OK && null != data) {
            mAdvertisement.ImageUrls = new ArrayList<String>();
            if (data.getData() != null) {
                mArrayUri.add(0, data.getData());
                callRecycleAdapter(mArrayUri);
            }
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(i, uri);
                }

                callRecycleAdapter(mArrayUri);
            }
        } else {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(i, uri);
                }

                callRecycleAdapter(mArrayUri);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void callRecycleAdapter(ArrayList<Uri> mArrayUri) {

        advListRecycleAdapter = new AddAdvRecycleAdapter(mArrayUri, this);
        images.setAdapter(advListRecycleAdapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        images.setLayoutManager(layoutManager);
        advListRecycleAdapter.notifyDataSetChanged();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
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
                                    Toast.makeText(getActivity(), getString(R.string.saved_data), Toast.LENGTH_LONG).show();

                                    progressBar.setVisibility(View.INVISIBLE);
                                    Intent home = new Intent(getActivity(), AdvertisementListActivity.class);
                                    startActivity(home);
                                }

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), R.string.saved_data, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void saveAdvertisements() {

        mAdvertisement.CreatorUser = new UserPreview();
        // Get the advertisement id
        if (id != null) {
            advId = id;
        } else {
            advId = mAdvertisementsRef.push().getKey();
        }


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

                preFillInputFieldsUser();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAdvertisement() {
        mAdvertisementsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdvertisement = dataSnapshot.getValue(Advertisement.class);

                preFillInputFieldsAdvertisement();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preFillInputFieldsAdvertisement() {
        title.setText(mAdvertisement.Title);
        shortDescription.setText(mAdvertisement.ShortDescription);
        longDescription.setText(mAdvertisement.LongDescription);
        location.setText(mAdvertisement.Location);


    }


    private void preFillInputFieldsUser() {
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
