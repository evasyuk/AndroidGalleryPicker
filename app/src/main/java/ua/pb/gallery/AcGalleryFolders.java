package ua.pb.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ua.pb.gallery.adapters.GalleryFoldersRecyclerAdapter;
import ua.pb.gallery.models.FolderEntity;

/**
 * Created by user on 15.08.15.
 */
public class AcGalleryFolders extends Activity {

    public static final String FOLDERS_KEY = "FOLDERS_KEY_";

    private RecyclerView foldersRecyclerView;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        activity = this;

        Intent receivedIntent = getIntent();
        if (receivedIntent == null) {
            showDialogAndDismiss("receivedIntent == null");
            return;
        }

        if (receivedIntent.getExtras() == null) {
            showDialogAndDismiss("receivedIntent.getExtras() == null");
            return;
        } else {
            Bundle bundle = receivedIntent.getExtras();

            ArrayList<String> foldersList = bundle.getStringArrayList(FOLDERS_KEY);
            if (foldersList == null) {
                showDialogAndDismiss("bundle.getStringArrayList(FOLDERS_KEY) == null");
            } else {
                ArrayList<FolderEntity> result = new ArrayList<>(foldersList.size());
                for (String fodler : foldersList) {
                    File file = new File(fodler);

                    FolderEntity entity = new FolderEntity(fodler, file);
                    result.add(entity);
                }

                setupLayout(result);
            }
        }
    }

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

    private void startAcGalleryPhoto(String folderPath) {
        Intent startAcGalleryPhoto = new Intent(AcGalleryFolders.this, AcGalleryPhotos.class);

        startAcGalleryPhoto.putExtra(Utils.PHOTO_KEY, folderPath);

        startActivityForResult(startAcGalleryPhoto, Utils.REQUEST_PICK_IMAGE);
    }

    private void setupLayout(final ArrayList<FolderEntity> list) {
        setContentView(R.layout.gallery_recycler_layout);
        foldersRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        final GalleryFoldersRecyclerAdapter adapter = new GalleryFoldersRecyclerAdapter(new GalleryFoldersRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(activity, "position=" + position, Toast.LENGTH_SHORT).show();
                startAcGalleryPhoto(list.get(position).getFolderFullPath());
            }
        }, list, this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int rowNumber = (int)dpHeight / (displayMetrics.density <= 1.5f ? 200 : 400 );

        foldersRecyclerView.setAdapter(adapter);
        foldersRecyclerView.setLayoutManager(new GridLayoutManager(activity, rowNumber, LinearLayoutManager.HORIZONTAL, false));

    }

    private void showDialogAndDismiss(String reason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(reason);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.create().show();
    }
}
