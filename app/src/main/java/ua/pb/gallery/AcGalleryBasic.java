package ua.pb.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
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
import ua.pb.gallery.adapters.GalleryFoldersRecyclerAdapter;
import ua.pb.gallery.adapters.GenericAdapter;
import ua.pb.gallery.models.FileItemModel;
import ua.pb.gallery.models.FolderEntity;

/**
 * Created by user on 29.08.15.
 */
public abstract class AcGalleryBasic extends Activity {

    public static final String FOLDERS_KEY = "FOLDERS_KEY_";

    public static final String IS_FOLDER_MODE_KEY = "IS_FOLDER_MODE_KEY";

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

            boolean isFolderMode = bundle.getBoolean(IS_FOLDER_MODE_KEY, true);

            if (isFolderMode) {
                ArrayList<String> foldersList = bundle.getStringArrayList(FOLDERS_KEY);
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
                    ArrayList<FileItemModel> result = getPhotos(folderName);

                    setupActivity(false, result, new GenericAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, FileItemModel model) {
                            imageChosen(model.getFileFullPath());
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

    private void startAcGalleryPhoto(String folderPath) {
        Intent startAcGalleryPhoto = new Intent(AcGalleryBasic.this, AcPhotos.class);

        startAcGalleryPhoto.putExtra(Utils.PHOTO_KEY, folderPath);
        startAcGalleryPhoto.putExtra(IS_FOLDER_MODE_KEY, false);

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

                applySearchFieldAlpha( 1 - (float)CURRENT_FAB_HEIGHT/(float)(MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN));
            }
        });
    }
    /**********************************************************************************************/
    /**********************************************************************************************/


    //---------------------------------------------------------------------------------------------/
    private LinearLayout actionBar;
    private TextView actionBarTitle;
    private ImageView backButton;
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

                actionBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        actionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
        actionBarTitle.setText(isFolderMode ? "Folders" : "Photos");

        backButton = (ImageView) findViewById(R.id.ivBack);
        backButton.setOnClickListener(new View.OnClickListener() {
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
        list.add("Alphabetic");
        list.add("Time");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == currentlyChosen)
                    return;
                if (position == 0) {
                    recyclerAdapter.sortAlphabeticAscending();
                } else if (position == 1) {
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
                        floatActionButton.setImageResource(R.mipmap.ic_search_green);
                    }
                } else {
                    if (isFloatActionButtonGreen) {
                        isFloatActionButtonGreen = false;
                        floatActionButton.setImageResource(R.mipmap.ic_search_white);
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
