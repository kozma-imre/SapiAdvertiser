package ro.sapientia.ms.sapiadvertiser.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.activities.AdvertisementDetailActivity;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;
import ro.sapientia.ms.sapiadvertiser.utils.GlideApp;

public class AdvertisementsRecyclerAdapter extends RecyclerView.Adapter<AdvertisementsRecyclerAdapter.ViewHolder> {

    private static final String TAG = "RECYCLE ADAPTER";
    private List<Advertisement> advList;
    private Context mCtx;
    private String mId;


    public AdvertisementsRecyclerAdapter(List<Advertisement> advList, Context mCtx) {

        this.mCtx = mCtx;
        this.advList = advList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.advertisements_list_item, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        Log.d(TAG, "onBINDHOLDER : called");


        final Advertisement advertisement = advList.get(i);
        GlideApp.with(mCtx)
                .load(advertisement.ImageUrls.get(0))
                .error(R.drawable.loading)
                .override(300, 300)
                .into(viewHolder.pictureView);

        GlideApp.with(mCtx)
                .load(advertisement.CreatorUser.ImageUrl)
                .error(R.drawable.default_image)
                .override(300, 300)
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.profileImageView);

        viewHolder.title.setText(advertisement.Title);
        viewHolder.descView.setText(advertisement.ShortDescription);
        viewHolder.numberOfViews.setText("" + advertisement.NumberOfViews);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentNumber = Integer.parseInt( viewHolder.numberOfViews.getText().toString() );
                mId = advertisement.Id;
                //String currentAdvId = advList.get(0).Id;
                FirebaseDatabase.getInstance().getReference().child(Constants.ADVERTISEMENTS_CHILD).child(mId).child(Constants.ADVERTISEMENT_VIEWS).setValue( currentNumber+1 );


                Intent detailAdvertisementIntent = new Intent(mCtx, AdvertisementDetailActivity.class);
                detailAdvertisementIntent.putExtra("Id", mId);
                mCtx.startActivity(detailAdvertisementIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return advList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        private TextView numberOfViews;
        private ImageView profileImageView;
        private ImageView pictureView;
        private TextView title;
        private TextView descView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pictureView = itemView.findViewById(R.id.adv_image);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
            descView = itemView.findViewById(R.id.adv_description);
            title = itemView.findViewById(R.id.adv_title);
            cardView = itemView.findViewById(R.id.my_adv_post);
            numberOfViews = itemView.findViewById(R.id.nr_views);
        }


    }
}
