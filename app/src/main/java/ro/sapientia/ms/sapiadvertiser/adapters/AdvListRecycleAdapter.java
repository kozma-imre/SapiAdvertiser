package ro.sapientia.ms.sapiadvertiser.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;


public class AdvListRecycleAdapter extends RecyclerView.Adapter<AdvListRecycleAdapter.ViewHolder> {

    private List<Advertisement> advList;
    private String mId;

    public AdvListRecycleAdapter(List<Advertisement> advList) {

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
        viewHolder.numberOfViews.setText("" + advertisement.NumberOfViews);


    }

    @Override
    public int getItemCount() {
        return advList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        private TextView numberOfViews;


        private TextView title;
        private TextView descView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            descView = mView.findViewById(R.id.adv_description);
            title = mView.findViewById(R.id.adv_title);

            numberOfViews = mView.findViewById(R.id.nr_views);
        }

    }
}
