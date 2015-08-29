package ua.pb.gallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import ua.pb.gallery.models.FolderIdem;

public class StarterActivity extends Activity {

    private long tempStartExecutionTime = System.currentTimeMillis();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Universal Image Loader
        // See the sample project how to use ImageLoader correctly.
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_placeholder) // resource or drawable
                .showImageOnFail(R.drawable.ic_launcher) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
        // Universal Image Loader


        ArrayList<String> images =  findImages();

        if (images.size() > 0) {
            startNextAcivity(images);
        } else {
            Toast.makeText(this, "no images were found!", Toast.LENGTH_LONG).show();//todo: replace with dialog
        }
    }

    private ArrayList<String> findImages() {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
        //Stores all the images from the gallery in Cursor
        Cursor cursorExternal = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        Cursor cursorInternal = getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null, orderBy);
        //Total number of images
        int countExternal = cursorExternal.getCount();
        int countInternal = cursorInternal.getCount();

        //Create an array to store path to all the images
        String[] arrPathExternal = new String[countExternal];
        String[] arrPathInternal = new String[countInternal];

        for (int i = 0; i < countExternal; i++) {
            cursorExternal.moveToPosition(i);
            int dataColumnIndex = cursorExternal.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPathExternal[i]= cursorExternal.getString(dataColumnIndex);
            Log.i("PATH", arrPathExternal[i]);
        }

        for (int i = 0; i < countInternal; i++) {
            cursorInternal.moveToPosition(i);
            int dataColumnIndex = cursorInternal.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPathInternal[i]= cursorInternal.getString(dataColumnIndex);
            Log.i("PATH", arrPathInternal[i]);
        }

        Log.i("PATH TIME", "Execution time: " + (System.currentTimeMillis() - tempStartExecutionTime) + "ms\n  "
                + "arrPathExternal.length=" + arrPathExternal.length + "'\n  "
                + "arrPathInternal.length=" + arrPathInternal.length);

        cursorExternal.close();
        cursorInternal.close();

        tempStartExecutionTime = System.currentTimeMillis();
        ArrayList<String> result = sortAndFilterArrayList(arrPathExternal);
        Log.i("PATH SORT TIME", "Filtering time: " + (System.currentTimeMillis() - tempStartExecutionTime) + "ms\n  "
                + "result.length=" + arrPathExternal.length);

        return result;
    }

    private void startNextAcivity(ArrayList<String> list) {
        Intent intent = new Intent(StarterActivity.this, AcFolders.class);
        intent.putStringArrayListExtra(Utils.FOLDERS_KEY, list);
        intent.putExtra(AcGalleryBasic.IS_FOLDER_MODE_KEY, true);
        startActivityForResult(intent, Utils.REQUEST_PICK_FOLDER);
    }

    private ArrayList<FolderIdem> sortByFolders(String[] arrPath) {
        ArrayList<FolderIdem> result = new ArrayList<>();
        for (int index = 0; index < arrPath.length; index++) {
            String[] splitResult = arrPath[index].split("/");

            String folderName = splitResult[splitResult.length - 2];
            //String folderFullPath = arrPath[index].split(folderName)[0];

            StringBuilder stringBuilder = new StringBuilder();
            for (int subindex = 0; subindex <= splitResult.length - 2; subindex++) {
                stringBuilder.append(splitResult[subindex]);
                if (subindex != splitResult.length - 2)
                    stringBuilder.append("/");
            }
            String folderFullPath = stringBuilder.toString();

            FolderIdem folder = new FolderIdem();

            folder.setFolderName(folderName);
            folder.setFullFilePath(folderFullPath);

            result.add(folder);
        }
        return result;
    }

    private TreeSet<String> sortAndFilter(String[] arrPath) {
        TreeSet<String> tree = new TreeSet<>();
        for (int index = 0; index < arrPath.length; index++) {
            String[] splitResult = arrPath[index].split("/");

            //String folderName = splitResult[splitResult.length - 2];
            //String folderFullPath = arrPath[index].split(folderName)[0];

            StringBuilder stringBuilder = new StringBuilder();
            for (int subindex = 0; subindex <= splitResult.length - 2; subindex++) {
                stringBuilder.append(splitResult[subindex]);
                if (subindex != splitResult.length - 2)
                    stringBuilder.append("/");
            }
            String folderFullPath = stringBuilder.toString();

            tree.add(folderFullPath);
        }
        return tree;
    }

    private ArrayList<String> sortAndFilterArrayList(String[] arrPath) {
        ArrayList<String> result = new ArrayList<>();

        TreeSet<String> subResult = sortAndFilter(arrPath);

        Iterator<String> iterator = subResult.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    public String getFilePath(String[] subDirs) {
        StringBuilder sb = new StringBuilder();
        for (int index = 1; index < subDirs.length - 1; index++) {
            sb.append("/");
            sb.append(subDirs[index]);
        }

        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("StarterActivity", "requestCode " + requestCode + "; resultCode " + requestCode );
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Utils.RESULT_PICK_IMAGE_OK) {
            Log.d("StarterActivity", "chosen photo path: " + data.getStringExtra(Utils.PHOTO_RESULT_PATH) );
            Toast.makeText(this, "chosen photo path: " + data.getStringExtra(Utils.PHOTO_RESULT_PATH), Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "result is shit", Toast.LENGTH_LONG).show();
        }
    }
}

