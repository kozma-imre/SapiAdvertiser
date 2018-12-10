package ro.sapientia.ms.sapiadvertiser.utils;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.List;

import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;

public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    private ImageView blogImageView;
    private StorageReference mStorage;
    public Context context;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
    }







    public void setDetails(Context ctx,String Title,String ShortDescription,String ImageUrls) {//Views
        TextView mTitleTv = mView.findViewById(R.id.adv_title);
        TextView mDetailTv = mView.findViewById(R.id.adv_description);
        ImageView mImageIv = mView.findViewById(R.id.adv_image);

        // set data to views

        mTitleTv.setText(Title);
        mDetailTv.setText(ShortDescription);
        //Picasso.get().load(ImageUrls).into(mImageIv);
        Glide.with(ctx)
                .load(ImageUrls)
                .into(mImageIv);

        ImageView profile_image=mView.findViewById(R.id.profile_image_view);

        mStorage = FirebaseStorage.getInstance().getReference();
        //Uri uri=mStorage.child("ProfilePictures").("").

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
