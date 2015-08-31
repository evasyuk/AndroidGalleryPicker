package ua.pb.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ua.pb.gallery.adapters.GenericAdapter;
import ua.pb.gallery.models.FileItemModel;
import ua.pb.gallery.widget.AnimatingRelativeLayout;

/**
 * Created by user on 30.08.15.
 */
public class AcPreview extends Activity {

    public static final String FOLDERS_KEY = "FOLDERS_KEY_";

    public static final String CHOSEN_PHOTO = "CHOSEN_PHOTO";

    ArrayList<String> photosString;

    ImagePagerAdapter imageAdapter;

    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

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

            ArrayList<String> foldersList = photosString = bundle.getStringArrayList(FOLDERS_KEY);
            int chosenPhoto = bundle.getInt(CHOSEN_PHOTO);

            if (foldersList == null) {
                showDialogAndDismiss("bundle.getStringArrayList(FOLDERS_KEY) == null");
            } else {
                ArrayList<FileItemModel> result = new ArrayList<>(foldersList.size());
                for (String fodler : foldersList) {
                    File file = new File(fodler);

                    FileItemModel entity = new FileItemModel(fodler, file);
                    result.add(entity);
                }

                setupContent(result, chosenPhoto);
            }

        }

    }

    private void showDialogAndDismiss(String info) {
        android.util.Log.e("AcPreview", info);
    }

    private void setupContent(List<FileItemModel> photosList, int chosenOne) {
        setContentView(R.layout.preview_activity);

        header = (AnimatingRelativeLayout) findViewById(R.id.header);
        backButtonWrapper = (RelativeLayout) findViewById(R.id.back_button_wrapper);
        textTitle = (TextView) findViewById(R.id.textView4);

        headerOverlayCancel = (RelativeLayout) findViewById(R.id.header_overlay_left);
        headerOverlayConfirm = (RelativeLayout) findViewById(R.id.header_overlay_right);

        backButtonWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        headerOverlayCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        headerOverlayConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAndReturn();
            }
        });

        textTitle.setText("PhotoPreview");

        ViewPager viewPager = this.viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = imageAdapter = new ImagePagerAdapter(this, photosList);
        viewPager.setAdapter(adapter);

//        viewPager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isHeaderHidden) {
//                    header.show(true);
//                    isHeaderHidden = !isHeaderHidden;
//                } else {
//                    header.hide(true);
//                    isHeaderHidden = !isHeaderHidden;
//                }
//            }
//        });

        viewPager.setCurrentItem(chosenOne);
    }

    private void confirmAndReturn() {
        int currentPhotoIndex = viewPager.getCurrentItem();
        String result = photosString.get(currentPhotoIndex);

        Intent intent = new Intent(AcPreview.this, AcPhotos.class);
        intent.putExtra(Utils.PHOTO_RESULT_PATH, result);

        Bundle bundle = new Bundle();
        bundle.putString(Utils.PHOTO_RESULT_PATH, result);
        intent.putExtras(bundle);

        if (Utils.BUG_WITH_ON_ACTIVITY_RESULT) {
            startActivity(intent);
            finish();
        }

        setResult(Utils.RESULT_PICK_IMAGE_OK, intent);
        finish();
    }

    AnimatingRelativeLayout header;
    RelativeLayout backButtonWrapper;
    TextView textTitle;
    boolean isHeaderHidden;

    RelativeLayout headerOverlayCancel;
    RelativeLayout headerOverlayConfirm;

    private class ImagePagerAdapter extends PagerAdapter {

        Activity activity;

        List<FileItemModel> list;

        ImagePagerAdapter (Activity activity, List<FileItemModel> list) {
            this.list = list;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
            //return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = activity;

            View root = LayoutInflater.from(context).inflate(R.layout.preview_view_pager_layout, container, false);

            ImageView imageView = (ImageView) root.findViewById(R.id.imageView6);

//            ImageView imageView = new ImageView(context);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            loadImage(Utils.FILE + list.get(position).getFileFullPath(), imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.e("ON TOUCH", "touch intercepted");
                    if (isHeaderHidden) {
                        header.show(true);
                        isHeaderHidden = !isHeaderHidden;
                    } else {
                        header.hide(true);
                        isHeaderHidden = !isHeaderHidden;
                    }
                }
            });

            //((ViewPager) container).addView(imageView, 0);
            ((ViewPager) container).addView(root);

            return root;
        }

        private void loadImage(String what, final ImageView where) {
            ImageSize cuImageSize = new ImageSize(500, 500);

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

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
    }
}