package ua.pb.gallery.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import ua.pb.gallery.R;
import ua.pb.gallery.Utils;
import ua.pb.gallery.models.FolderEntity;

/**
 * Created by user on 15.08.15.
 */
public class GalleryFoldersRecyclerAdapter extends RecyclerView.Adapter<GalleryFoldersRecyclerAdapter.FolderViewHolder> implements FilterInterface{

    public static final int DESIRED_WIDTH = 240;

    public static final int DESIRED_HEIGHT = 320;

    private Activity activity;

    private ArrayList<FolderEntity> originList;
    private ArrayList<FolderEntity> filteredList;

    private OnItemClickListener onItemClickedCallback;

    private boolean isDualSpan;

    public void changeLayoutSpanType(boolean isDualSpan) {
        this.isDualSpan = isDualSpan;
    }

    public GalleryFoldersRecyclerAdapter(OnItemClickListener onItemClickedCallback, ArrayList<FolderEntity> originList, Activity activity) {
        this.onItemClickedCallback = onItemClickedCallback;
        this.originList = originList;
        this.filteredList = originList;
        this.activity = activity;
    }

    private FilenameFilter picturesfilter = new FilenameFilter() {//todo: extend file filter
        @Override
        public boolean accept(File dir, String filename) {
            if (filename.contains("jpeg") || filename.contains("jpg") || filename.contains("png") ) {
                return true;
            }
            return false;
        }
    };


    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        Log.d("Recycler", "onCreateViewHolder(ViewGroup, int) index = " + i);
        View view;
        if (isDualSpan)
            view = LayoutInflater.from(activity).inflate(R.layout.gallery_folder_item_05x, viewGroup, false);
        else
            view = LayoutInflater.from(activity).inflate(R.layout.gallery_folder_item_1x, viewGroup, false);

        return new FolderViewHolder(view);
    }

    @Override
    public int getItemCount() {
        Log.d("Recycler", "getItemCount() count = " + originList.size());
        return filteredList.size();
    }

    @Override
    public void onBindViewHolder(FolderViewHolder folderViewHolder, int i) {
        Log.d("Recycler", "onBindViewHolder(FolderViewHolder, int) index = " + i);
        FolderEntity entity = filteredList.get(i);
        folderViewHolder.folderName.setText(entity.getFolder().getName());

        File[] innerPhotos = entity.getFolder().listFiles(picturesfilter);
        folderViewHolder.photosCount.setText("" + innerPhotos.length);
        if (innerPhotos.length > 0) {
            Utils.IMAGE_LOADER.displayImage(Utils.FILE + innerPhotos[0].getAbsolutePath(), folderViewHolder.folderCover);
        }
        else
            Utils.IMAGE_LOADER.displayImage(Utils.DRAWABLE + R.drawable.ic_launcher, folderViewHolder.folderCover);
    }

    class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FrameLayout container;

        ImageView folderCover;
        TextView folderName;
        TextView photosCount;

        public FolderViewHolder(View itemView) {
            super(itemView);

            container = (FrameLayout) itemView.findViewById(R.id.container);

            folderCover = (ImageView) itemView.findViewById(R.id.imageView);
            folderName = (TextView) itemView.findViewById(R.id.textView);
            photosCount = (TextView) itemView.findViewById(R.id.textView3);

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

    @Override
    public void applyFilter(String filterKeyWord) {
        ArrayList<FolderEntity> newList = new ArrayList<>();
        for (FolderEntity folderEntity : originList) {
            String[] temp = folderEntity.getFolderFullPath().split("/");
            String folderName = temp[temp.length - 1];
            if (folderName.toLowerCase().contains(filterKeyWord)) {
                newList.add(folderEntity);
            }
        }
        filteredList = newList;
        notifyDataSetChanged();
    }
}
