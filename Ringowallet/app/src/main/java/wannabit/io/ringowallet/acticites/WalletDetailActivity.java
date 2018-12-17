package wannabit.io.ringowallet.acticites;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.fragments.MainPriceFragment;
import wannabit.io.ringowallet.fragments.WalletDetailFragment;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;
import wannabit.io.ringowallet.views.KKViewPager;
import wannabit.io.ringowallet.views.LinePageIndicator;
import wannabit.io.ringowallet.views.UnderlinePageIndicator;

public class WalletDetailActivity extends BaseActivity {

    private Toolbar                     mToolbar;
    private TextView                    mTitle;

    private KKViewPager                 mViewPager;
    private UnderlinePageIndicator      mLinePageIndicator;

    private String                      mIntentSymbol, mIntentType, mIntentContractAddr, mIntentPage;
    private WalletPageAdapter           mAdapter;
    private ArrayList<Key>              mKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);
        mToolbar                    = findViewById(R.id.tool_bar);
        mTitle                      = findViewById(R.id.toolbar_title);
        mViewPager                  = findViewById(R.id.view_pager);
        mLinePageIndicator          = findViewById(R.id.indicator);

        mIntentSymbol = getIntent().getStringExtra("symbol");
        mIntentType = getIntent().getStringExtra("type");
        if(!WUtils.isMainCoin(mIntentSymbol)) {
            mIntentContractAddr = getIntent().getStringExtra("contractAddr");
        }
        mIntentPage = getIntent().getStringExtra("page");

        mTitle.setTypeface(WUtils.getTypefaceRegular(this));
        mTitle.setText(mIntentSymbol);

        mKeys = getBaseDao().onSelectKeysByType(mIntentType, mIntentSymbol);
        mAdapter = new WalletPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mLinePageIndicator.setViewPager(mViewPager);
        mViewPager.setPageMargin(42);
        mViewPager.setAnimationEnabled(true);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageScrollStateChanged(int i) { }

            @Override
            public void onPageSelected(int i) {
                WLog.w("onPageSelected : " + i);
            }
        });
        if(!TextUtils.isEmpty(mIntentPage)) {
            int initPage = 0;
            for(int i = 0 ; i < mKeys.size(); i++) {
                if(mKeys.get(i).uuid.equals(mIntentPage)) {
                    initPage = i;
                    break;
                }
            }
            mViewPager.setCurrentItem(initPage, false);

        } else {
            mViewPager.setCurrentItem(0, false);
        }

    }


    private class WalletPageAdapter extends FragmentPagerAdapter {

        public WalletPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseFragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putInt("size", mKeys.size());
            if(!WUtils.isMainCoin(mIntentSymbol)) {
                bundle.putString("contractAddr", mIntentContractAddr);
            }
            WalletDetailFragment instance = WalletDetailFragment.newInstance(bundle);
            instance.setKey(mKeys.get(position));
            return instance;
        }


        @Override
        public int getCount() {
            return mKeys.size();
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }
}
