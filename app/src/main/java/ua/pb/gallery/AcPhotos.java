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

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by user on 29.08.15.
 */
public class AcPhotos extends AcGalleryBasic {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("AcGalleryPhotos", "requestCode " + requestCode + "; resultCode " + requestCode);
       // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Utils.RESULT_PICK_IMAGE_OK) {
             setResult(Utils.RESULT_PICK_IMAGE_OK, data);
             finish();
        } else if (resultCode == Utils.RESULT_PICK_IMAGE_CANCALLED) {
            Toast.makeText(activity, "Choose folder again", Toast.LENGTH_SHORT).show();
        }
    }

}
