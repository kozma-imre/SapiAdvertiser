package ro.sapientia.ms.sapiadvertiser.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.fragments.AddAdvertisementFragment;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;
import ro.sapientia.ms.sapiadvertiser.utils.GlideApp;

// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.


public class AdvertisementDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    @BindView(R.id.slider)
    SliderLayout mDemoSlider;
    @BindView(R.id.phone_text)
    TextView phoneInput;


    @BindView(R.id.location_text)
    TextView mLocation;
    @BindView(R.id.share_btn)
    ImageButton shareBtn;
    @BindView(R.id.delete_btn)
    ImageButton deleteBtn;
    @BindView(R.id.edit_btn)
    ImageButton editBtn;
    @BindView(R.id.report_btn)
    ImageButton reportBtn;
    @BindView(R.id.profile_image_view)
    ImageView profileImage;
    @BindView(R.id.user_text)
    TextView userName;


    @BindView(R.id.long_description_text)
    EditText mLongDescription;

    private DatabaseReference mAdvertisementsRef;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;

    private String id;
    private Advertisement adv;
    private String mUserId;
    private AddAdvertisementFragment addAdvertisementFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_detail);
        // We get the data with the intent

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("Id");

        }
        // and now we can retrieve the data from the database where the id is this what we got from the intent


        // Binding the id`s to the fields with butter knife
        ButterKnife.bind(AdvertisementDetailActivity.this);

        addAdvertisementFragment = new AddAdvertisementFragment();
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getAdvertisement();

    }

    private void getAdvertisement() {

        mAdvertisementsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adv = dataSnapshot.getValue(Advertisement.class);

                incrementNrOfViews();


                phoneInput.setEnabled(false);

                setImageSlider();

                phoneInput.setText(adv.PhoneNumber);

                mLocation.setText(adv.Location);

                mLongDescription.setText(adv.LongDescription);


                setControlButtons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void incrementNrOfViews() {
        adv.NumberOfViews += 1;
        mAdvertisementsRef.child(id).setValue(adv);
    }

    private void setControlButtons() {
        if (adv.CreatorUser.Id.equals(mUserId)) {

            deleteBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.VISIBLE);
            reportBtn.setVisibility(View.INVISIBLE);
            profileImage.setVisibility(View.INVISIBLE);
            userName.setVisibility(View.INVISIBLE);

        } else {

            deleteBtn.setVisibility(View.INVISIBLE);
            editBtn.setVisibility(View.INVISIBLE);
            reportBtn.setVisibility(View.VISIBLE);
            profileImage.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);

            GlideApp.with(AdvertisementDetailActivity.this)
                    .load(adv.CreatorUser.ImageUrl)
                    .error(R.drawable.default_image)
                    .override(300, 300)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);

            userName.setText(adv.CreatorUser.Name);
        }
    }

    private void setImageSlider() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();


        for (String image : adv.ImageUrls) {
            TextSliderView sliderView = new TextSliderView(AdvertisementDetailActivity.this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(image)
                    .description(adv.Title)
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(AdvertisementDetailActivity.this);

            // add your extra information
            // sliderView.bundle(new Bundle());
            //sliderView.getBundle().putString("extra", adv.Title);
            mDemoSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        //mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        //mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(AdvertisementDetailActivity.this);
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick(R.id.delete_btn)
    public void deleteAdvertisement() {

        adv.IsDeleted = true;
        mAdvertisementsRef.child(adv.Id).setValue(adv);

        Intent mainIntent = new Intent(AdvertisementDetailActivity.this, AdvertisementListActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @OnClick(R.id.edit_btn)
    public void editAdvertisement() {


        Intent mainIntent = new Intent(AdvertisementDetailActivity.this, AdvertisementListActivity.class);
        mainIntent.putExtra("edit", id);
        startActivity(mainIntent);
        finish();


    }

    @OnClick(R.id.share_btn)
    public void shareAdvertisement() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, adv.Title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, adv.LongDescription);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }


    @OnClick(R.id.report_btn)
    public void reportAdvertisement() {

        adv.IsReported = true;
        mAdvertisementsRef.child(adv.Id).setValue(adv);

    }


}
