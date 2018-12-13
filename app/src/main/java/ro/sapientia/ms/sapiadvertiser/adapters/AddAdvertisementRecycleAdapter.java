package ro.sapientia.ms.sapiadvertiser.adapters;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.utils.GlideApp;


public class AddAdvertisementRecycleAdapter extends RecyclerView.Adapter<AddAdvertisementRecycleAdapter.ViewHolder> {

    private static final String TAG = "RECYCLE ADAPTER";
    private ArrayList<Uri> advList;
    private Context mCtx;


    public AddAdvertisementRecycleAdapter(ArrayList<Uri> advList, Context mCtx) {

        this.mCtx = mCtx;
        this.advList = advList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_images, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Uri mainImageUri = advList.get(i);

        GlideApp.with(mCtx)
                .load(mainImageUri)
                .error(R.drawable.default_image2)
                .into(viewHolder.pictureView);
    }

    @Override
    public int getItemCount() {
        return advList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView pictureView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pictureView = itemView.findViewById(R.id.list_images);

        }
    }
}