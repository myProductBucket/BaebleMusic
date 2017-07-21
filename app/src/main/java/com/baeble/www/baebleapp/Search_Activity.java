package com.baeble.www.baebleapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.custom.ThemeableMediaRouteActionProvider;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.longtailvideo.jwplayer.cast.CastManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import retrofit.client.Response;

public class Search_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = Search_Activity.class.getSimpleName();
    GridView gridView_search;
    String categoryName;
    public SearchVideoGridAdapter adapter = null;
    static List<FeaturedVideo> searchVideos;
    private Handler mainHandler;
    private boolean mLoading = true;
    private boolean isSucceed = true;
    private ProgressDialog progress;
    private String categoryApiName = "new";
    private boolean isDownloadSucceed = false;

    BaseInputConnection inputConnection;
    ImageView img_crossSearch, img_backSearch, img_searchSearch;
    EditText ed_searchSearch;
    LinearLayout ly_searchMessage;
    TextView txt_searchResultMessage;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    RelativeLayout ly_topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        mainHandler = new Handler(this.getMainLooper());
        progress = new ProgressDialog(this);
        searchVideos = new ArrayList<FeaturedVideo>();

        img_crossSearch = (ImageView) findViewById(R.id.img_crossSearch);
        img_crossSearch.setOnClickListener(this);
        img_backSearch = (ImageView) findViewById(R.id.img_backSearch);
        img_backSearch.setOnClickListener(this);
        img_searchSearch = (ImageView) findViewById(R.id.img_searchSearch);
        img_searchSearch.setOnClickListener(this);
        ed_searchSearch = (EditText) findViewById(R.id.ed_searchSearch);
        inputConnection = new BaseInputConnection(ed_searchSearch, true);
        ly_searchMessage = (LinearLayout) findViewById(R.id.ly_searchMessage);
        txt_searchResultMessage = (TextView)findViewById(R.id.txt_searchResultMessage);

        ed_searchSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    downloadSearchVideo(ed_searchSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Set up the ActionBar
        ly_topBar = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF212221));
        actionBar.setCustomView(ly_topBar);
//
        Toolbar parent =(Toolbar) ly_topBar.getParent();//first get parent toolbar of current action bar
        parent.setContentInsetsAbsolute(0,0);// set padding programmatically to 0dp
        boolean isLandscapeMode = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        gridView_search = (GridView) findViewById(R.id.gridView_search);
        gridView_search.setVisibility(View.GONE);
        gridView_search.setScrollingCacheEnabled(true);
        gridView_search.setNumColumns(isLandscapeMode ? 2 : 1);
        gridView_search.setHorizontalSpacing(isLandscapeMode ? 20 : 0);
        gridView_search.setVerticalSpacing(20);

        gridView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Search_Activity.this, Video_Activity.class);
                intent.putExtra("curPosition", position);
                intent.putExtra("category", "search");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void downloadSearchVideo(String strKeyword){
        categoryName = Main_Tab_Activity.txt_tabCateroryDetail.getText().toString();
        String categoryProgressName = "new";

        if(strKeyword.length() <= 1) {
            ly_searchMessage.setVisibility(View.VISIBLE);
            gridView_search.setVisibility(View.GONE);
            txt_searchResultMessage.setText(getString(R.string.exception_search_result));
            return;
        }

        switch (categoryName){
            case "NEWEST":
                categoryApiName = "new";
                categoryProgressName = getResources().getString(R.string.DownloadingNewestVideo);
                break;
            case "TRENDING":
                categoryApiName = "trend";
                categoryProgressName = getResources().getString(R.string.DownloadingTrendingVideo);
                break;
            case "CONCERTS":
                categoryApiName = "con";
                categoryProgressName = getResources().getString(R.string.DownloadingConcertVideo);
                break;
            case "MUSIC VIDEOS":
                categoryApiName = "mus";
                categoryProgressName = getResources().getString(R.string.DownloadingMusicVideo);
                break;
            case "INTERVIEWS":
                categoryApiName = "int";
                categoryProgressName = getResources().getString(R.string.DownloadingInterviewVideo);
                break;
            default:
                categoryApiName = "new";
                categoryProgressName = getResources().getString(R.string.DownloadingNewestVideo);
                break;
        }

        progress = ProgressDialog.show(this, "Please wait", "Search ...",
                true, false);

        if(searchVideos.size() > 0) {
            searchVideos.clear();
        }

        if(adapter != null)
          adapter.destory();

        SvcApiService.getUserIdEndPoint().GetSearchVideos(strKeyword, "all", new SvcApiRestCallback<List<FeaturedVideo>>() {
            @Override
            public void failure(SvcError svcError) {
                Log.d(TAG, "Failed to download featured video info error: " + svcError.getErrors() );

                adapter = new SearchVideoGridAdapter(Search_Activity.this, searchVideos);
                gridView_search.setAdapter(adapter);

                mLoading = false;
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        ly_searchMessage.setVisibility(View.VISIBLE);
                        gridView_search.setVisibility(View.GONE);
                        txt_searchResultMessage.setText(getString(R.string.no_search_result).replace("\n", System.getProperty("line.separator")));
                    }
                };
                mainHandler.post(progressRunnable);
            }

            @Override
            public void success(List<FeaturedVideo> featuredVideos, Response response) {

                mLoading = false;
                isSucceed = true;

                if (featuredVideos != null) {
                    if (featuredVideos.size() > 0) {
                        Log.d(TAG, "Success to download featured video info = " + featuredVideos.size());
                        for (int videoIndex = 0; videoIndex < featuredVideos.size(); videoIndex++) {
                            if (checkJsonValidity(featuredVideos.get(videoIndex)))
                                searchVideos.add(featuredVideos.get(videoIndex));
                            else
                                isSucceed = false;
                        }
                    } else {
                        isSucceed = false;
                    }
                } else {
                    isSucceed = false;
                }

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        if (isSucceed) {
                            isDownloadSucceed = true;
                            ly_searchMessage.setVisibility(View.GONE);
                            gridView_search.setVisibility(View.VISIBLE);
                            adapter = new SearchVideoGridAdapter(Search_Activity.this, searchVideos);
                            gridView_search.setAdapter(adapter);
                        } else {
                            ly_searchMessage.setVisibility(View.VISIBLE);
                            gridView_search.setVisibility(View.GONE);
                            txt_searchResultMessage.setText(getString(R.string.no_search_result).replace("\n", System.getProperty("line.separator")));
                        }

                    }
                };
                mainHandler.post(progressRunnable);

            }
        });
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_crossSearch:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                break;
            case R.id.img_backSearch:
                finish();
                break;
            case R.id.img_searchSearch:
                ly_searchMessage.setVisibility(View.GONE);
                gridView_search.setVisibility(View.VISIBLE);

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(ed_searchSearch.getWindowToken(), 0);

                Log.d(TAG, "input search keyword: " + ed_searchSearch.getText());
                downloadSearchVideo(ed_searchSearch.getText().toString());
                break;
            case R.id.img_gridSearchMoreLayout:
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        showPopupMenu(v);
                    }
                });
                break;
        }
    }


        private void showErrorMessage(){
            AlertDialog.Builder builder = new AlertDialog.Builder(Search_Activity.this);
            builder.setTitle("Data Error");
            builder.setMessage("Sorry - We have no videos for this - Please search again");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    downloadSearchVideo(ed_searchSearch.getText().toString());
                }
            });

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

        private boolean checkJsonValidity(FeaturedVideo fvideo){
            if(fvideo.getVideoType().isEmpty()
                    || fvideo.getVideoName().isEmpty()
                    || fvideo.getVideoImage().isEmpty()
                    || fvideo.getBandName().isEmpty())
                return false;

            return true;
        }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "screen configuration changed " + newConfig.orientation);

        super.onConfigurationChanged(newConfig);
        if(isDownloadSucceed == true) {
            ly_searchMessage.setVisibility(View.GONE);
            gridView_search.setVisibility(View.VISIBLE);
        }else{
            ly_searchMessage.setVisibility(View.VISIBLE);
            gridView_search.setVisibility(View.GONE);
        }

        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            gridView_search.setNumColumns(2);
            gridView_search.setHorizontalSpacing(20);
            gridView_search.setVerticalSpacing(20);
        }else{
            gridView_search.setNumColumns(1);
            gridView_search.setHorizontalSpacing(0);
            gridView_search.setVerticalSpacing(20);
        }
    }

    private void showPopupMenu(View view) {
        // Retrieve the clicked item from view's tag
        final FeaturedVideo item = (FeaturedVideo) view.getTag();

        Context wrapper = new ContextThemeWrapper(this, R.style.AppThemeCustom);
        PopupMenu popup = new PopupMenu(wrapper, view);

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.menu_video_list, popup.getMenu());
        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_one:
                        shareOnFacebook(item);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d(TAG, "onActivityResult called");
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Create the chromecast button
        MenuItem castButton = menu.add(Menu.NONE, R.id.media_route_menu_item, Menu.NONE, R.string.ccl_media_route_menu_title);
        // Make the button always visible
        MenuItemCompat.setShowAsAction(castButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        // Set the action provider to MediaRouteActionProvider
        MenuItemCompat.setActionProvider(castButton, new ThemeableMediaRouteActionProvider(this));
        // Register the MediaRouterButton on the JW Player SDK
        CastManager.getInstance().addMediaRouterButton(menu, R.id.media_route_menu_item);

        return true;
    }

    public void shareOnFacebook(FeaturedVideo item) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            makeBranchMessage(item);
        }
    }

    FeaturedVideo selectedVideo;
    private void makeBranchMessage(FeaturedVideo video) {
        selectedVideo = video;
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
                .setCanonicalIdentifier(video.getVideoID())
                        // The canonical URL for SEO purposes (optional)
                .setCanonicalUrl("https://branch.io/deepviews")
                        // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                .setTitle("Baeble Music Videos : " + video.getVideoName())
                .setContentDescription(video.getVideoDescription())
                .setContentImageUrl(video.getVideoImage())
                        // You use this to specify whether this content can be discovered publicly - default is public
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                        // Here is where you can add custom keys/values to the deep link data
                .addContentMetadata("videotype", video.getVideoType())
                .addContentMetadata("videoid", video.getVideoID());

        branchUniversalObject.registerView();

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", video.getPageURL());
//                .addControlParameter("$ios_url", "https://itunes.apple.com/us/app/baeble-music-videos/id1070313507?mt=8")
//                .addControlParameter("$android_url", "https://play.google.com/store/apps/details?id=com.baeble.www.baebleapp");

        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {

                if (error == null) {
                    //Toast.makeText(getApplicationContext(), "onLinkCreate() called url= " + url, Toast.LENGTH_LONG).show();
                    makeShareContent(selectedVideo, url);
                } else {
                    //Toast.makeText(getApplicationContext(), "onLinkCreate() called " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void makeShareContent(FeaturedVideo item, String branchURL) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(branchURL != null ? branchURL : "https://developers.facebook.com"))
                .setContentTitle(item.getVideoName())
                .setContentDescription(item.getVideoShortDescription())
                .setImageUrl(Uri.parse(item.getVideoImage()))
                .build();
        shareDialog.show(content);
    }
}
