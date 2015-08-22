package ua.pb.gallery.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ua.pb.gallery.R;
import ua.pb.gallery.Utils;
import ua.pb.gallery.models.FolderEntity;

/**
 * Created by user on 15.08.15.
 */
public class GalleryFoldersRecyclerAdapter extends RecyclerView.Adapter<GalleryFoldersRecyclerAdapter.FolderViewHolder> {

    public static final int DESIRED_WIDTH = 240;

    public static final int DESIRED_HEIGHT = 320;

    private Activity activity;

    private ArrayList<FolderEntity> list;

    private OnItemClickListener onItemClickedCallback;

    private int itemViewWidth = -2, itemViewHeight = -2;

    private ImageLoader imageLoader = ImageLoader.getInstance();


    public GalleryFoldersRecyclerAdapter(OnItemClickListener onItemClickedCallback, ArrayList<FolderEntity> list, Activity activity) {
        this.onItemClickedCallback = onItemClickedCallback;
        this.list = list;
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

    public void setIteViewDimensions(int width, int height) {
        itemViewHeight = height;
        itemViewWidth = width;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        Log.d("Recycler", "onCreateViewHolder(ViewGroup, int) index = " + i);
        View view = LayoutInflater.from(activity).inflate(R.layout.gallery_folder_item, viewGroup, false);

        return new FolderViewHolder(view);
    }

    @Override
    public int getItemCount() {
        Log.d("Recycler", "getItemCount() count = " + list.size());
        return list.size();
    }

    @Override
    public void onBindViewHolder(FolderViewHolder folderViewHolder, int i) {
        Log.d("Recycler", "onBindViewHolder(FolderViewHolder, int) index = " + i);
        FolderEntity entity = list.get(i);
        folderViewHolder.folderName.setText(entity.getFolder().getName());

        File[] innerPhotos = entity.getFolder().listFiles(picturesfilter);
        folderViewHolder.photosCount.setText("" + innerPhotos.length);
        if (innerPhotos.length > 0) {
            Utils.IMAGE_LOADER.displayImage(Utils.FILE + innerPhotos[0].getAbsolutePath(), folderViewHolder.folderCover);
//            Utils.IMAGE_LOADER.displayImage(Utils.FILE + innerPhotos[0], folderViewHolder.folderCover, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    updateView(view, loadedImage);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                }
//            } );
        }
        else
            Utils.IMAGE_LOADER.displayImage(Utils.DRAWABLE + R.drawable.ic_launcher, folderViewHolder.folderCover);
        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        //  which implements ImageAware interface)

    }

    private void updateView(final View view, final Bitmap bitmap) {

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                int viewHeight = view.getLayoutParams().height;
                int vuewWidth = view.getLayoutParams().width;


                Bitmap bmOverlay = Bitmap.createBitmap(vuewWidth, viewHeight, Bitmap.Config.ARGB_8888);

                Paint p = new Paint();
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                Canvas c = new Canvas(bmOverlay);
                c.drawBitmap(bitmap, 0, 0, null);
                c.drawRect(30, 30, 100, 100, p);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (android.os.Build.VERSION.SDK_INT >= 16){
                            setBackgroundV16Plus(view, bitmap);
                        }
                        else{
                            setBackgroundV16Minus(view, bitmap);
                        }
                    }
                });
            }
        });


    }

    @TargetApi(16)
    private void setBackgroundV16Plus(View view, Bitmap bitmap) {
        view.setBackground(new BitmapDrawable(activity.getResources(), bitmap));

    }

    @SuppressWarnings("deprecation")
    private void setBackgroundV16Minus(View view, Bitmap bitmap) {
        view.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    ExecutorService executorService = Executors.newCachedThreadPool();

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
}
