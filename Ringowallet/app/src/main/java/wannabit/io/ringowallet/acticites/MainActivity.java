package wannabit.io.ringowallet.acticites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.dialog.BottomDialog_New_KeyPair;
import wannabit.io.ringowallet.dialog.Dialog_No_Mnemonic;
import wannabit.io.ringowallet.fragments.MainFavoFragment;
import wannabit.io.ringowallet.fragments.MainPriceFragment;
import wannabit.io.ringowallet.fragments.MainSettingFragment;
import wannabit.io.ringowallet.fragments.MainWalletFragment;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Price;
import wannabit.io.ringowallet.model.Token;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.res.ResPrice;
import wannabit.io.ringowallet.task.GenerateKeyTask;
import wannabit.io.ringowallet.task.InitWalletTask;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.balance.BalanceCheckByKeyTask;
import wannabit.io.ringowallet.task.balance.BalanceCheckFactory;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;
import wannabit.io.ringowallet.views.BottomSheetAdapter;
import wannabit.io.ringowallet.views.BottomSheetListener;

public class MainActivity extends BaseActivity implements TaskCallback, BottomSheetListener {

    private AppBarLayout                    mAppbar;
    private CollapsingToolbarLayout         mCollapsingToolbarLayout;
    private Toolbar                         mToolbar;
    private TextView                        mWalletTitle, mTotalBalanceTv;

    private AHBottomNavigationViewPager     mViewPager;
    private TabLayout                       mTabLayer;
    private FloatingActionButton            mFloatingActionButton;

    private FrameLayout                     mBottomSheet;
    private SearchView                      mBottomSearchView;
    private RecyclerView                    mBottomRecycler;

    private MainPageAdapter                 mPageAdapter;
    private BottomSheetBehavior             mBottomSheetBehavior;
    private BottomSheetAdapter              mBottomSheetAdapter;

    private BigDecimal                      mTotalValue = BigDecimal.ZERO;
    public ArrayList<WalletItem>           mWalletItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppbar                     = findViewById(R.id.app_bar);
        mCollapsingToolbarLayout    = findViewById(R.id.collapse_layer);
        mToolbar                    = findViewById(R.id.tool_bar);
        mWalletTitle                = findViewById(R.id.wallet_title);
        mTotalBalanceTv             = findViewById(R.id.toolbar_total_balance);
        mViewPager                  = findViewById(R.id.view_pager);
        mTabLayer                   = findViewById(R.id.bottom_tab);
        mBottomSheet                = findViewById(R.id.bottom_sheet);
        mBottomSearchView           = findViewById(R.id.bottom_searchView);
        mBottomRecycler             = findViewById(R.id.bottom_sheet_recycler);
        mFloatingActionButton       = findViewById(R.id.btn_floating);

        mWalletTitle.setTypeface(WUtils.getTypefaceRegular(this));
        mTotalBalanceTv.setTypeface(WUtils.getTypefaceRegular(this));
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(WUtils.getTypefaceRegular(this));
        mCollapsingToolbarLayout.setExpandedTitleTypeface(WUtils.getTypefaceRegular(this));
        setSupportActionBar(mToolbar);


        mPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mPageAdapter);
        mTabLayer.setupWithViewPager(mViewPager);

        mTabLayer.getTabAt(0).setIcon(R.drawable.ic_favorite_btn_off);
        mTabLayer.getTabAt(1).setIcon(R.drawable.ic_wallet_btn_off);
        mTabLayer.getTabAt(2).setIcon(R.drawable.ic_coinprice_btn_off);
        mTabLayer.getTabAt(3).setIcon(R.drawable.ic_settings_btn_off);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageScrollStateChanged(int i) { }

            @Override
            public void onPageSelected(int position) {
                onUpdateTitle();
                if(position != 1) {
                    mAppbar.setExpanded(false);
                    if (mFloatingActionButton.isShown()) mFloatingActionButton.hide();
                } else {
                    mAppbar.setExpanded(true);
                    if (!mFloatingActionButton.isShown()) mFloatingActionButton.show();
                }
            }
        });
        mViewPager.setCurrentItem(1, false);


        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isCollapsed = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) scrollRange = appBarLayout.getTotalScrollRange();
                mWalletTitle.setAlpha((float)(scrollRange + verticalOffset)/(float)scrollRange);

            }
        });


        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mBottomSheetAdapter.getFilter().filter("");
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (mBottomSearchView != null) {
                            mBottomSearchView.setQuery("", false);
                            mBottomSearchView.clearFocus();
                            mBottomSearchView.onActionViewCollapsed();
                        }
                        if (!mFloatingActionButton.isShown()) mFloatingActionButton.show();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });

        mBottomRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBottomRecycler.setHasFixedSize(true);
        mBottomSheetAdapter = new BottomSheetAdapter(getBaseApplication(), this, Glide.with(this));
        mBottomRecycler.setAdapter(mBottomSheetAdapter);




        mBottomSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBottomSheetAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mBottomSheetAdapter.getFilter().filter(query);
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!getBaseDao().hasAnyData()) {
            finish();
        }
        if(!getBaseDao().hasAnyKey()) {
            onShowWaitDialog();
            new InitWalletTask(getBaseApplication(), this).execute();
        } else {
            onFetchKeys();
            onCheckCoinPrice();
            onFetchBalance();
        }
    }


    public void onFetchKeys() {
        WLog.w("onFetchKeys");
        mWalletItems = getBaseDao().getInitWalletItems();
        ((MainWalletFragment)mPageAdapter.getFragments().get(1)).onSetWallets(mWalletItems);
        onUpdateTotalBalance();
    }

    private void onFetchBalance() {
        for (WalletItem walletItem : mWalletItems) {
//            BalanceCheckFactory.getBalance(getBaseApplication(), this, walletItem.keys).execute();
            BalanceCheckByKeyTask task = BalanceCheckFactory.getBalance(getBaseApplication(), this, walletItem.keys);
            if(task != null) task.execute();
        }
    }


    protected void onUpdateTitle() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                mCollapsingToolbarLayout.setTitle(getString(R.string.title_favorite));
                break;
            case 1:
                mCollapsingToolbarLayout.setTitle(WUtils.getDpAllValue(getBaseApplication(), mTotalValue));
                break;
            case 2:
                mCollapsingToolbarLayout.setTitle(getString(R.string.title_price));
                break;
            case 3:
                mCollapsingToolbarLayout.setTitle(getString(R.string.title_setting));
                break;
        }

    }

    public void onUpdateTotalBalance() {
        mTotalValue = BigDecimal.ZERO;
        for(WalletItem wallet : mWalletItems) {
            mTotalValue = mTotalValue.add(WUtils.getTotalWalletValue(getBaseApplication(), wallet, getBaseDao().onSelectPriceBySymbol(wallet.symbol)));
        }
        if(!isFinishing() && mViewPager.getCurrentItem() == 1) onUpdateTitle();

    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSelectItem(final Bundle bundle) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                BottomDialog_New_KeyPair bottomSheetDialog = BottomDialog_New_KeyPair.getInstance();
                bottomSheetDialog.setArguments(bundle);
                bottomSheetDialog.show(getSupportFragmentManager(), "dialog");
            }
        }, 200);
    }

    public void onShowHideBottomSheet(boolean toShow) {
        if(toShow) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public FloatingActionButton getFloatingBtn() {
        return mFloatingActionButton;
    }

    @Override
    public void onTaskResponse(TaskResult result) {
        if(isFinishing()) return;
        onHideWaitDialog();
        if (result.taskType == BaseConstant.TASK_INIT_WALLET) {
            if(result.isSuccess) {
                onCheckCoinPrice();
                Toast.makeText(this, R.string.msg_wallet_made_success, Toast.LENGTH_SHORT).show();
                onFetchKeys();
            } else { }

        } else if (result.taskType == BaseConstant.TASK_INSERT_GENERATE_WITH_MNEMONIC) {
            if(result.isSuccess) {
                Toast.makeText(this, R.string.msg_add_key_success, Toast.LENGTH_SHORT).show();
                onFetchKeys();
            } else { }

        } else if (result.taskType == BaseConstant.TASK_BALANCE) {
            if(result.isSuccess) {
                onFetchKeys();
            } else { }
        }
    }

    public void onGenerateKeyPairWithMnemonic(Bundle bundle) {
        if(getBaseDao().onHasMnemonic()) {
            onShowWaitDialog();
            new GenerateKeyTask(getBaseApplication(), this).execute(bundle.getString("type"), bundle.getString("symbol"));

        } else {
            Dialog_No_Mnemonic noMnemonic = Dialog_No_Mnemonic.newInstance();
            noMnemonic.setCancelable(true);
            noMnemonic.show(getSupportFragmentManager(), "dialog");
        }
    }

    public void onInsertNewPrivateKey(Bundle bundle) {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        intent.putExtra(BaseConstant.CONST_CREATE_PURPOSE, BaseConstant.CONST_CREATE_PURPOSE_ADD_KEY);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    public void onInitMnemoncis() {
        Intent intent = new Intent(this, CreateWalletActivity.class);
        intent.putExtra(BaseConstant.CONST_CREATE_PURPOSE, BaseConstant.CONST_CREATE_PURPOSE_MNEMONIC);
        startActivity(intent);
    }


    private class MainPageAdapter extends FragmentPagerAdapter {

        private ArrayList<BaseFragment> mFragments = new ArrayList<>();
        private BaseFragment mCurrentFragment;

        public MainPageAdapter(FragmentManager fm) {
            super(fm);
            mFragments.clear();
            mFragments.add(MainFavoFragment.newInstance());
            mFragments.add(MainWalletFragment.newInstance());
            mFragments.add(MainPriceFragment.newInstance());
            mFragments.add(MainSettingFragment.newInstance());
        }

        @Override
        public BaseFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((BaseFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        public BaseFragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public ArrayList<BaseFragment> getFragments() {
            return mFragments;
        }
    }


    private void onCheckCoinPrice() {
        for(int i = 0; i < mWalletItems.size(); i++) {
            final WalletItem walletItem = mWalletItems.get(i);
            ApiClient.getCryptoCompareService(this).getTokenPrice(walletItem.symbol, "USD,KRW").enqueue(new Callback<ResPrice>() {
                @Override
                public void onResponse(Call<ResPrice> call, Response<ResPrice> response) {
                    if(response.isSuccessful()) {
                        try {
                            Price price = new Price(walletItem.symbol, response.body().USD.toString(), response.body().KRW.toString());
                            getBaseDao().onInsertPrice(price);
                        } catch (Exception e) {
                            Price price = new Price(walletItem.symbol, "0", "0");
                            getBaseDao().onInsertPrice(price);
                        }

                    }  else {

                    }
                }

                @Override
                public void onFailure(Call<ResPrice> call, Throwable t) { }
            });
        }
    }
}

