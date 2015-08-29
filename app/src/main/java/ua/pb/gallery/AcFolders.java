package ua.pb.gallery;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


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
