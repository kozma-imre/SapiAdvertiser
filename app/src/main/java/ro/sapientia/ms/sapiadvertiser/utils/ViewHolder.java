package ro.sapientia.ms.sapiadvertiser.utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import ro.sapientia.ms.sapiadvertiser.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    private ImageView blogImageView;
    public Context context;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
    }

    public void setDetails(Context ctx,String Title,String ShortDescription,String ImageUrls)
    {//Views
        TextView mTitleTv=mView.findViewById(R.id.rTitleTv);
        TextView mDetailTv=mView.findViewById(R.id.rDescriptionTv);
        ImageView mImageIv=mView.findViewById(R.id.rImageView);

       // set data to views

        mTitleTv.setText(Title);
        mDetailTv.setText(ShortDescription);
        //Picasso.get().load(ImageUrls).into(mImageIv);
        Glide.with(ctx)
                .load(ImageUrls)
                .into(mImageIv);


    }
    public void setBlogImage(String downloadUri, String thumbUri){

        blogImageView = mView.findViewById(R.id.profile_image);

        RequestOptions requestOptions = new RequestOptions();
       // requestOptions.placeholder(R.drawable.image_placeholder);

        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                Glide.with(context).load(thumbUri)
        ).into(blogImageView);


    }


}
