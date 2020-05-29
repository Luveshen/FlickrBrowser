package luvi.pillay.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> photoList;
    private Context context;

    FlickrRecyclerViewAdapter(List<Photo> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view request");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        Log.d(TAG, "onBindViewHolder: " + photo.getTitle() + " --> " + position);
        Picasso.get().load(photo.getImage())
                .error(R.drawable.place_holder)
                .placeholder(R.drawable.place_holder)
                .into(holder.thumbNail);

        holder.title.setText(photo.getTitle());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return (photoList != null) ? photoList.size() : 0;
    }

    void loadNewData(List<Photo> newPhotos){
        photoList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return (photoList != null && !photoList.isEmpty()) ? photoList.get(position) : null;
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageVIewHolder";
        ImageView thumbNail = null;
        TextView title = null;

        public FlickrImageViewHolder(View itemView){
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbNail = itemView.findViewById(R.id.thumbNail);
            this.title = itemView.findViewById(R.id.title);
        }
    }
}
