package ro.sapientia.ms.sapiadvertiser.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.adapters.AdvListRecycleAdapter;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.adv_list_view)
    RecyclerView advListView;
    private List<Advertisement> advList;
    private String mUserId;
    private AdvListRecycleAdapter advListRecycleAdapter;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAdvertisementsRef;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        advList = new ArrayList<>();
        advListView.setHasFixedSize(true);

        advListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);
        mUserId = mAuth.getCurrentUser().getUid();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getMyAdvertisements();

    }

    public void getMyAdvertisements() {

        Query recentList = mAdvertisementsRef;

        recentList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                advList.clear();
                for (DataSnapshot advertisementSnapshot : dataSnapshot.getChildren()) {

                    Advertisement adv = advertisementSnapshot.getValue(Advertisement.class);
                    if (adv.IsDeleted == false) {
                        advList.add(adv);
                    }
                    advListRecycleAdapter = new AdvListRecycleAdapter(advList);
                    advListView.setAdapter(advListRecycleAdapter);
                    advListRecycleAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
