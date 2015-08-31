package ua.pb.gallery.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ua.pb.gallery.R;
import ua.pb.gallery.Utils;
import ua.pb.gallery.models.FileItemModel;

/**

 Copyright 2015 evasyuk  < vasyuk.eugene@gmail.com >

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

/**
 * Created by user on 29.08.15.
 */
public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.FileItemHolderView> implements FilterInterface{

    boolean isFolderMode;

    private static final int IMAGE_SIZE_DUAL = 100;
    private static final int IAMGE_SIZE_QUADRO = 250;

    private Activity activity;

    private ArrayList<FileItemModel> originList;
    private ArrayList<FileItemModel> filteredList;

    private OnItemClickListener onItemClickedCallback;

    private boolean isDualSpan;

    private ImageSize imageSizeDualTile = new ImageSize(IAMGE_SIZE_QUADRO, IAMGE_SIZE_QUADRO);
    private ImageSize imageSizeQuadroTile = new ImageSize(IMAGE_SIZE_DUAL, IMAGE_SIZE_DUAL);


    private static Comparator<FileItemModel> ALPHABETICAL_ORDER = new Comparator<FileItemModel>() {
        public int compare(FileItemModel str1, FileItemModel str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getFile().getName(), str2.getFile().getName());
            if (res == 0) {
                res = str1.getFile().getName().compareTo(str2.getFile().getName());
            }
            return res;
        }
    };

    private static Comparator<FileItemModel> TIME_ORDER = new Comparator<FileItemModel>() {
        public int compare(FileItemModel str1, FileItemModel str2) {

            long res = str1.getFile().lastModified() - str2.getFile().lastModified();

            return (int)res;
        }
    };

    public void changeLayoutSpanType(boolean isDualSpan) {
        this.isDualSpan = isDualSpan;
    }

    public void sortAlphabeticAscending() {
        Collections.sort(filteredList, ALPHABETICAL_ORDER);
        notifyDataSetChanged();
    }

    public void sortTimeAsceniding() {
        Collections.sort(filteredList, TIME_ORDER);
        notifyDataSetChanged();
    }

    public GenericAdapter(boolean isFolderMode, OnItemClickListener onItemClickedCallback, ArrayList<FileItemModel> originList, Activity activity) {
        this.onItemClickedCallback = onItemClickedCallback;
        this.originList = originList;
        this.filteredList = originList;
        this.activity = activity;
        this.isFolderMode = isFolderMode;

        sortTimeAsceniding();
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
    public FileItemHolderView onCreateViewHolder(ViewGroup viewGroup, final int i) {
        Log.d("Recycler", "onCreateViewHolder(ViewGroup, int) index = " + i);
        View view;
        if (isDualSpan)
            view = LayoutInflater.from(activity).inflate(R.layout.gallery_folder_item_05x, viewGroup, false);
        else
            view = LayoutInflater.from(activity).inflate(R.layout.gallery_folder_item_1x, viewGroup, false);

        return new FileItemHolderView(view);
    }

    @Override
    public int getItemCount() {
        Log.d("Recycler", "getItemCount() count = " + originList.size());
        return filteredList.size();
    }

    @Override
    public void onBindViewHolder(final FileItemHolderView fileItemHolderView, int i) {
        Log.d("Recycler", "onBindViewHolder(FileItemHolderView, int) index = " + i);

        FileItemModel entity = filteredList.get(i);

        fileItemHolderView.folderName.setText(entity.getFile().getName());

        if (isFolderMode) {
            File[] innerPhotos = entity.getFile().listFiles(picturesfilter);
            fileItemHolderView.photosCount.setText("" + innerPhotos.length);

            if (innerPhotos.length > 0)
                loadImage(Utils.FILE + findLastOne(innerPhotos).getAbsolutePath(), fileItemHolderView.folderCover);
            else
                loadImage(Utils.DRAWABLE + R.drawable.ic_launcher, fileItemHolderView.folderCover);

        } else {
            fileItemHolderView.photoCountHolder.setVisibility(View.GONE);

            loadImage(Utils.FILE + entity.getFile().getAbsolutePath(), fileItemHolderView.folderCover);
        }
    }

    private void loadImage(String what, final ImageView where) {
        ImageSize cuImageSize = !isDualSpan ? imageSizeDualTile : imageSizeQuadroTile;

        android.util.Log.e("BITMAP PROBLEM", "cuImageSize: " + cuImageSize.toString());

        Utils.IMAGE_LOADER.loadImage(what, cuImageSize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                android.util.Log.e("BITMAP PROBLEM", "onLoadingStarted, String s: " + s);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                android.util.Log.e("BITMAP PROBLEM", "onLoadingFailed, FailReason: " + failReason);
            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                android.util.Log.e("BITMAP PROBLEM", "onLoadingComplete, String s: " + s);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        where.setImageBitmap(bitmap);
                    }
                });

            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                android.util.Log.e("BITMAP PROBLEM", "onLoadingCancelled, String s: " + s);
            }
        });
    }

    private File findLastOne(File[] list) {
        File temp = list[0];
        for (int index = 1; index < list.length; index++) {
            if (list[index].lastModified() >= temp.lastModified()) {
                temp = list[index];
            }
        }
        return temp;
    }

    class FileItemHolderView extends RecyclerView.ViewHolder implements View.OnClickListener {

        FrameLayout container;

        ImageView folderCover;
        TextView folderName;
        TextView photosCount;

        LinearLayout photoCountHolder;

        public FileItemHolderView(View itemView) {
            super(itemView);

            container = (FrameLayout) itemView.findViewById(R.id.container);

            folderCover = (ImageView) itemView.findViewById(R.id.imageView);
            folderName = (TextView) itemView.findViewById(R.id.textView);
            photosCount = (TextView) itemView.findViewById(R.id.textView3);

            photoCountHolder = (LinearLayout) itemView.findViewById(R.id.temp_id_g354);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickedCallback != null) {
                onItemClickedCallback.onItemClick(v, getAdapterPosition(), filteredList.get(getAdapterPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position, FileItemModel model);
    }

    @Override
    public void applyFilter(String filterKeyWord) {
        ArrayList<FileItemModel> newList = new ArrayList<>();
        for (FileItemModel FileItemModel : originList) {
            String[] temp = FileItemModel.getFileFullPath().split("/");
            String folderName = temp[temp.length - 1];
            if (folderName.toLowerCase().contains(filterKeyWord)) {
                newList.add(FileItemModel);
            }
        }
        filteredList = newList;
        notifyDataSetChanged();
    }
}
