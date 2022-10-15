package gr.gov.yme.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import gr.gov.yme.R;
import gr.gov.yme.activities.MapActivity;
import gr.gov.yme.models.location.Image;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final String TAG = "ImageAdapter";
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

        ctx = holder.itemView.getContext();

        Image imageItem = imageList.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(/*new FitCenter(), new RoundedCorners(16)*/ new CenterInside());
        Log.i(TAG, "Image url: " + imageItem.url);
        GlideApp.with(ctx)
                .load(imageItem.url)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(holder.image);
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


