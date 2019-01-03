package ro.sapientia.ms.sapiadvertiser.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.activities.AdvertisementDetailActivity;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.GlideApp;


public class AdvListRecycleAdapter extends RecyclerView.Adapter<AdvListRecycleAdapter.ViewHolder> {

    private List<Advertisement> advList;
    private Context mCtx;


    public AdvListRecycleAdapter(List<Advertisement> advList, Context mCtx) {

        this.mCtx = mCtx;

        this.advList = advList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.advertisements_list_item, viewGroup, false);
        return new AdvListRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Advertisement advertisement = advList.get(i);

        viewHolder.title.setText(advertisement.Title);
        viewHolder.descView.setText(advertisement.ShortDescription);


        GlideApp.with(mCtx)
                .load(advertisement.ImageUrls.get(0))
                .error(R.drawable.default_image2)
                .override(300, 300)
                .into(viewHolder.pictureView);

        GlideApp.with(mCtx)
                .load(advertisement.CreatorUser.ImageUrl)
                .error(R.drawable.default_image)
                .override(300, 300)
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.profileImageView);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent detailAdvertisementIntent = new Intent(mCtx, AdvertisementDetailActivity.class);
                detailAdvertisementIntent.putExtra("Id", advertisement.Id);
                mCtx.startActivity(detailAdvertisementIntent);

            }
        });

        viewHolder.numberOfViews.setText("" + advertisement.NumberOfViews);
    }

    @Override
    public int getItemCount() {
        return advList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        CardView cardView;
        private TextView numberOfViews;
        private ImageView profileImageView;
        private ImageView pictureView;
        private TextView title;
        private TextView descView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            descView = mView.findViewById(R.id.adv_description);
            title = mView.findViewById(R.id.adv_title);
            pictureView = mView.findViewById(R.id.adv_image);
            profileImageView = mView.findViewById(R.id.profile_image_view);
            numberOfViews = mView.findViewById(R.id.nr_views);
            cardView = mView.findViewById(R.id.my_adv_post);
        }

    }
}
