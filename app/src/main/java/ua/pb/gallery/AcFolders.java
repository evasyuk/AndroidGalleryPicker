package ua.pb.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ua.pb.gallery.adapters.GenericAdapter;
import ua.pb.gallery.models.FileItemModel;
import ua.pb.gallery.models.FolderEntity;

/**
 * Created by user on 29.08.15.
 */
public class AcFolders extends AcGalleryBasic {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AcGalleryPhotos", "requestCode " + requestCode + "; resultCode " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Utils.RESULT_PICK_IMAGE_OK) {
            setResult(Utils.RESULT_PICK_IMAGE_OK, data);
            finish();
        } else if (resultCode == Utils.RESULT_PICK_IMAGE_CANCALLED) {
            Toast.makeText(activity, "Choose folder again", Toast.LENGTH_SHORT).show();
        }
    }
}
