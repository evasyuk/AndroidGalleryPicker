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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import ua.pb.gallery.adapters.FilterInterface;
import ua.pb.gallery.adapters.GenericAdapter;
import ua.pb.gallery.models.FileItemModel;

/**
 * Created by user on 29.08.15.
 */
public abstract class AcGalleryBasic extends Activity {


    private RecyclerView foldersRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private GenericAdapter recyclerAdapter;
    private FilterInterface filterInterface;

    private ArrayList<FileItemModel> list;

    protected Activity activity;

    private boolean isFolderMode;

    private GenericAdapter.OnItemClickListener onItemClickListener;

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

        if (Utils.BUG_WITH_ON_ACTIVITY_RESULT) {
            if (getIntent() != null) {
                String result = getIntent().getStringExtra(Utils.PHOTO_RESULT_PATH);
                if (result == null) {
                    android.util.Log.e("INTENT BUG", "result == null, but hasn't to");
                    if (getIntent().getExtras() == null) {
                        android.util.Log.e("INTENT BUG", "bitch! there is no extras! but has to be!");
                    } else {
                        String result2 = getIntent().getExtras().getString(Utils.PHOTO_RESULT_PATH);
                        if (result2 != null) {
                            android.util.Log.e("INTENT BUG", "OK, path: " + result2);
                            setResult(Utils.RESULT_PICK_IMAGE_OK, getIntent());
                            finish();
                        } else {
                            android.util.Log.e("INTENT BUG", "resulted String s NULL - thi is an ERROR");
                        }
                    }
                } else {
                    setResult(Utils.RESULT_PICK_IMAGE_OK, getIntent());
                    finish();
                }
            } else {
                android.util.Log.e("INTENT BUG","intetn is NULL, but hasn't to");
            }
        }

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

            boolean isFolderMode = bundle.getBoolean(Utils.IS_FOLDER_MODE_KEY, true);

            if (isFolderMode) {
                ArrayList<String> foldersList = bundle.getStringArrayList(Utils.FOLDERS_KEY);
                if (foldersList == null) {
                    showDialogAndDismiss("bundle.getStringArrayList(FOLDERS_KEY) == null");
                } else {
                    ArrayList<FileItemModel> result = new ArrayList<>(foldersList.size());
                    for (String fodler : foldersList) {
                        File file = new File(fodler);

                        FileItemModel entity = new FileItemModel(fodler, file);
                        result.add(entity);
                    }

                    setupActivity(true, result, new GenericAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, FileItemModel model) {
                            startAcGalleryPhoto(model.getFileFullPath());
                        }
                    });
                }
            } else {
                String folderName = getIntent().getExtras().getString(Utils.PHOTO_KEY);
                if (folderName != null) {
                    final ArrayList<FileItemModel> result = getPhotos(folderName);

                    final ArrayList<String> photos2 = new ArrayList<>(result.size());

                    for (FileItemModel file : result) {
                        photos2.add(file.getFileFullPath());
                    }

                    setupActivity(false, result, new GenericAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, FileItemModel model) {
                            //imageChosen(model.getFileFullPath());

                            int chosenPhoto = 0;

                            for (int index = 0; index < result.size(); index++) {
                                FileItemModel temp = result.get(index);
                                if (temp.getFileFullPath().equals(model.getFileFullPath())) {
                                    chosenPhoto = index;
                                    break;
                                }
                            }

                            startAcPreview(photos2, chosenPhoto);
                        }
                    });
                } else {
                    showDialogAndDismiss("folderName == null");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Runtime.getRuntime().gc();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finishActivity(RESULT_CANCELED);
        //or

        if (!isFolderMode) {
            setResult(Utils.RESULT_PICK_IMAGE_CANCALLED);
            finish();
        }
    }

    // TODO: FIX BUG
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        android.util.Log.e("INTENT_BUG","intent.getStringExtra(Utils.PHOTO_RESULT_PATH) is " + (intent.getStringExtra(Utils.PHOTO_RESULT_PATH) == null ? "null" : "not null"));

        if (intent != null) {
            Log.d("StarterActivity", "chosen photo path: " + intent.getStringExtra(Utils.PHOTO_RESULT_PATH));
            Toast.makeText(this, "chosen photo path: " + intent.getStringExtra(Utils.PHOTO_RESULT_PATH), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("StarterActivity", "requestCode " + requestCode + "; resultCode " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Utils.RESULT_PICK_IMAGE_OK) {
            Log.d("StarterActivity", "chosen photo path: " + data.getStringExtra(Utils.PHOTO_RESULT_PATH) );
            Toast.makeText(this, "chosen photo path: " + data.getStringExtra(Utils.PHOTO_RESULT_PATH), Toast.LENGTH_LONG).show();
            finish();
        } else {
            //Toast.makeText(this, "result is shit", Toast.LENGTH_LONG).show();
        }
    }

    private void imageChosen(String filePath) {
        Intent intent = new Intent();
        intent.putExtra(Utils.PHOTO_RESULT_PATH, filePath);
        setResult(Utils.RESULT_PICK_IMAGE_OK, intent);
        finish();
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

    private void startAcPreview(ArrayList<String> photos, int chosenPhoto) {
        Intent intent = new Intent(AcGalleryBasic.this, AcPreview.class);

        intent.putExtra(AcPreview.CHOSEN_PHOTO, chosenPhoto);
        intent.putExtra(Utils.FOLDERS_KEY, photos);
        //setResult(Utils.RESULT_PICK_IMAGE_OK, intent);
        startActivityForResult(intent, Utils.RESULT_PICK_IMAGE_OK);

    }
    private void startAcGalleryPhoto(String folderPath) {
        Intent startAcGalleryPhoto = new Intent(this, AcPhotos.class);

        startAcGalleryPhoto.putExtra(Utils.PHOTO_KEY, folderPath);
        startAcGalleryPhoto.putExtra(Utils.IS_FOLDER_MODE_KEY, false);

        startActivityForResult(startAcGalleryPhoto, Utils.REQUEST_PICK_IMAGE);
    }

    // NOTE: don't forget to setup Activity before start using it
    protected void setupActivity(boolean isFolderMode,
                              ArrayList<FileItemModel> list,
                              GenericAdapter.OnItemClickListener onItemClickListener) {
        this.isFolderMode = isFolderMode;
        this.list = list;
        this.onItemClickListener = onItemClickListener;

        setupLayout();
    }
    //---------------------------------------------------------------------------------------------/
    private void setupLayout( ) {
        setContentView(R.layout.gallery_recycler_layout);
        initActionBar();
        initFloatActionButton();
        initSearchField();
        initGridChanging();
        setupSpinner();
        foldersRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        createRecyclerView(list, true);
    }

    private void createRecyclerView (final ArrayList<FileItemModel> list, boolean isDualSpan) {
        recyclerAdapter = new GenericAdapter(isFolderMode, onItemClickListener, list, this);

        android.util.Log.e("BITMAP PROBLEM", "createRecyclerView, isDualSpan: " + isDualSpan);

        recyclerAdapter.changeLayoutSpanType(!isDualSpan);

        filterInterface = recyclerAdapter;

        gridLayoutManager = new GridLayoutManager(activity, isDualSpan ? 2 : 4, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setSmoothScrollbarEnabled(true);


        foldersRecyclerView.setAdapter(recyclerAdapter);
        foldersRecyclerView.setLayoutManager(gridLayoutManager);

        foldersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CURRENT_ACTION_BAR_HEIGHT += dy;


                if (CURRENT_ACTION_BAR_HEIGHT < 0) {
                    CURRENT_ACTION_BAR_HEIGHT = 0;
                }

                if (CURRENT_ACTION_BAR_HEIGHT > MAX_ACTION_BAR_HEIGHT) {
                    CURRENT_ACTION_BAR_HEIGHT = MAX_ACTION_BAR_HEIGHT;
                }

                applyNewMargin(CURRENT_ACTION_BAR_HEIGHT);
            }
        });
        foldersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CURRENT_FAB_HEIGHT += dy;


                if (CURRENT_FAB_HEIGHT < 0) {
                    CURRENT_FAB_HEIGHT = 0;

                    //disable search
                    //needToShowSearchField = false;
                    //searchFilterField.setVisibility(View.GONE);
                }

                if (CURRENT_FAB_HEIGHT > MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN) {
                    CURRENT_FAB_HEIGHT = MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN;
                }

                applyNewFABHeight(CURRENT_FAB_HEIGHT);

                applySearchFieldAlpha(1 - (float) CURRENT_FAB_HEIGHT / (float) (MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN));
            }
        });

        foldersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }
    /**********************************************************************************************/
    /**********************************************************************************************/


    //---------------------------------------------------------------------------------------------/
    private LinearLayout actionBar;
    private TextView actionBarTitle;
    private RelativeLayout backButtonWrapper;
    private int CURRENT_ACTION_BAR_HEIGHT;
    private int MAX_ACTION_BAR_HEIGHT;
    private void applyNewMargin(int height) {
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)actionBar.getLayoutParams();
        lParams.setMargins(0, -height, 0, 0);

        actionBar.setLayoutParams(lParams);
    }
    private void initActionBar() {
        actionBar = (LinearLayout) findViewById(R.id.header_tool);

        actionBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MAX_ACTION_BAR_HEIGHT = actionBar.getHeight();
                if (MAX_ACTION_BAR_HEIGHT == 0) {
                    MAX_ACTION_BAR_HEIGHT = actionBar.getMeasuredHeight();
                }

                if (Build.VERSION.SDK_INT <= 15)
                    actionBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    actionBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        actionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
        actionBarTitle.setText(isFolderMode ? "Folders" : "Photos");

        backButtonWrapper = (RelativeLayout) findViewById(R.id.ivBackWrapper);
        backButtonWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    //---------------------------------------------------------------------------------------------/
    private ImageView changeGrid;
    boolean isDualSpan = true;
    private void initGridChanging() {
        changeGrid = (ImageView) findViewById(R.id.changeGridImageView);
        changeGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDualSpan = !isDualSpan;

                if (isDualSpan){
                    changeGrid.setImageResource(R.mipmap.ic_tile33);
                } else {
                    changeGrid.setImageResource(R.mipmap.ic_tile22);
                }

                createRecyclerView(list, isDualSpan);
            }
        });
    }

    protected void showDialogAndDismiss(String reason) {
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

    private Spinner spinner;
    private int currentlyChosen;
    private void setupSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Time");
        list.add("Alphabetic");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == currentlyChosen)
                    return;
                if (position == 1) {
                    recyclerAdapter.sortAlphabeticAscending();
                } else if (position == 0) {
                    recyclerAdapter.sortTimeAsceniding();
                }
                currentlyChosen = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {/**/}
        });
    }
    /**********************************************************************************************/
    /**********************************************************************************************/

    //---------------------------------------------------------------------------------------------/
    private ImageView floatActionButton;
    private int CURRENT_FAB_HEIGHT;
    private int MAX_FAB_HEIGHT;
    private int FAB_BOTTOM_MARGIN = 15;
    private int FAB_RIGHT_MARGIN = 15;
    private boolean isFloatActionButtonGreen;
    private void initFloatActionButton() {
        floatActionButton = (ImageView) findViewById(R.id.floatActionButton);

        floatActionButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MAX_FAB_HEIGHT = floatActionButton.getHeight();
                if (MAX_FAB_HEIGHT == 0) {
                    MAX_FAB_HEIGHT = floatActionButton.getMeasuredHeight();
                }

                if (Build.VERSION.SDK_INT <= 15)
                    floatActionButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    floatActionButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        floatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needToShowSearchField)
                    searchFilterField.setVisibility(View.VISIBLE);
                else
                    searchFilterField.setVisibility(View.GONE);

                needToShowSearchField = !needToShowSearchField;
            }
        });
    }

    private void applyNewFABHeight(int height) {
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)floatActionButton.getLayoutParams();
        lParams.setMargins(0, 0, FAB_RIGHT_MARGIN, -height);

        floatActionButton.setLayoutParams(lParams);
    }
    /**********************************************************************************************/
    /**********************************************************************************************/

    //---------------------------------------------------------------------------------------------/
    private RelativeLayout searchFilterField;
    private EditText searchPhotoEditText;
    private boolean needToShowSearchField = true;// do I "need to show search field on the next click"? - yes(by default)
    private void initSearchField() {
        searchFilterField = (RelativeLayout) findViewById(R.id.searchFilterField);
        searchPhotoEditText = (EditText) findViewById(R.id.editText);
        //todo: add on inputListener
        searchPhotoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/**/}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    if (!isFloatActionButtonGreen) {
                        isFloatActionButtonGreen = true;
                        floatActionButton.setImageResource(R.mipmap.ic_search_green_theme);
                    }
                } else {
                    if (isFloatActionButtonGreen) {
                        isFloatActionButtonGreen = false;
                        floatActionButton.setImageResource(R.mipmap.ic_search_theme);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterInterface.applyFilter(s.toString());
            }
        });
    }
    private void enableSearchFilterSection() {
        needToShowSearchField = true;
        searchFilterField.setVisibility(View.VISIBLE);
    }

    private void applySearchFieldAlpha(float alpha) {
        searchFilterField.setAlpha(alpha);
    }
    /**********************************************************************************************/
    /**********************************************************************************************/


}
