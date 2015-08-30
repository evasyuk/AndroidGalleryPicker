package ua.pb.gallery;

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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
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
import ua.pb.gallery.adapters.GenericAdapter;
import ua.pb.gallery.models.FileItemModel;
import ua.pb.gallery.models.PhotoEntity;

/**
 * Created by user on 15.08.15.
 */
@Deprecated
public class AcGalleryPhotos extends Activity {



    private Activity activity;

    private RecyclerView photosRecyclerView;
    private GenericAdapter adapter;

    private GridLayoutManager gridLayoutManager;

    private ArrayList<FileItemModel> photos;

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

        setContentView(R.layout.gallery_recycler_layout);
        photosRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        gridLayoutManager = new GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setSmoothScrollbarEnabled(true);

        adapter = new GenericAdapter(false, new GenericAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, FileItemModel model) {
                imageChosen(model.getFileFullPath());
            }
        }, photos = getPhotos(folderPath), activity);

        photosRecyclerView.setAdapter(adapter);
        photosRecyclerView.setLayoutManager(gridLayoutManager);

    }

    private ArrayList<FileItemModel> getPhotos(String fodlerPath) {
        File folder = new File(fodlerPath);
        File[] photos = folder.listFiles(picturesfilter);

        ArrayList<FileItemModel> result = new ArrayList<>(photos.length);

        for (File photoFile : photos) {
            result.add(new FileItemModel(photoFile.getAbsolutePath(), photoFile));
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
 

