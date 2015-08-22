package ua.pb.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import ua.pb.gallery.adapters.GalleryPhotosRecyclerAdapter;
import ua.pb.gallery.adapters.GridLayoutManager;
import ua.pb.gallery.models.PhotoEntity;

/**
 * Created by user on 15.08.15.
 */
public class AcGalleryPhotos extends Activity {



    private Activity activity;

    private RecyclerView photosRecyclerView;
    private GalleryPhotosRecyclerAdapter adapter;

    private GridLayoutManager gridLayoutManager;

    private ArrayList<PhotoEntity> photos;

    private FilenameFilter picturesfilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            if (filename.contains("jpeg") || filename.contains("jpg") || filename.contains("png") ) {
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent()!= null) {
            if (getIntent().getExtras() != null) {
                String folderName = getIntent().getExtras().getString(Utils.PHOTO_KEY);
                if (folderName != null) {
                    setupAndShowContent(folderName);
                } else {
                    finishWithShame("folderName == null");
                }
            } else {
                finishWithShame("getIntent().getExtras() == null");
            }
        } else {
            finishWithShame("getIntent() == null");
        }
    }

    private void finishWithShame(String reason) {
        Log.e(AcGalleryPhotos.class.getSimpleName(), reason);
    }

    private void setupAndShowContent(String folderPath) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        activity = this;

        setContentView(R.layout.gallery_photo_viewer_layer);
        photosRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int rowNumber = (int)dpHeight / (displayMetrics.density <= 1.5f ? 200 : 400 );

        photosRecyclerView.setLayoutManager(gridLayoutManager = new GridLayoutManager(activity, rowNumber, LinearLayoutManager.HORIZONTAL));


        adapter = new GalleryPhotosRecyclerAdapter(new GalleryPhotosRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                imageChosen(photos.get(position).getPhotoFileFullPath());
            }
        }, photos = getPhotos(folderPath), activity);

        photosRecyclerView.setAdapter(adapter);

        //todo: make adapter
    }

    private ArrayList<PhotoEntity> getPhotos(String fodlerPath) {
        File folder = new File(fodlerPath);
        File[] photos = folder.listFiles(picturesfilter);

        ArrayList<PhotoEntity> result = new ArrayList<>(photos.length);

        for (File photoFile : photos) {
            result.add(new PhotoEntity(photoFile.getAbsolutePath(), photoFile));
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finishActivity(RESULT_CANCELED);
        //or
        setResult(Utils.RESULT_PICK_IMAGE_CANCALLED);
        finish();
    }

    private void imageChosen(String filePath) {
        Intent intent = new Intent();
        intent.putExtra(Utils.PHOTO_RESULT_PATH, filePath);
        setResult(Utils.RESULT_PICK_IMAGE_OK, intent);
        finish();
    }
}
 

