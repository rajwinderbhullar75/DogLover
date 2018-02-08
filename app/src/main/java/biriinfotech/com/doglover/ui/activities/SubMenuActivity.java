package biriinfotech.com.doglover.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import biriinfotech.com.doglover.R;
import biriinfotech.com.doglover.RetrofitClient;
import biriinfotech.com.doglover.controller.ApiInterface;
import biriinfotech.com.doglover.controller.Config;
import biriinfotech.com.doglover.model.MenuModel;
import biriinfotech.com.doglover.model.ResponsePojo;
import biriinfotech.com.doglover.ui.adapters.MenuAdapter;
import biriinfotech.com.doglover.ui.customviews.CallProgressWheel;
import biriinfotech.com.doglover.ui.customviews.EllipsizingTextView;
import biriinfotech.com.doglover.utils.Constants;
import biriinfotech.com.doglover.utils.Utility;
import retrofit.Callback;
import retrofit.RetrofitError;

public class SubMenuActivity extends BaseActivity {

    private static final String LOG_TAG = "SubMenuActivity";
//    private CollapsingToolbarLayout mnToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRvMain;
    private ImageView mIvMenuImage;
    private TextView mTvMenuItemTitle;
    private TextView mTvMenuItemDescription;
    private ProgressBar mHomeProgress;
MenuModel mMenuModel;
    @Override
    AppCompatActivity setInstance() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu);
        mMenuModel= (MenuModel) getIntent().getSerializableExtra(Constants.POJO);
        EllipsizingTextView.maxLines=5;
    }

    @Override
    void setUpToolbar() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
//        mnToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mnToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mMenuModel.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mnToolbarLayout.setTitle(getIntent().getStringExtra(Constants.MENU_TITLE));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
//                    showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
//                    hideOption(R.id.action_info);
                }
            }
        });
    }

    private void hideOption(int id) {
    }

    private void showOption(int id) {
    }

    @Override
    void setUpLayout() {
        mRvMain = (RecyclerView) findViewById(R.id.rv_main);
        mRvMain.setLayoutManager(new LinearLayoutManager(this));
        mIvMenuImage=(ImageView) findViewById(R.id.iv_menu_image);
        mTvMenuItemTitle=(TextView) findViewById(R.id.menu_item_title);
        mTvMenuItemDescription=(TextView) findViewById(R.id.tv_menu_item_description);
        mHomeProgress=(ProgressBar) findViewById(R.id.homeprogress);


    }

    @Override
    void setDataInViewObjects() {
        mTvMenuItemTitle.setText(mMenuModel.getTitle());
        mTvMenuItemDescription.setText(mMenuModel.getDescription().toString().trim());

        Glide.with(this)
                .load(Config.getBaseURL()+"/"+mMenuModel.getImage())
//                .placeholder(R.drawable.loading_spinner)
//                .into(mIvMenuImage);
                .listener(new RequestListener<Drawable>() {


                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mHomeProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mIvMenuImage);
        getSubMenuData(mMenuModel.getCategory_id());
    }


    private void getSubMenuData(String menuId) {
        try {
            ApiInterface mWebApi = RetrofitClient.createService(ApiInterface.class, this);
            CallProgressWheel.showLoadingDialog(this, "Loading...");

            mWebApi.getSubMenuData(menuId, new Callback<ResponsePojo>() {
                @Override
                public void success(ResponsePojo commonResponse, retrofit.client.Response response) {

                    Log.d(LOG_TAG, "Success " + commonResponse.toString());

                    try {
                        if (commonResponse.isSuccess()) {
                            mRvMain.setAdapter(new MenuAdapter(SubMenuActivity.this,commonResponse.getData(),mMenuModel.getPosition()));
                        } else {
                            Utility.showToastMessageShort(SubMenuActivity.this, commonResponse.getMsg().toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CallProgressWheel.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    try {
//                            Utility.showToastMessageShort(LoginSignUpActivity.this, getResources().getString(R.string.server_error));
                        Utility.showToastMessageShort(SubMenuActivity.this, error.getMessage().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CallProgressWheel.dismissLoadingDialog();
                }
            });
        } catch (Exception e) {
            Utility.showException(SubMenuActivity.this, e);
        }


    }

}