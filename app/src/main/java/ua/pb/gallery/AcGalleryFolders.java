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
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ua.pb.gallery.adapters.FilterInterface;
import ua.pb.gallery.adapters.GalleryFoldersRecyclerAdapter;
import ua.pb.gallery.models.FolderEntity;

/**
 * Created by user on 15.08.15.
 */
public class AcGalleryFolders extends Activity {

    public static final String FOLDERS_KEY = "FOLDERS_KEY_";

    private RecyclerView foldersRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private GalleryFoldersRecyclerAdapter recyclerAdapter;
    private FilterInterface filterInterface;

    private ArrayList<FolderEntity> list;

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

    //---------------------------------------------------------------------------------------------/
    private void setupLayout(final ArrayList<FolderEntity> list) {
        setContentView(R.layout.gallery_recycler_layout);
        initActionBar();
        initFloatActionButton();
        initSearchField();
        initGridChanging();
        setupSpinner();
        foldersRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        this.list = list;

        createRecyclerView(list, true);

    }

    private void createRecyclerView (final ArrayList<FolderEntity> list, boolean isDualSpan) {
        recyclerAdapter = new GalleryFoldersRecyclerAdapter(new GalleryFoldersRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(activity, "position=" + position, Toast.LENGTH_SHORT).show();
                startAcGalleryPhoto(list.get(position).getFolderFullPath());
            }
        }, list, this);

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

                android.util.Log.e("ALPHA", "CURRENT_FAB_HEIGHT=" + CURRENT_FAB_HEIGHT +
                        " MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN = " + (MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN) +
                        " alpha=" + (CURRENT_FAB_HEIGHT / (MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN)));
                applySearchFieldAlpha( 1 - (float)CURRENT_FAB_HEIGHT/(float)(MAX_FAB_HEIGHT + FAB_BOTTOM_MARGIN));
            }
        });
    }
    /**********************************************************************************************/
    /**********************************************************************************************/

    //---------------------------------------------------------------------------------------------/
    LinearLayout actionBar;
    int CURRENT_ACTION_BAR_HEIGHT;
    int MAX_ACTION_BAR_HEIGHT;
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
    }
    //---------------------------------------------------------------------------------------------/
    ImageView changeGrid;
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

    Spinner spinner;
    int currentlyChosen;
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
    ImageView floatActionButton;
    int CURRENT_FAB_HEIGHT;
    int MAX_FAB_HEIGHT;
    int FAB_BOTTOM_MARGIN = 15;
    int FAB_RIGHT_MARGIN = 15;
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
    RelativeLayout searchFilterField;
    EditText searchPhoto;
    boolean needToShowSearchField;
    private void initSearchField() {
        searchFilterField = (RelativeLayout) findViewById(R.id.searchFilterField);
        searchPhoto = (EditText) findViewById(R.id.editText);
        //todo: add on inputListener
        searchPhoto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/**/}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {/**/}

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
