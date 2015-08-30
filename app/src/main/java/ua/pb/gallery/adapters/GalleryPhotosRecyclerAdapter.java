package ua.pb.gallery.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

import ua.pb.gallery.Utils;
import ua.pb.gallery.models.PhotoEntity;
import ua.pb.gallery.R;

/**
 * Created by user on 16.08.15.
 */
@Deprecated
public class GalleryPhotosRecyclerAdapter extends RecyclerView.Adapter<GalleryPhotosRecyclerAdapter.PhotoViewHolder> {

    private OnItemClickListener onItemClickedCallback;

    private ArrayList<PhotoEntity> photos;

    private Activity activity;

    public GalleryPhotosRecyclerAdapter(OnItemClickListener onItemClickedCallback, ArrayList<PhotoEntity> list, Activity activity) {
        this.onItemClickedCallback = onItemClickedCallback;
        this.photos = list;
        this.activity = activity;
    }



    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.gallery_photo_item, viewGroup, false);

        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoEntity entity = photos.get(position);
        holder.folderName.setText(entity.getPhotoFile().getName());

        File photo = photos.get(position).getPhotoFile();

        // Load image, decode it to Bitmap and return Bitmap to callback
//        ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
//        Utils.IMAGE_LOADER.loadImage(imageUri, targetSize, options, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                // Do whatever you want with Bitmap
//            }
//        });

        Utils.IMAGE_LOADER.displayImage(Utils.FILE + photo.getAbsolutePath(), holder.folderCover);

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FrameLayout container;

        ImageView folderCover;
        TextView folderName;



        public PhotoViewHolder(View itemView) {
            super(itemView);

            container = (FrameLayout) itemView.findViewById(R.id.container);

            folderCover = (ImageView) itemView.findViewById(R.id.imageView);
            folderName = (TextView) itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedCallback != null) {
                onItemClickedCallback.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

}
