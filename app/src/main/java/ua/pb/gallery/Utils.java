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

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Created by user on 17.08.15.
 */
public class Utils {


    public static boolean BUG_WITH_ON_ACTIVITY_RESULT = false;


    public static final String FOLDERS_KEY = "FOLDERS_KEY_";

    public static final String IS_FOLDER_MODE_KEY = "IS_FOLDER_MODE_KEY";

    public static final String FILE = "file://";
    public static final String DRAWABLE = "drawable://";
    public static final String ASSETS = "assets://";
    public static final String WEB = "http://";

    public static final int REQUEST_PICK_IMAGE = 1000;
    public static final int REQUEST_PICK_FOLDER = 1003;
    public static final int RESULT_PICK_IMAGE_OK = 1001;
    public static final int RESULT_PICK_IMAGE_CANCALLED = 1002;
    public static final int RESULT_PICK_FOLDER_OK = 1004;
    public static final int RESULT_PICK_FODLER_CANCELLED = 1005;

    public static final String PHOTO_KEY = "PHOTO_KEY";
    public static final String PHOTO_RESULT_PATH = "PHOTO_RESULT_PATH";


    public static ImageLoader IMAGE_LOADER = ImageLoader.getInstance();

    // Universal Image Loader
    // See the sample project how to use ImageLoader correctly.
    public static DisplayImageOptions IMAGE_LOADER_OPTIONS = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_placeholder) // resource or drawable
            .showImageOnFail(R.drawable.ic_launcher) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .displayer(new SimpleBitmapDisplayer()) // default
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

}
