package gr.gov.yme.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import gr.gov.yme.R;
import gr.gov.yme.activities.MapActivity;
import gr.gov.yme.models.location.Image;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    Context ctx;


    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Image> imageList;

    public ImageAdapter(List<Image> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);   //todo: change parent_item layout file

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image imageItem = imageList.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new FitCenter(), new RoundedCorners(16));
        Glide.with(ctx)
                .load(imageItem.url)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(holder.image);


        //holder.image.setImageResource();
    }

    @Override
    public int getItemCount() {
        if (imageList == null) {
            return 0;
        } else {
            return imageList.size();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ImageViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image_recyclable);
        }
    }
}


